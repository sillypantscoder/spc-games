package com.sillypantscoder.spcgames.games;

import com.sillypantscoder.spcgames.GameType;
import com.sillypantscoder.spcgames.Subprocess;

public class SwapGame extends GameType.StaticGameType {
	public String getName() {
		return "Swap Game";
	}
	public String getDescription() {
		return "There is a grid of squares, each of which is either \"filled\" or \"empty\". You are the blue triangle and you're trying to get to the goal, which is the green triangle. You can use the arrow keys to move left and right, and jump. You can only jump 1 block high. Use the down arrow key to fall.\n\nIn addition, if you divide the world into 2x2 chunks, you can swap any two chunks at any time. (Even when you are in mid-air!) However, you can't move the chunk you're in, or the chunk the goal is in.\n\nUse the arrow keys to move the player. Click (or tap) on two chunks to swap them. On mobile, you can move the player by swiping on the player.";
	}
	public String getID() {
		return "swapgame";
	}
	public Subprocess getProcess() {
		String[] files = new String[] {
			"{\"url\":\"/\",\"filepath\":\"stuff/swapgame/index.html\",\"type\":\"text/html\"}",
			"{\"url\":\"/game2.xml\",\"filepath\":\"stuff/swapgame/game2.xml\",\"type\":\"image/svg+xml\"}",
			"{\"url\":\"/confetti_mod.js\",\"filepath\":\"stuff/swapgame/confetti_mod.js\",\"type\":\"text/javascript\"}"
		};
		return new Subprocess(new String[] {"python3", "fileserver.py", "{\"name\":\"swapgame\",\"files\":[" + String.join(",", files) + "]}"}, "..");
	}
}
