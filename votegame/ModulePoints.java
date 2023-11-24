public class ModulePoints extends Module {
	public ModulePoints(Game game) {
		super(game);
	}
	public String getModuleName() {
		return "Points";
	}
	public void accept() {
		for (int i = 0; i < game.players.size(); i++) {
			Player p = game.players.get(i);
			p.score = 0;
		}
	}
	public void repeal() {
		super.repeal();
		for (int i = 0; i < game.players.size(); i++) {
			Player p = game.players.get(i);
			p.score = 0;
		}
	}
	public Option.Action[] getActions(Game game) {
		return new Option.Action[] {
			GivePoints.create(game),
			MultiplyPoints.create(game),
			MovePoints.create(game),
			InvertAllScores.create(game),
			LowestBonus.create(game),
			HighestPenalty.create(game)
		};
	}
	public Option.Rule[] getRules(Game game) {
		return new Option.Rule[] {
			InvertPointChanges.create(game),
			MultiplyPointChanges.create(game),
			RepeatedLowestBonus.create(game),
			RepeatedHighestPenalty.create(game)
		};
	}
	// === ACTIONS ===
	public static class GivePoints extends Option.Action {
		public Player target;
		public float amount;
		public boolean reverse;
		public Game game;
		public GivePoints(Game game, Player target, float amount, boolean reverse) {
			this.game = game;
			this.target = target;
			this.amount = amount;
			this.reverse = reverse;
		}
		public static GivePoints create(Game game) {
			if (game.players.size() == 0) return null;
			Player target = random.choice(game.players);
			float amount = random.choice(new Float[] { 1f, 2f, 5f, target.score });
			if (target.score == 0) amount = random.choice(new Float[] { 1f, 2f, 3f, 4f, 5f });
			boolean reverse = random.choice(new Boolean[] { true, false, false });
			if (target.score <= 0) reverse = false;
			return new GivePoints(game, target, amount, reverse);
		}
		public String getName() { return (reverse ? "Take" : "Give") + " " + amount + " points " + (reverse ? "from" : "to") + " " + target.name; }
		public String execute() { target.score += amount * (reverse ? -1 : 1) * (game.rulePointMultiplier); return (reverse ? "Took" : "Gave") + " " + (amount * game.rulePointMultiplier) + " points " + (reverse ? "from" : "to") + " " + target.name; }
	}
	public static class MultiplyPoints extends Option.Action {
		public Player target;
		public float amount;
		public MultiplyPoints(Player target, float amount) {
			this.target = target;
			this.amount = amount;
		}
		public static MultiplyPoints create(Game game) {
			if (game.players.size() == 0) return null;
			Player target = random.choice(game.players);
			float amount = random.choice(new Float[] { 1f / 2f, 2f, 0f });
			return new MultiplyPoints(target, amount);
		}
		public String getName() { return "Multiply " + target.name + "'s points by " + amount; }
		public String execute() { target.score *= amount; return "Multiplied " + target.name + "'s points by " + amount; }
	}
	public static class MovePoints extends Option.Action {
		public Game game;
		public Player playerFrom;
		public Player playerTo;
		public float amount;
		public MovePoints(Game game, Player playerFrom, Player playerTo, float amount) {
			this.game = game;
			this.playerFrom = playerFrom;
			this.playerTo = playerTo;
			this.amount = amount;
		}
		public static MovePoints create(Game game) {
			if (game.players.size() < 2) return null;
			Player playerFrom = random.choice(game.players);
			Player playerTo = random.choice(game.players);
			if (playerFrom == playerTo) return null;
			float amount = random.choice(new Float[] { 1f / 2f, 2f, 0f });
			return new MovePoints(game, playerFrom, playerTo, amount);
		}
		public String getName() { return "Steal " + amount + " points from " + playerFrom.name + " and give them to " + playerTo.name; }
		public String execute() { playerFrom.score -= (amount * game.rulePointMultiplier); playerTo.score += (amount * game.rulePointMultiplier); return "Stole " + (amount * game.rulePointMultiplier) + " points from " + playerFrom.name + " and gave them to " + playerTo.name; }
	}
	public static class InvertAllScores extends Option.Action {
		public Game game;
		public InvertAllScores(Game game) {
			this.game = game;
		}
		public static InvertAllScores create(Game game) {
			return new InvertAllScores(game);
		}
		public String getName() { return "Invert everybody's scores!"; }
		public String execute() { for (int i = 0; i < game.players.size(); i++) { game.players.get(i).score *= -1; } return "Inverted everyone's scores!"; }
	}
	public static class LowestBonus extends Option.Action {
		public Game game;
		public LowestBonus(Game game) {
			this.game = game;
		}
		public static LowestBonus create(Game game) {
			return new LowestBonus(game);
		}
		public String getName() { return "Give the player in last place 6 points"; }
		public String execute() {
			Player min = null;
			float m = 100;
			for (int i = 0; i < game.players.size(); i++) {
				Player p = game.players.get(i);
				if (p.score < m) {
					m = p.score;
					min = p;
				}
			}
			if (min == null) return "Every player is above 100 points!";
			float amt = 6 * game.rulePointMultiplier;
			min.score += amt;
			return "Gave the player in last place (" + min.name + ") " + amt + " points";
		}
	}
	public static class HighestPenalty extends Option.Action {
		public Game game;
		public HighestPenalty(Game game) {
			this.game = game;
		}
		public static HighestPenalty create(Game game) {
			return new HighestPenalty(game);
		}
		public String getName() { return "Take 6 points from the player in first place"; }
		public String execute() {
			Player max = null;
			float m = -100;
			for (int i = 0; i < game.players.size(); i++) {
				Player p = game.players.get(i);
				if (p.score > m) {
					m = p.score;
					max = p;
				}
			}
			if (max == null) return "Every player is below -100 points!";
			float amt = 6 * game.rulePointMultiplier;
			max.score -= amt;
			return "Took " + amt + " points from the player in first place (" + max.name + ")";
		}
	}
	// === RULES ===
	public static class InvertPointChanges extends Option.Rule {
		public Game target;
		public InvertPointChanges(Game game) {
			target = game;
		}
		public static InvertPointChanges create(Game game) {
			return new InvertPointChanges(game);
		}
		public String getName() {
			return "Reverse all point changes";
		}
		public void accept() {
			target.rulePointMultiplier *= -1;
		}
		public void repeal() {
			target.rulePointMultiplier *= -1;
		}
	}
	public static class MultiplyPointChanges extends Option.Rule {
		public Game target;
		public MultiplyPointChanges(Game game) {
			target = game;
		}
		public static MultiplyPointChanges create(Game game) {
			return new MultiplyPointChanges(game);
		}
		public String getName() {
			return "Double all point changes";
		}
		public void accept() {
			target.rulePointMultiplier *= 2;
		}
		public void repeal() {
			target.rulePointMultiplier /= 2;
		}
	}
	public static class RepeatedLowestBonus extends Option.Rule.RepeatRule {
		public RepeatedLowestBonus(Game game) {
			super(game, LowestBonus.create(game));
		}
		public static RepeatedLowestBonus create(Game game) {
			return new RepeatedLowestBonus(game);
		}
		public String getSource() {
			return "last-place-bonus";
		}
	}
	public static class RepeatedHighestPenalty extends Option.Rule.RepeatRule {
		public RepeatedHighestPenalty(Game game) {
			super(game, HighestPenalty.create(game));
		}
		public static RepeatedHighestPenalty create(Game game) {
			return new RepeatedHighestPenalty(game);
		}
		public String getSource() { return "first-place-penalty"; }
	}
}
