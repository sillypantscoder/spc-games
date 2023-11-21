package com.sillypantscoder.spcgames;

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
		if (path.equals("/")) return new HttpResponse().setStatus(200).setBody("Hiiiiiiii");
		else if (path.startsWith("/game/")) {
			String name = path.split("/")[2];
			String gamePath = path.substring(6 + name.length());
			for (int i = 0; i < games.size(); i++) {
				if (games.get(i).id.equals(name)) {
					return games.get(i).get(gamePath);
				}
			}
		} else if (path.startsWith("/create_game/")) {
			String type = path.split("/")[2];
			for (GameType info : new GameType[] {
				new ColtSuperExpress()
			}) {
				if (info.getID().equals(type)) {
					// Create a new game of this type
					Game newGame = new Game(info);
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
