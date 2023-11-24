import java.util.ArrayList;

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
	public Option[] getOptions(Game game) {
		Option[] actions = new Option[] {
			ToggleStar.create(game),
			ToggleStar.create(game),
			ToggleStar.create(game)
		};
		for (int i = 0; i < game.rules.size(); i++) {
			if (game.rules.get(i).getClass() == ModulePoints.class) {
				actions = Option.combineOptionLists(actions, new Option[] {
					StarToPoints.create(game)
				});
			}
		}
		return actions;
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
	public static class StarToPoints extends Option.Action {
		public int amount;
		public Game game;
		public StarToPoints(Game game, int amount) {
			this.game = game;
			this.amount = amount;
		}
		public static StarToPoints create(Game game) {
			int amount = random.choice(new Integer[] { 5, 10, 20 });
			return new StarToPoints(game, amount);
		}
		public String getName() {
			return "Everyone with a star gets " + amount + " points";
		}
		public String execute() {
			ArrayList<String> targets = new ArrayList<String>();
			for (int i = 0; i < game.players.size(); i++) {
				Player p = game.players.get(i);
				if (p.hasStar) {
					p.score += amount;
					targets.add(p.name);
				}
			}
			if (targets.size() == 0) return "Gave no one points for their stars";
			if (targets.size() == 1) return "Game " + targets.get(0) + " " + amount + " points for their star";
			return "Gave " + Utils.humanJoinList(targets) + " " + amount + " points each for their stars";
		}
	}
}
