package com.sillypantscoder.spcgames;

import java.util.Date;
import java.util.Random;

import com.sillypantscoder.spcgames.http.HttpResponse;

public abstract class Game {
	public GameInfo info;
	public String name;
	public String id;
	public long deletionTime;
	public Game(GameInfo info, String name) {
		this.info = info;
		this.name = name;
		this.id = info.getID() + new Random().nextInt(Integer.MAX_VALUE);
		this.deletionTime = 0;
	}
	public abstract HttpResponse get(String path);
	public abstract HttpResponse post(String path, String body);
	public abstract void remove();
	/**
	 * Check whether the game should be deleted because it was marked for deletion.
	 * @return
	 */
	public boolean stillValid() {
		if (this.deletionTime == 0) return true;
		return this.deletionTime > new Date().getTime();
	}
}
