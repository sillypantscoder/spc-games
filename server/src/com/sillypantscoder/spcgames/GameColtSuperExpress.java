package com.sillypantscoder.spcgames;

public class GameColtSuperExpress extends Game {
	public View getView() {
		return new View() {
			public String getName() {
				return "Colt Super Express";
			}
			public String getID() {
				return "coltsuperexpress";
			}
			public Game create() {
				return new GameColtSuperExpress();
			}
		};
	}
	public Subprocess getProcess() {
		return new Subprocess(new String[] {"python3", "main.py"}, "../coltsuperexpress");
	}
}
