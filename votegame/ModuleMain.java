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
		if (random.randint(0, 10) < 1) list.accept(Victory.create(game));
		if (random.randint(0, 6) < 1) list.accept(AloneVictory.create(game));
		// Rules
		list.accept(AcceptZeroVotes.create(game));
	}
	public Option[] getAllOptions() {
		return new Option[] {
			new Victory(null, null),
			new AloneVictory(null),
			new AcceptZeroVotes(null)
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
		public void accept() {
			target.ruleZeroVotes = true;
		}
		public void repeal() {
			target.ruleZeroVotes = false;
		}
	}
}
