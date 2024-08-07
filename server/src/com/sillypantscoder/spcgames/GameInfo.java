package com.sillypantscoder.spcgames;

import com.sillypantscoder.spcgames.Game.ActiveGame;

public interface GameInfo {
	/**
	 * Get the name of the game.
	 * @return
	 */
	public String getName();
	/**
	 * Get the short description of the game. This should include just a very basic description.
	 * @return
	 */
	public String getShortDescription();
	/**
	 * Get the long description of the game. This can include instructions, how the game works, etc.
	 * @return
	 */
	public String getLongDescription();
	/**
	 * Get the ID of the game. This should be /^[a-z\-]+$/.
	 * @return
	 */
	public String getID();
	public static interface ActiveGameInfo extends GameInfo {
		/**
		 * Create an instance of the game.
		 */
		public ActiveGame create(String name);
	}
}
