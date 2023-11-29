import java.util.ArrayList;
import java.util.function.Consumer;

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
	public void getOptions(Game game, Consumer<Option> list) {
		// Actions
		list.accept(ToggleStar.create(game));
		list.accept(ToggleStar.create(game));
		list.accept(ToggleStar.create(game));
		list.accept(ToggleAllStars.create(game));
		list.accept(RandomizeAllStars.create(game));
		// Actions with Points
		if (game.hasModule(ModulePoints.class)) {
			list.accept(StarToPoints.create(game));
			list.accept(StarToPointMultiplier.create(game));
		}
		// Rules with Points
		if (game.hasModule(ModulePoints.class)) {
			list.accept(RepeatedStarToPoints.create(game));
		}
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
	public static class ToggleAllStars extends Option.Action {
		public Game game;
		public ToggleAllStars(Game game) {
			this.game = game;
		}
		public static ToggleAllStars create(Game game) {
			return new ToggleAllStars(game);
		}
		public String getName() {
			return "Invert everybody's stars!";
		}
		public String execute() {
			for (int i = 0; i < game.players.size(); i++) {
				Player p = game.players.get(i);
				p.hasStar = !p.hasStar;
			}
			return "Inverted everybody's stars!";
		}
	}
	public static class RandomizeAllStars extends Option.Action {
		public Game game;
		public RandomizeAllStars(Game game) {
			this.game = game;
		}
		public static RandomizeAllStars create(Game game) {
			return new RandomizeAllStars(game);
		}
		public String getName() {
			return "Randomize everybody's stars!";
		}
		public String execute() {
			for (int i = 0; i < game.players.size(); i++) {
				Player p = game.players.get(i);
				p.hasStar = random.choice(new Boolean[] { true, false });
			}
			return "Randomized everybody's stars!";
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
			if (targets.size() == 1) return "Gave " + targets.get(0) + " " + amount + " points for their star";
			return "Gave " + Utils.humanJoinList(targets) + " " + amount + " points each for their stars";
		}
	}
	public static class StarToPointMultiplier extends Option.Action {
		public Game game;
		public StarToPointMultiplier(Game game) {
			this.game = game;
		}
		public static StarToPointMultiplier create(Game game) {
			return new StarToPointMultiplier(game);
		}
		public String getName() {
			return "Everyone with a star (and positive points) loses it but gets x2 points";
		}
		public String execute() {
			ArrayList<String> targets = new ArrayList<String>();
			for (int i = 0; i < game.players.size(); i++) {
				Player p = game.players.get(i);
				if (p.hasStar && p.score > 0) {
					p.score *= 2;
					p.hasStar = false;
					targets.add(p.name);
				}
			}
			if (targets.size() == 0) return "Gave no one points for their stars";
			if (targets.size() == 1) return "Gave " + targets.get(0) + " x2 points for their star";
			return "Gave " + Utils.humanJoinList(targets) + " x2 points each for their stars";
		}
	}
	// === RULES ===
	public static class RepeatedStarToPoints extends Option.Rule.RepeatRule {
		public RepeatedStarToPoints(Game game) {
			super(game, new StarToPoints(game, 5));
		}
		public static RepeatedStarToPoints create(Game game) {
			return new RepeatedStarToPoints(game);
		}
		public String getSource() {
			return "star-to-points";
		}
	}
	public static class RequireStar extends Option.Rule.WinCondition {
		public RequireStar(Game game) {
			super(game);
		}
		public static RequireStar create(Game game) {
			return new RequireStar(game);
		}
		public String getName() { return "Require having a star to win"; }
		public boolean isPlayerValid(Player target) {
			return target.hasStar;
		}
	}
}
