import java.util.ArrayList;
import java.util.function.Consumer;

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
	public void getOptions(Game game, Consumer<Option> list) {
		// Actions
		list.accept(GivePoints.create(game));
		list.accept(MultiplyPoints.create(game));
		list.accept(MovePoints.create(game));
		list.accept(InvertAllScores.create(game));
		list.accept(LowestBonus.create(game));
		list.accept(HighestPenalty.create(game));
		list.accept(Victory.create(game));
		// Rules
		list.accept(InvertPointChanges.create(game));
		list.accept(MultiplyPointChanges.create(game));
		list.accept(RepeatedLowestBonus.create(game));
		list.accept(RepeatedHighestPenalty.create(game));
		list.accept(RepeatedScoreWrap.create(game));
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
		public String getName() { return "Give the player(s) in last place 6 points"; }
		public String execute() {
			ArrayList<Player> min = new ArrayList<Player>();
			float m = 100;
			for (int i = 0; i < game.players.size(); i++) {
				Player p = game.players.get(i);
				if (p.score < m) {
					m = p.score;
					min.clear();
				}
				if (p.score == m) {
					min.add(p);
				}
			}
			if (min.size() == 0) return "Every player is above 100 points!";
			else {
				float amt = 6 * game.rulePointMultiplier;
				ArrayList<String> targets = new ArrayList<String>();
				for (Player target : min) {
					target.score += amt;
					targets.add(target.name);
				}
				if (targets.size() == 1) return "Gave the player in last place (" + targets.get(0) + ") " + amt + " points";
				return "Gave the players in last place (" + Utils.humanJoinList(targets) + ") " + amt + " points each for their stars";
			}
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
			ArrayList<Player> max = new ArrayList<Player>();
			float m = -100;
			for (int i = 0; i < game.players.size(); i++) {
				Player p = game.players.get(i);
				if (p.score > m) {
					m = p.score;
					max.clear();
				}
				if (p.score == m) {
					max.add(p);
				}
			}
			if (max.size() == 0) return "Every player is below -100 points!";
			else {
				float amt = 6 * game.rulePointMultiplier;
				ArrayList<String> targets = new ArrayList<String>();
				for (Player target : max) {
					target.score -= amt;
					targets.add(target.name);
				}
				if (targets.size() == 1) return "Took " + amt + " points from the player in first place (" + targets.get(0) + ")";
				return "Took " + amt + " points from the players in first place (" + Utils.humanJoinList(targets) + ")";
			}
		}
	}
	public static class Victory extends Option.Action {
		public Game game;
		public Victory(Game game) {
			this.game = game;
		}
		public static Victory create(Game game) {
			return new Victory(game);
		}
		public String getName() { return "The player with the highest score wins!"; }
		public String execute() {
			ArrayList<Player> max = new ArrayList<Player>();
			float m = -1000;
			for (int i = 0; i < game.players.size(); i++) {
				Player p = game.players.get(i);
				if (p.score > m) {
					m = p.score;
					max.clear();
				}
				if (p.score == m) {
					max.add(p);
				}
			}
			if (max.size() == 0) return "Every player is below -1000 points!";
			else if (max.size() == 1) {
				Player yay = max.get(0);
				game.winner = yay;
				return yay.name + " wins!";
			} else return "Multiple players are tied for first place!";
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
	public static class RepeatedScoreWrap extends Option.Rule.RepeatRule {
		public int amt;
		public RepeatedScoreWrap(Game game, int amt) {
			super(game, new ScoreWrapAction(game, amt));
			this.amt = amt;
		}
		public static class ScoreWrapAction extends Option.Action {
			public Game game;
			public int amt;
			public ScoreWrapAction(Game game, int amt) {
				this.game = game;
				this.amt = amt;
			}
			public String getName() {
				return "Scores wrap around at " + amt;
			}
			public String execute() {
				for (int i = 0; i < game.players.size(); i++) {
					Player p = game.players.get(i);
					while (p.score > amt - 1) {
						p.score -= amt * 2;
					}
					while (p.score < -amt) {
						p.score += amt * 2;
					}
				}
				return "Wrapped scores at " + amt;
			}
		}
		public static RepeatedScoreWrap create(Game game) {
			int amount = random.choice(new Integer[] { 16, 16, 16, 32, 32, 64 });
			return new RepeatedScoreWrap(game, amount);
		}
		@Override
		public String getName() {
			return "Scores wrap around at " + amt;
		}
		public String getSource() { return "score-wrap"; }
	}
}
