package com.sillypantscoder.votegame;

import java.util.ArrayList;

public class Player {
	// basic things
	public String realName;
	public String displayName;
	public ArrayList<String> events;
	public int vote;
	// module info
	public float score;
	public boolean hasStar;
	public ModuleColors.Color color;
	public Player(String realName, String displayName) {
		this.realName = realName;
		this.displayName = displayName;
		this.events = new ArrayList<String>();
		this.vote = -1;
		this.score = 0;
		this.hasStar = false;
		this.color = ModuleColors.Color.Red;
	}
	public void fire(String event) {
		events.add(event);
	}
	public String getMessagesString() {
		String r = "";
		for (int i = 0; i < events.size(); i++) {
			r += events.get(i) + "\n";
		}
		events = new ArrayList<String>();
		return r;
	}
}
