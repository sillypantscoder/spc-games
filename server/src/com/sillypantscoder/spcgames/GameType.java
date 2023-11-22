package com.sillypantscoder.spcgames;

public abstract class GameType {
	public abstract String getName();
	public abstract String getID();
	public abstract Subprocess getProcess();
	public abstract String getStatus(Game game);
	public abstract String getModStatus(Game game);
}
