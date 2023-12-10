package com.sillypantscoder.spcgames.games;

import com.sillypantscoder.spcgames.GameType;
import com.sillypantscoder.spcgames.Subprocess;

public class PlanetBomber extends GameType.StaticGameType {
	public String getName() {
		return "Planet Bomber";
	}
	public String getDescription() {
		return "At the bottom of the screen are all the bombs you can use. They slowly recharge over time. You can press the \"Use\" and \"Use all\" buttons to drop bombs on the planet. If you reach the center of the planet, you go to the next planet!";
	}
	public String getID() {
		return "planetbomber";
	}
	public Subprocess getProcess() {
		String[] files = new String[] {
			"{\"url\":\"/\",\"filepath\":\"stuff/planetbomber.html\",\"type\":\"text/html\"}"
		};
		return new Subprocess(new String[] {"python3", "fileserver.py", "{\"name\":\"planetbomber\",\"files\":[" + String.join(",", files) + "]}"}, "..");
	}
}
