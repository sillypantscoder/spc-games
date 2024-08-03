package com.sillypantscoder.spcgames.games;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sillypantscoder.spcgames.GameInfo;
import com.sillypantscoder.spcgames.Subprocess;
import com.sillypantscoder.spcgames.WebProcess;
import com.sillypantscoder.spcgames.Game.ActiveGame;
import com.sillypantscoder.spcgames.http.HttpResponse;

public class ColtSuperExpress extends ActiveGame {
	public WebProcess process;
	public ColtSuperExpress(String name) {
		super(getInfo(), name);
		process = new WebProcess(new Subprocess(new String[] {"python3", "fakeserver.py"}, "../coltsuperexpress"));
	}
	public static GameInfo.ActiveGameInfo getInfo() {
		return new GameInfo.ActiveGameInfo() {
			public String getName() {
				return "Colt Super Express";
			}
			public String getShortDescription() {
				return "Shoot each other while on a train.";
			}
			public String getLongDescription() {
				return "In this game, you all are on a train and you are trying to get all the other players off the train. By shooting them. Each round, you have 5 cards: walk forwards, turn around, change level, shoot, and revenge. You will choose 3 of the cards, in order, to play during this round. Once everyone has chosen their cards, the round starts. The first player reveals their first card, and plays it. Then the second player reveals their first card, and plays it. Once everyone has revealed their first card, everyone reveals their second card, and so on. After everyone has run out of cards, the round ends. One train car falls off the end and a new round begins. Once there is only one person left on the train, that person is declared the winner.";
			}
			public String getID() {
				return "coltsuperexpress";
			}
			public ColtSuperExpress create(String name) {
				return new ColtSuperExpress(name);
			}
		};
	}
	public HttpResponse get(String path) {
		return process.get(path);
	}
	public HttpResponse post(String path, String body) {
		return process.post(path, body);
	}
	public void remove() {
		process.destroy();
	}
	public String getStatus() {
		String status_full = this.get("/status").bodyString();
		Matcher matcher = Pattern.compile("\"status\": \"([\\w]+)\"").matcher(status_full);
		if (matcher.find()) {
			String status = matcher.group(1);
			if (status.equals("joining")) return "Joining!";
			else if (status.equals("schemin")) return "In progress";
			else if (status.equals("executing")) return "In progress";
			else if (status.equals("finished")) return "Finished";
			else return "<u onclick='alert(\"" + status + "\")'>???</u>";
		} else {
			return "???";
		}
	}
	public String getModStatus() {
		return this.get("/status_mod").bodyString();
	}
}
