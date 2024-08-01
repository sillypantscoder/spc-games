// package com.sillypantscoder.spcgames.games;

// import com.sillypantscoder.spcgames.GameType;
// import com.sillypantscoder.spcgames.Subprocess;

// public class TheForeheadGame extends GameType.StaticGameType {
// 	public String getName() {
// 		return "The Forehead Game";
// 	}
// 	public String getDescription() {
// 		return "In this game, you hold up the phone to your forehead. The phone shows a random word chosen out of a list of words, and everyone else shouts clues at you about what the word is. (Everyone else can't use the word on screen.) If you get the word correct, tilt the phone down. If you decide to pass, tilt the screen upwards. When the time runs out, see how many you got!\nThere are a variety of word sets you can shoose from, or create your own word set!";
// 	}
// 	public String getID() {
// 		return "foreheadgame";
// 	}
// 	public Subprocess getProcess() {
// 		return new Subprocess(new String[] {"python3", "fakeserver.py"}, "../theforeheadgame");
// 	}
// }
