package com.sillypantscoder.spcgames.games;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sillypantscoder.spcgames.Game;
import com.sillypantscoder.spcgames.GameType;
import com.sillypantscoder.spcgames.Subprocess;

public class ColtSuperExpress extends GameType {
	public String getName() {
		return "Colt Super Express";
	}
	public String getID() {
		return "coltsuperexpress";
	}
	public Subprocess getProcess() {
		return new Subprocess(new String[] {"python3", "fakeserver.py"}, "../coltsuperexpress");
	}
	public String getStatus(Game game) {
		String status_full = game.get("/status").body;
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
}
