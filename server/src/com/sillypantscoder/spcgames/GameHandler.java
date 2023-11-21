package com.sillypantscoder.spcgames;

import java.util.ArrayList;

import com.sillypantscoder.spcgames.http.HttpResponse;
import com.sillypantscoder.spcgames.http.RequestHandler;

public class GameHandler extends RequestHandler {
	public ArrayList<Game> games;
	public GameHandler() {
		this.games = new ArrayList<Game>();
		games.add(new Game() {});
	}
	public HttpResponse get(String path) {
		// System.out.println(games.get(0));
		// System.out.println(games.get(0).get(path).toString());
		return games.get(0).get(path);
		// return new HttpResponse();
	}
	public HttpResponse post(String path, String body) {
		return new HttpResponse().setStatus(404);
	}
}
