package com.sillypantscoder.spcgames;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

import com.sillypantscoder.spcgames.Game.ActiveGame;
import com.sillypantscoder.spcgames.Game.StaticGame;
import com.sillypantscoder.spcgames.GameInfo.ActiveGameInfo;
import com.sillypantscoder.spcgames.games.ColtSuperExpress;
import com.sillypantscoder.spcgames.games.GeometryDash;
import com.sillypantscoder.spcgames.games.PixelRaceGame;
import com.sillypantscoder.spcgames.games.PlanetBomber;
import com.sillypantscoder.spcgames.games.SwapGame;
import com.sillypantscoder.spcgames.games.TheForeheadGame;
import com.sillypantscoder.spcgames.games.VotingGame;
import com.sillypantscoder.spcgames.http.HttpResponse;
import com.sillypantscoder.spcgames.http.RequestHandler;

/**
 * The main HTTP handler used for the server.
 */
public class GameHandler extends RequestHandler {
	public static ActiveGameInfo[] activeGameTypes = new ActiveGameInfo[] {
		ColtSuperExpress.getInfo(),
		VotingGame.getInfo()
	};
	public ArrayList<ActiveGame> games;
	public ArrayList<StaticGame> staticGames;
	public GameHandler() {
		this.games = new ArrayList<ActiveGame>();
		this.staticGames = new ArrayList<StaticGame>();
		this.staticGames.add(new GeometryDash());
		this.staticGames.add(new PixelRaceGame());
		this.staticGames.add(new PlanetBomber());
		this.staticGames.add(new SwapGame());
		this.staticGames.add(new TheForeheadGame());
	}
	public HttpResponse get(String path) {
		for (int i = 0; i < games.size(); i++) {
			Game g = games.get(i);
			if (! g.stillValid()) {
				games.remove(g);
				i -= 1;
				continue;
			}
		}
		if (path.equals("/")) {
			return new HttpResponse().setStatus(200).addHeader("Content-Type", "text/html").setBody(AssetLoader.getResource("assets/server_files/index.html"));
		} else if (path.equals("/gamelist/active")) {
			ArrayList<String[]> info = new ArrayList<String[]>();
			for (int i = 0; i < games.size(); i++) {
				ActiveGame g = games.get(i);
				info.add(new String[] {
					g.info.getName(),
					g.name,
					g.id,
					String.valueOf(g.deletionTime)
				});
			}
			String body = "";
			for (int i = 0; i < info.size(); i++) {
				if (i != 0) body += "\n";
				body += String.join("|||", info.get(i));
			}
			return new HttpResponse().setStatus(200).addHeader("Content-Type", "text/plain").setBody(body);
		} else if (path.equals("/gamelist/static")) {
			ArrayList<String[]> info = new ArrayList<String[]>();
			for (int i = 0; i < staticGames.size(); i++) {
				StaticGame g = staticGames.get(i);
				info.add(new String[] {
					g.info.getName(),
					g.info.getID()
				});
			}
			String body = "";
			for (int i = 0; i < info.size(); i++) {
				if (i != 0) body += "\n";
				body += String.join("|||", info.get(i));
			}
			return new HttpResponse().setStatus(200).addHeader("Content-Type", "text/plain").setBody(body);
		} else if (path.equals("/create")) {
			return new HttpResponse().setStatus(200).addHeader("Content-Type", "text/html").setBody(AssetLoader.getResource("assets/server_files/create.html"));
		} else if (path.startsWith("/host/main/")) {
			String name = path.split("/")[3];
			for (int i = 0; i < games.size(); i++) {
				ActiveGame g = games.get(i);
				if (g.id.equals(name)) {
					return new HttpResponse()
						.setStatus(200)
						.addHeader("Content-Type", "text/html")
						.setBody(
							AssetLoader.getResource("assets/server_files/host.html")
								.replaceAll("XXGAMENAME", g.name)
								.replaceAll("XXGAMETYPENAME", g.info.getName())
						);
				}
			}
		} else if (path.startsWith("/host/delete/")) {
			String name = path.split("/")[3];
			for (int i = 0; i < games.size(); i++) {
				if (games.get(i).id.equals(name)) {
					games.get(i).remove();
					games.remove(i);
					return new HttpResponse().setStatus(200);
				}
			}
		} else if (path.startsWith("/host/data/")) {
			String name = path.split("/")[3];
			for (int i = 0; i < games.size(); i++) {
				if (games.get(i).id.equals(name)) {
					return new HttpResponse()
						.setStatus(200)
						.addHeader("Content-Type", "text/html")
						.setBody(games.get(i).getModStatus());
				}
			}
		} else if (path.startsWith("/game/")) {
			String name = path.split("/")[2];
			String gamePath = path.substring("/game/".length() + name.length());
			for (int i = 0; i < games.size(); i++) {
				if (games.get(i).id.equals(name)) {
					return games.get(i).get(gamePath);
				}
			}
		} else if (path.startsWith("/game_static/")) {
			String name = path.split("/")[2];
			String gamePath = path.substring("/game_static/".length() + name.length());
			for (int i = 0; i < staticGames.size(); i++) {
				if (staticGames.get(i).info.getID().equals(name)) {
					return staticGames.get(i).get(gamePath);
				}
			}
		} else if (path.startsWith("/create_game/")) {
			String type = path.split("\\?")[0].split("/")[2];
			String newname = URLDecoder.decode(path.split("\\?")[1], StandardCharsets.UTF_8);
			for (ActiveGameInfo info : activeGameTypes) {
				if (info.getID().equals(type)) {
					// Create a new game of this type
					ActiveGame newGame = info.create(newname);
					this.games.add(newGame);
					return new HttpResponse().setStatus(200).addHeader("Content-Type", "text/plain").setBody(newGame.id);
				}
			}
		} else if (path.startsWith("/info/active/")) {
			String type = path.split("/")[3];
			for (GameInfo info : activeGameTypes) {
				if (info.getID().equals(type)) {
					return new HttpResponse()
						.setStatus(200)
						.addHeader("Content-Type", "text/html")
						.setBody("<!DOCTYPE html><html><head><style>a{color:rgb(0,0,200);}</style></head><body><a href=\"/\">Back Home</a><h3>" + info.getName() + "</h3><p>" + info.getLongDescription().replaceAll("<", "&lt;").replaceAll("\n", "</p><p>") + "</p></body></html>");
				}
			}
		} else if (path.startsWith("/info/static/")) {
			String type = path.split("/")[3];
			for (StaticGame game : staticGames) {
				GameInfo info = game.info;
				if (info.getID().equals(type)) {
					return new HttpResponse()
						.setStatus(200)
						.addHeader("Content-Type", "text/html")
						.setBody("<!DOCTYPE html><html><head><style>a{color:rgb(0,0,200);}</style></head><body><a href=\"/\">Back Home</a><h3>" + info.getName() + "</h3><p>" + info.getLongDescription().replaceAll("<", "&lt;").replaceAll("\n", "</p><p>") + "</p></body></html>");
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
		} else if (path.startsWith("/game_static/")) {
			String name = path.split("/")[2];
			String gamePath = path.substring("/game_static/".length() + name.length());
			for (int i = 0; i < staticGames.size(); i++) {
				if (staticGames.get(i).info.getID().equals(name)) {
					return staticGames.get(i).post(gamePath, body);
				}
			}
		} else if (path.equals("/mark_delete")) {
			for (int i = 0; i < games.size(); i++) {
				Game game = games.get(i);
				if (game.id.equals(body)) {
					if (game.deletionTime == 0) games.get(i).deletionTime = new Date().getTime() + (30 * 60 * 1000l);
					else game.deletionTime = 0;
					return new HttpResponse();
				}
			}
		}
		System.err.println("Error for POST request: " + path);
		return new HttpResponse().setStatus(404);
	}
}
