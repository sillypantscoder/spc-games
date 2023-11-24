package com.sillypantscoder.spcgames.games;

import com.sillypantscoder.spcgames.Game;
import com.sillypantscoder.spcgames.GameType;
import com.sillypantscoder.spcgames.Subprocess;

public class VotingGame extends GameType {
	public String getName() {
		return "The Voting Game";
	}
	public String getDescription() {
		return "Everyone starts with 0 points. There are a bunch of confusing options to vote for, such as \"Take -2 points from <player>\". Once everyone has voted, the options with the most votes get applied. In addition, the rules to the game are not static, and many of the options you can vote for are about adding or removing rules to the game.";
	}
	public String getID() {
		return "votegame";
	}
	public Subprocess getProcess() {
		return new Subprocess(new String[] {"python3", "compiler.py"}, "../votegame");
	}
	public String getStatus(Game game) {
		return "???";
	}
	public String getModStatus(Game game) {
		return "There are no options available for this game";
	}
}
