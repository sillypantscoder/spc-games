import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

public class ModuleMain extends Module {
	public ModuleMain(Game game) {
		super(game);
	}
	public String getModuleName() {
		return "Basic Actions";
	}
	public void getOptions(Game game, Consumer<Option> list) {
		// Actions
		if (random.randint(0, 5) < 1) list.accept(Victory.create(game));
		if (random.randint(0, 3) < 1) list.accept(AloneVictory.create(game));
		list.accept(SelectRule.create(game));
		// Rules
		if (game.players.size() >= 3) list.accept(AcceptZeroVotes.create(game));
	}
	public Option.Rule[] getAllRules(Game game) {
		return new Option.Rule[] {
			new AcceptZeroVotes(game)
		};
	}
	// === ACTIONS ===
	public static class Victory extends Option.Action {
		public Player target;
		public Game game;
		public Victory(Game game, Player target) {
			this.game = game;
			this.target = target;
		}
		public static Victory create(Game game) {
			if (game.players.size() == 0) return null;
			Player target = random.choice(game.players);
			return new Victory(game, target);
		}
		public String getName() { return target.name + " wins (if valid)"; }
		public String execute() {
			Optional<Option.Rule.WinCondition> invalidation = game.findWinInvalidations(target);
			if (invalidation.isEmpty()) {
				game.winner = target;
				return target.name + " wins!";
			} else return target.name + " cannot win because: " + invalidation.get();
		}
	}
	public static class AloneVictory extends Option.Action {
		public Game game;
		public AloneVictory(Game game) {
			this.game = game;
		}
		public static AloneVictory create(Game game) {
			return new AloneVictory(game);
		}
		public String getName() { return "If there is exactly one player who can win, they win"; }
		public String execute() {
			ArrayList<Player> validWinners = new ArrayList<Player>();
			for (Player target : game.players) {
				if (game.findWinInvalidations(target).isEmpty()) {
					validWinners.add(target);
				}
			}
			if (validWinners.size() == 1) {
				Player target = validWinners.get(0);
				game.winner = target;
				return target.name + " wins!";
			} else if (validWinners.size() == 0) return "No one can win!";
			else return "More than one player can win!";
		}
	}
	public static class SelectRule extends Option.Action {
		public Option.Rule rule1;
		public Option.Rule rule2;
		public Game game;
		public SelectRule(Game game, Option.Rule rule1, Option.Rule rule2) {
			this.game = game;
			this.rule1 = rule1;
			this.rule2 = rule2;
		}
		public static SelectRule create(Game game) {
			ArrayList<Option.Rule> activeRules = game.rules;
			ArrayList<Option.Rule> possibleRules = new ArrayList<Option.Rule>();
			// Go through all the game's modules
			for (int i = 0; i < activeRules.size(); i++) {
				if (activeRules.get(i) instanceof Module mod) {
					// For each one...
					// Get the list of rules it provides
					Option.Rule[] newOptions = mod.getAllRules(game);
					// Go through the list
					for (Option.Rule newRule : newOptions) {
						// Don't include rules we already have
						if (activeRules.indexOf(newRule) == -1) {
							// Remember this rule
							possibleRules.add(newRule);
						}
					}
				}
			}
			// System.err.println("done");
			// Select some rules (from possibleRules)
			if (possibleRules.size() < 2) return null;
			random.shuffle(possibleRules);
			return new SelectRule(game, possibleRules.get(0), possibleRules.get(1));
		}
		public String getName() { return "Randomly add one of these two rules:\n- " + this.rule1.getName() + "\n- " + this.rule2.getName(); }
		public String execute() {
			Option.Rule selected = random.choice(new Option.Rule[] { rule1, rule2 });
			game.rules.add(selected);
			return "Added the rule: " + selected.getName();
		}
	}
	// === RULES ===
	public static class AcceptZeroVotes extends Option.Rule {
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
		public void accept() {}
		public void repeal() {}
	}
}
