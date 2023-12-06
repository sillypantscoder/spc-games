package com.sillypantscoder.spcgames.games;

import com.sillypantscoder.spcgames.GameType;
import com.sillypantscoder.spcgames.Subprocess;

public class TheForeheadGame extends GameType.StaticGameType {
	public String getName() {
		return "The Forehead Game";
	}
	public String getDescription() {
		return "";
	}
	public String getID() {
		return "foreheadgame";
	}
	public Subprocess getProcess() {
		return new Subprocess(new String[] {"python3", "fakeserver.py"}, "../theforeheadgame");
	}
}
