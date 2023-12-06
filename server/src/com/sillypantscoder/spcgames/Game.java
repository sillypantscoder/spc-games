package com.sillypantscoder.spcgames;

import java.util.Date;
import java.util.Random;

public class Game extends WebProcess {
	public GameType type;
	public String name;
	public String id;
	public long deletionTime;
	public Game(GameType type, String name) {
		super(type.getProcess());
		this.type = type;
		this.name = name;
		id = type.getID() + new Random().nextInt(Integer.MAX_VALUE);
		this.deletionTime = 0;
	}
	/**
	 * Get the short status to be shown on the home page.
	 * @return
	 */
	public String getStatus() {
		return this.type.getStatus(this);
	}
	/**
	 * Get the moderator status to be shown on the /host page. This can (and should) contain HTML.
	 * @return
	 */
	public String getModStatus() {
		return this.type.getModStatus(this);
	}
	/**
	 * Check whether the game should be deleted because it was marked for deletion.
	 * @return
	 */
	public boolean stillValid() {
		if (this.deletionTime == 0) return true;
		return this.deletionTime > new Date().getTime();
	}
}
