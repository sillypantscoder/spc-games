package com.sillypantscoder.spcgames.games;

import com.sillypantscoder.spcgames.Game.StaticGame;
import com.sillypantscoder.spcgames.GameInfo;
import com.sillypantscoder.spcgames.Utils;
import com.sillypantscoder.spcgames.http.HttpResponse;

public class PlanetBomber extends StaticGame {
	public PlanetBomber() {
		super(getInfo());
	}
	public static GameInfo getInfo() {
		return new GameInfo() {
			public String getName() {
				return "Planet Bomber";
			}
			public String getShortDescription() {
				return "A game where you need to destroy a planet using various bombs.";
			}
			public String getLongDescription() {
				return "You need to destroy the planet. At the bottom of the screen are all the bombs you can use. They slowly recharge over time. You can press the \"Use\" and \"Use all\" buttons to drop bombs on the planet. If you reach the center of the planet, you go to the next planet!";
			}
			public String getID() {
				return "planetbomber";
			}
		};
	}
	public HttpResponse get(String path) {
		if (path.equals("/")) return new HttpResponse().addHeader("Content-Type", "text/html").setBody(Utils.readFile("../stuff/planetbomber.html"));
		return new HttpResponse().setStatus(404);
	}
	public HttpResponse post(String path, String body) {
		return new HttpResponse().setStatus(404);
	}
}
