// package com.sillypantscoder.spcgames.games;

// import com.sillypantscoder.spcgames.GameType;
// import com.sillypantscoder.spcgames.Subprocess;

// public class PixelRaceGame extends GameType.StaticGameType {
// 	public String getName() {
// 		return "Pixel Race";
// 	}
// 	public String getDescription() {
// 		return "";
// 	}
// 	public String getID() {
// 		return "pixelrace";
// 	}
// 	public Subprocess getProcess() {
// 		String files = "{\"name\": \"pixelrace\", \"files\": [{\"url\": \"/editor/editor.html\", \"filepath\": \"pixelracing/editor/editor.html\", \"type\": \"text/html\"}, {\"url\": \"/editor/editor.js\", \"filepath\": \"pixelracing/editor/editor.js\", \"type\": \"text/javascript\"}, {\"url\": \"/game/astar2.js\", \"filepath\": \"pixelracing/game/astar2.js\", \"type\": \"text/javascript\"}, {\"url\": \"/game/game.html\", \"filepath\": \"pixelracing/game/game.html\", \"type\": \"text/html\"}, {\"url\": \"/game/game.js\", \"filepath\": \"pixelracing/game/game.js\", \"type\": \"text/javascript\"}, {\"url\": \"/gameonline/gameonline.html\", \"filepath\": \"pixelracing/gameonline/gameonline.html\", \"type\": \"text/html\"}, {\"url\": \"/gameonline/online.js\", \"filepath\": \"pixelracing/gameonline/online.js\", \"type\": \"text/javascript\"}, {\"url\": \"/gameload.html\", \"filepath\": \"pixelracing/gameload.html\", \"type\": \"text/html\"}, {\"url\": \"/\", \"filepath\": \"pixelracing/index.html\", \"type\": \"text/html\"}, {\"url\": \"/levels.json\", \"filepath\": \"pixelracing/levels.json\", \"type\": \"application/json\"}, {\"url\": \"/main.css\", \"filepath\": \"pixelracing/main.css\", \"type\": \"text/css\"}, {\"url\": \"/main.js\", \"filepath\": \"pixelracing/main.js\", \"type\": \"text/javascript\"}, {\"url\": \"/query.js\", \"filepath\": \"pixelracing/query.js\", \"type\": \"text/javascript\"}], \"folders\": []}";
// 		return new Subprocess(new String[] {"bash", "-c", "python3 fileserver.py '" + files + "'"}, "..");
// 	}
// }
