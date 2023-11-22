import java.util.ArrayList;

public abstract class Option {
	public abstract String getName();
	// Utils
	public static Option[] createOptionList(ArrayList<Option> items) {
		Option[] r = new Option[items.size()];
		for (int i = 0; i < items.size(); i++) {
			r[i] = items.get(i);
		}
		return r;
	}
	@Override
	public String toString() { return getName(); }
	// Actions
	public static abstract class Action extends Option {
		public abstract String execute();
		public static class GivePoints extends Action {
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
		public static class MultiplyPoints extends Action {
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
		public static class MovePoints extends Action {
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
		public static class InvertAllScores extends Action {
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
		public static class LowestBonus extends Action {
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
		public static class HighestPenalty extends Action {
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
				return "Took " + amt + " points from the player in last place (" + max.name + ")";
			}
		}
	}
	// Rules
	public static abstract class Rule extends Option {
		public static Rule checkFor(Game game, Rule type) {
			Class<? extends Rule> ruleType = type.getClass();
			for (int i = 0; i < game.rules.size(); i++) {
				if (game.rules.get(i).getClass().equals(ruleType)) {
					return game.rules.get(i);
				}
			}
			return type;
		}
		public abstract void accept();
		public abstract void repeal();
		public static abstract class RepeatRule extends Rule {
			public Game target;
			public Action action;
			public RepeatRule(Game target, Action action) {
				this.target = target;
				this.action = action;
			}
			public String getName() {
				return this.action.getName() + " each round";
			}
			public abstract String getSource();
			public void accept() {}
			public void repeal() {}
		}
		public static class InvertPointChanges extends Rule {
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
		public static class MultiplyPointChanges extends Rule {
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
		public static class AcceptZeroVotes extends Rule {
			public Game target;
			public AcceptZeroVotes(Game game) {
				target = game;
			}
			public static AcceptZeroVotes create(Game game) {
				return new AcceptZeroVotes(game);
			}
			public String getName() {
				return "Also accept options with zero votes";
			}
			public void accept() {
				target.ruleZeroVotes = true;
			}
			public void repeal() {
				target.ruleZeroVotes = false;
			}
		}
		public static class RepeatedLowestBonus extends RepeatRule {
			public RepeatedLowestBonus(Game game) {
				super(game, Action.LowestBonus.create(game));
			}
			public static RepeatedLowestBonus create(Game game) {
				return new RepeatedLowestBonus(game);
			}
			public String getSource() {
				return "last-place-bonus";
			}
		}
		public static class RepeatedHighestPenalty extends RepeatRule {
			public RepeatedHighestPenalty(Game game) {
				super(game, Action.HighestPenalty.create(game));
			}
			public static RepeatedHighestPenalty create(Game game) {
				return new RepeatedHighestPenalty(game);
			}
			public String getSource() { return "first-place-penalty"; }
		}
		// public static class RepeatedRandomPenalty extends Rule {
		// 	public Game game;
		// 	public RepeatedRandomPenalty(Game game) {
		// 		this.game = game;
		// 	}
		// 	public static RepeatedRandomPenalty create(Game game) {
		// 		return new RepeatedRandomPenalty(game);
		// 	}
		// 	public String getName() { return "Take 5 points from a random player each round"; }
		// 	public void accept() {
		// 		game.ruleRandomPenalty = true;
		// 	}
		// 	public void repeal() {
		// 		game.ruleRandomPenalty = false;
		// 	}
		// }
	}
	// Generate
	public static Option[] combineOptionLists(Option[]... lists) {
		ArrayList<Option> combined = new ArrayList<Option>();
		for (Option[] list : lists) {
			for (Option o : list) {
				combined.add(o);
			}
		}
		return optionArrayListToList(combined);
	}
	public static Option[] optionArrayListToList(ArrayList<Option> list) {
		Option[] ol = new Option[list.size()];
		for (int x = 0; x < list.size(); x++) ol[x] = list.get(x);
		return ol;
	}
	public static Option create(Game game) {
		Action[] actionList = new Action[] {
			Action.GivePoints.create(game),
			Action.GivePoints.create(game),
			Action.GivePoints.create(game),
			Action.MultiplyPoints.create(game),
			Action.MovePoints.create(game),
			Action.InvertAllScores.create(game),
			Action.LowestBonus.create(game),
			Action.HighestPenalty.create(game)
		};
		Rule[] ruleList = new Rule[] {
			Rule.InvertPointChanges.create(game),
			Rule.MultiplyPointChanges.create(game),
			Rule.AcceptZeroVotes.create(game),
			Rule.RepeatedLowestBonus.create(game),
			Rule.RepeatedHighestPenalty.create(game)
		};
		Option[] possible = combineOptionLists(actionList, actionList, ruleList);
		// int selectedIndex = random.randint(0, possible.length - 1);
		Option selectedOption = random.choice(possible);
		if (selectedOption == null) return null;
		if (selectedOption instanceof Rule ruleOption) return Rule.checkFor(game, ruleOption);
		else return selectedOption;
	}
}