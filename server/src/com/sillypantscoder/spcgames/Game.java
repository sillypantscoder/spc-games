package com.sillypantscoder.spcgames;

import java.util.Date;
import java.util.Random;

import com.sillypantscoder.spcgames.http.HttpResponse;

public abstract class Game {
	public GameInfo info;
	public String id;
	public long deletionTime;
	public Game(GameInfo info) {
		this.info = info;
		this.id = info.getID() + new Random().nextInt(Integer.MAX_VALUE);
		this.deletionTime = 0;
	}
	public abstract HttpResponse get(String path);
	public abstract HttpResponse post(String path, String body);
	/**
	 * Check whether the game should be deleted because it was marked for deletion.
	 * @return
	 */
	public boolean stillValid() {
		if (this.deletionTime == 0) return true;
		return this.deletionTime > new Date().getTime();
	}
	public static abstract class ActiveGame extends Game {
		public String name;
		public ActiveGame(GameInfo.ActiveGameInfo info, String name) {
			super(info);
			this.name = name;
		}
		public abstract void remove();
		public abstract String getStatus();
		public abstract String getModStatus();
	}
	public static abstract class StaticGame extends Game {
		public StaticGame(GameInfo info) {
			super(info);
		}
	}
}
