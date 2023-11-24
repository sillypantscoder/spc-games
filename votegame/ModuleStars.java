public class ModuleStars extends Module {
	public ModuleStars(Game game) {
		super(game);
	}
	public String getModuleName() {
		return "Stars";
	}
	public void accept() {
		for (int i = 0; i < game.players.size(); i++) {
			Player p = game.players.get(i);
			p.hasStar = false;
		}
	}
	public void repeal() {
		super.repeal();
		for (int i = 0; i < game.players.size(); i++) {
			Player p = game.players.get(i);
			p.hasStar = false;
		}
	}
	public Option.Action[] getActions(Game game) {
		return new Option.Action[] {
			ToggleStar.create(game),
			ToggleStar.create(game),
			ToggleStar.create(game)
		};
	}
	public Option.Rule[] getRules(Game game) {
		return new Option.Rule[] {
		};
	}
	// === ACTIONS ===
	public static class ToggleStar extends Option.Action {
		public Player target;
		public Game game;
		public ToggleStar(Game game, Player target) {
			this.game = game;
			this.target = target;
		}
		public static ToggleStar create(Game game) {
			if (game.players.size() == 0) return null;
			Player target = random.choice(game.players);
			return new ToggleStar(game, target);
		}
		public String getName() {
			if (target.hasStar) return "Take " + target.name + "'s star";
			else return "Give " + target.name + " a star";
		}
		public String execute() {
			target.hasStar = !target.hasStar;
			if (target.hasStar) return "Gave " + target.name + " a star";
			else return "Took " + target.name + "'s star";
		}
	}
}
