package com.sillypantscoder.spcgames.games;

import com.sillypantscoder.spcgames.GameType;
import com.sillypantscoder.spcgames.Subprocess;

public class GeometryDash extends GameType.StaticGameType {
	public String getName() {
		return "Geometry Dash clone";
	}
	public String getDescription() {
		return "";
	}
	public String getID() {
		return "geometrydash";
	}
	public Subprocess getProcess() {
		return new Subprocess(new String[] {"python3", "fakeserver.py"}, "../geometrydash");
	}
}
