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
	public void getOptions(Consumer<Option> list) {
		for (var i = 0; i < 3; i++) {
			// Actions
			list.accept(GiveStar.create(game));
			list.accept(GiveStar.create(game));
			list.accept(GiveStar.create(game));
			list.accept(GiveStar.create(game));
			list.accept(GiveStar.create(game));
			list.accept(TakeStar.create(game));
			list.accept(ToggleAllStars.create(game));
			// Actions with Points
			if (game.hasRule(ModulePoints.class)) {
				list.accept(StarToPoints.create(game));
				list.accept(StarToPointMultiplier.create(game));
				list.accept(PrimeNumberStars.create(game));
			}
			// Rules
			list.accept(RequireStar.create(game));
			// Rules with Points
			if (game.hasRule(ModulePoints.class)) {
				list.accept(RepeatedStarToPoints.create(game));
			}
		}
	}
	public Option.Rule[] getAllRules() {
		return new Option.Rule[] {
			new RepeatedStarToPoints(game),
			new RequireStar(game)
		};
	}
	// === ACTIONS ===
	public static class GiveStar extends Option.Action {
		public Player target;
		public Game game;
		public GiveStar(Game game, Player target) {
			this.game = game;
			this.target = target;
		}
		public static GiveStar create(Game game) {
			if (game.players.size() == 0) return null;
			Player target = random.choice(game.players);
			if (target.hasStar) return null;
			return new GiveStar(game, target);
		}
		public String getName() {
			return "Give " + target.name + " a star";
		}
		public String execute() {
			target.hasStar = true;
			return "Gave " + target.name + " a star";
		}
	}
	public static class TakeStar extends Option.Action {
		public Player target;
		public Game game;
		public TakeStar(Game game, Player target) {
			this.game = game;
			this.target = target;
		}
		public static TakeStar create(Game game) {
			if (game.players.size() == 0) return null;
			Player target = random.choice(game.players);
			if (! target.hasStar) return null;
			return new TakeStar(game, target);
		}
		public String getName() {
			return "Take " + target.name + "'s star";
		}
		public String execute() {
			target.hasStar = false;
			return "Took " + target.name + "'s star";
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
			return "Everyone with a star gets " + amount + " points (ignoring 'point changes' rules)";
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
	public static class PrimeNumberStars extends Option.Action {
		public Game game;
		public PrimeNumberStars(Game game) {
			this.game = game;
		}
		public static StarToPointMultiplier create(Game game) {
			return new StarToPointMultiplier(game);
		}
		public String getName() {
			return "Everyone with prime-numbered points gets a star, everyone else loses a star";
		}
		public String execute() {
			ArrayList<String> targets = new ArrayList<String>();
			for (int i = 0; i < game.players.size(); i++) {
				Player p = game.players.get(i);
				int a = Math.round(p.score);
				if (a == p.score && isPrime(a)) {
					p.hasStar = true;
					targets.add(p.name);
				} else {
					p.hasStar = false;
				}
			}
			if (targets.size() == 0) return "Gave no one stars for their points";
			if (targets.size() == 1) return "Gave " + targets.get(0) + " a star for their points";
			return "Gave " + Utils.humanJoinList(targets) + " a star each for their points";
		}
		public boolean isPrime(int number) {
			for (int i = 2; i <= number / 2; ++i) {
				// condition for nonprime number
				if (number % i == 0) {
					return false;
				}
			}
			return true;
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
