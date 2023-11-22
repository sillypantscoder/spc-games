package com.sillypantscoder.spcgames;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.sillypantscoder.spcgames.games.ColtSuperExpress;
import com.sillypantscoder.spcgames.http.HttpResponse;
import com.sillypantscoder.spcgames.http.RequestHandler;

public class GameHandler extends RequestHandler {
	public ArrayList<Game> games;
	public GameHandler() {
		this.games = new ArrayList<Game>();
	}
	public HttpResponse get(String path) {
		if (path.equals("/")) return new HttpResponse().setStatus(200).setBody(Utils.readFile("public_files/index.html"));
		else if (path.equals("/gamelist")) {
			ArrayList<String[]> info = new ArrayList<String[]>();
			for (int i = 0; i < games.size(); i++) {
				Game g = games.get(i);
				info.add(new String[] {
					g.type.getName(),
					g.name,
					g.id,
					g.getStatus()
				});
			}
			String body = "";
			for (int i = 0; i < info.size(); i++) {
				if (i != 0) body += "\n";
				body += String.join("|||", info.get(i));
			}
			return new HttpResponse().setStatus(200).setBody(body);
		} else if (path.startsWith("/game/")) {
			String name = path.split("/")[2];
			String gamePath = path.substring(6 + name.length());
			for (int i = 0; i < games.size(); i++) {
				if (games.get(i).id.equals(name)) {
					return games.get(i).get(gamePath);
				}
			}
		} else if (path.startsWith("/create_game/")) {
			String type = path.split("\\?")[0].split("/")[2];
			String newname = URLDecoder.decode(path.split("\\?")[1], StandardCharsets.UTF_8);
			for (GameType info : new GameType[] {
				new ColtSuperExpress()
			}) {
				if (info.getID().equals(type)) {
					// Create a new game of this type
					Game newGame = new Game(info, newname);
					this.games.add(newGame);
					return new HttpResponse().setStatus(200).addHeader("Content-Type", "text/plain").setBody(newGame.id);
				}
			}
		}
		System.err.println("Error for request: " + path);
		return new HttpResponse().setStatus(404);
	}
	public HttpResponse post(String path, String body) {
		if (path.startsWith("/game/")) {
			String name = path.split("/")[2];
			String gamePath = path.substring(6 + name.length());
			for (int i = 0; i < games.size(); i++) {
				if (games.get(i).id.equals(name)) {
					return games.get(i).post(gamePath, body);
				}
			}
		}
		System.err.println("Error for POST request: " + path);
		return new HttpResponse().setStatus(404);
	}
}
