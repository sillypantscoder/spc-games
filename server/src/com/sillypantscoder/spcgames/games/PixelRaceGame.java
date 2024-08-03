package com.sillypantscoder.spcgames.games;

import com.sillypantscoder.spcgames.Game.StaticGame;
import com.sillypantscoder.spcgames.GameInfo;
import com.sillypantscoder.spcgames.Utils;
import com.sillypantscoder.spcgames.http.HttpResponse;

public class PixelRaceGame extends StaticGame {
	public PixelRaceGame() {
		super(getInfo());
	}
	public static GameInfo getInfo() {
		return new GameInfo() {
			public String getName() {
				return "Pixel Race";
			}
			public String getShortDescription() {
				return "A game where you can race pixels.";
			}
			public String getLongDescription() {
				return "A game where you can race pixels.";
			}
			public String getID() {
				return "pixelrace";
			}
		};
	}
	public HttpResponse get(String path) {
		if (path.equals("/editor/editor.html")) return new HttpResponse().addHeader("Content-Type", "text/html").setBody(Utils.readFile("../pixelracing/editor/editor.html"));
		if (path.equals("/editor/editor.js")) return new HttpResponse().addHeader("Content-Type", "text/javascript").setBody(Utils.readFile("../pixelracing/editor/editor.js"));
		if (path.equals("/game/astar2.js")) return new HttpResponse().addHeader("Content-Type", "text/javascript").setBody(Utils.readFile("../pixelracing/game/astar2.js"));
		if (path.startsWith("/game/game.html")) return new HttpResponse().addHeader("Content-Type", "text/html").setBody(Utils.readFile("../pixelracing/game/game.html"));
		if (path.equals("/game/game.js")) return new HttpResponse().addHeader("Content-Type", "text/javascript").setBody(Utils.readFile("../pixelracing/game/game.js"));
		if (path.equals("/gameonline/gameonline.html")) return new HttpResponse().addHeader("Content-Type", "text/html").setBody(Utils.readFile("../pixelracing/gameonline/gameonline.html"));
		if (path.equals("/gameonline/online.js")) return new HttpResponse().addHeader("Content-Type", "text/javascript").setBody(Utils.readFile("../pixelracing/gameonline/online.js"));
		if (path.equals("/gameload.html")) return new HttpResponse().addHeader("Content-Type", "text/html").setBody(Utils.readFile("../pixelracing/gameload.html"));
		if (path.equals("/")) return new HttpResponse().addHeader("Content-Type", "text/html").setBody(Utils.readFile("../pixelracing/index.html"));
		if (path.equals("/levels.json")) return new HttpResponse().addHeader("Content-Type", "application/json").setBody(Utils.readFile("../pixelracing/levels.json"));
		if (path.equals("/main.css")) return new HttpResponse().addHeader("Content-Type", "text/css").setBody(Utils.readFile("../pixelracing/main.css"));
		if (path.equals("/main.js")) return new HttpResponse().addHeader("Content-Type", "text/javascript").setBody(Utils.readFile("../pixelracing/main.js"));
		if (path.equals("/query.js")) return new HttpResponse().addHeader("Content-Type", "text/javascript").setBody(Utils.readFile("../pixelracing/query.js"));
		return new HttpResponse().setStatus(404);
	}
	public HttpResponse post(String path, String body) {
		return new HttpResponse().setStatus(404);
	}
}
