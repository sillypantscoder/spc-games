package com.sillypantscoder.spcgames.games;

import com.sillypantscoder.spcgames.Game.StaticGame;
import com.sillypantscoder.spcgames.AssetLoader;
import com.sillypantscoder.spcgames.GameInfo;
import com.sillypantscoder.spcgames.http.HttpResponse;

public class SwapGame extends StaticGame {
	public SwapGame() {
		super(getInfo());
	}
	public static GameInfo getInfo() {
		return new GameInfo() {
			public String getName() {
				return "Swap Game";
			}
			public String getShortDescription() {
				return "Try to get to the goal by swapping parts of the world.";
			}
			public String getLongDescription() {
				return "There is a grid of squares, each of which is either \"filled\" or \"empty\". You are the blue triangle and you're trying to get to the goal, which is the green triangle. You can use the arrow keys to move left and right, and jump. You can only jump 1 block high. Use the down arrow key to fall.\n\nIn addition, if you divide the world into 2x2 chunks, you can swap any two chunks at any time. (Even when you are in mid-air!) However, you can't move the chunk you're in, or the chunk the goal is in.\n\nUse the arrow keys to move the player. Click (or tap) on two chunks to swap them. On mobile, you can move the player by swiping on the player.";
			}
			public String getID() {
				return "swapgame";
			}
		};
	}
	public HttpResponse get(String path) {
		if (path.equals("/")) return new HttpResponse().addHeader("Content-Type", "text/html").setBody(AssetLoader.getResource("assets/swapgame/index.html"));
		if (path.equals("/style.css")) return new HttpResponse().addHeader("Content-Type", "text/css").setBody(AssetLoader.getResource("assets/swapgame/style.css"));
		if (path.equals("/script.js")) return new HttpResponse().addHeader("Content-Type", "text/javascript").setBody(AssetLoader.getResource("assets/swapgame/script.js"));
		if (path.equals("/game2.xml")) return new HttpResponse().addHeader("Content-Type", "image/svg+xml").setBody(AssetLoader.getResource("assets/swapgame/game2.xml"));
		if (path.equals("/confetti_mod.js")) return new HttpResponse().addHeader("Content-Type", "text/javascript").setBody(AssetLoader.getResource("assets/swapgame/confetti_mod.js"));
		return new HttpResponse().setStatus(404);
	}
	public HttpResponse post(String path, String body) {
		return new HttpResponse().setStatus(404);
	}
}
