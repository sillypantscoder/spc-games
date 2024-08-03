package com.sillypantscoder.spcgames.games;

import com.sillypantscoder.spcgames.GameInfo;
import com.sillypantscoder.spcgames.Subprocess;
import com.sillypantscoder.spcgames.WebProcess;
import com.sillypantscoder.spcgames.http.HttpResponse;
import com.sillypantscoder.spcgames.Game.StaticGame;

public class TheForeheadGame extends StaticGame {
	public WebProcess process;
	public TheForeheadGame() {
		super(getInfo());
		process = new WebProcess(new Subprocess(new String[] {"python3", "fakeserver.py"}, "../theforeheadgame"));
	}
	public static GameInfo getInfo() {
		return new GameInfo() {
			public String getName() {
				return "The Forehead Game";
			}
			public String getShortDescription() {
				return "Try to guess a random word while everyone shouts clues at you.";
			}
			public String getLongDescription() {
				return "In this game, you hold up the phone to your forehead. The phone shows a random word chosen out of a list of words, and everyone else shouts clues at you about what the word is. (Everyone else can't use the word on screen.) If you get the word correct, tilt the phone down. If you decide to pass, tilt the screen upwards. When the time runs out, see how many you got!\nThere are a variety of word sets you can shoose from, or create your own word set!";
			}
			public String getID() {
				return "foreheadgame";
			}
		};
	}
	public HttpResponse get(String path) {
		return process.get(path);
	}
	public HttpResponse post(String path, String body) {
		return process.post(path, body);
	}
}
