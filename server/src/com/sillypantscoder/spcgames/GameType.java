package com.sillypantscoder.spcgames;

public abstract class GameType {
	/**
	 * Get the name of the game type.
	 * @return
	 */
	public abstract String getName();
	/**
	 * Get the long description of the game. This can include instructions, how the game works, etc.
	 * @return
	 */
	public abstract String getDescription();
	/**
	 * Get the ID of the game. This should be /^[a-z\-]+$/.
	 * @return
	 */
	public abstract String getID();
	/**
	 * Create a Subprocess object with the game.
	 * @return
	 */
	public abstract Subprocess getProcess();
	/**
	 * Get the short game status: the string that will be shown on the home page.
	 * @param game
	 * @return
	 */
	public abstract String getStatus(Game game);
	/**
	 * Get the moderator status to be shown on the "host" page. This can (and should) contain HTML.
	 * @param game
	 * @return
	 */
	public abstract String getModStatus(Game game);
}
