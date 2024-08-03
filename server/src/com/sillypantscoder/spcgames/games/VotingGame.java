package com.sillypantscoder.spcgames.games;

import com.sillypantscoder.spcgames.GameInfo;
import com.sillypantscoder.spcgames.Game.ActiveGame;
import com.sillypantscoder.spcgames.http.HttpResponse;

public class VotingGame extends ActiveGame {
	public com.sillypantscoder.votegame.Game game;
	public VotingGame(String name) {
		super(getInfo(), name);
		this.game = new com.sillypantscoder.votegame.Game();
	}
	public static GameInfo.ActiveGameInfo getInfo() {
		return new GameInfo.ActiveGameInfo() {
			public String getName() {
				return "The Voting Game";
			}
			public String getShortDescription() {
				return "Confuse your brain with some weird voting.";
			}
			public String getLongDescription() {
				return "Everyone starts with 0 points. There are a bunch of confusing options to vote for, such as \"Take -2 points from <player>\". Once everyone has voted, the options with the most votes get applied. In addition, the rules to the game are not static, and many of the options you can vote for are about adding or removing rules to the game.";
			}
			public String getID() {
				return "votegame";
			}
			public VotingGame create(String name) {
				return new VotingGame(name);
			}
		};
	}
	public HttpResponse get(String path) {
		return game.get(path);
	}
	public HttpResponse post(String path, String body) {
		return game.post(path, body);
	}
	public void remove() {}
	public String getStatus() {
		return game.get("/status").bodyString();
	}
	public String getModStatus() {
		return game.get("/status_mod").bodyString();
	}
}
