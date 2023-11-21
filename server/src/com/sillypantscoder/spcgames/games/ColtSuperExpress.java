package com.sillypantscoder.spcgames.games;

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
}
