package com.sillypantscoder.spcgames.games;

import com.sillypantscoder.spcgames.Game;
import com.sillypantscoder.spcgames.GameType;
import com.sillypantscoder.spcgames.Subprocess;

public class VotingGame extends GameType {
	public String getName() {
		return "The Voting Game";
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
		return "???";
	}
}
