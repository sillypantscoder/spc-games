import java.util.function.Consumer;

public class ModuleMain extends Module {
	public ModuleMain(Game game) {
		super(game);
	}
	public String getModuleName() {
		return "Basic Actions";
	}
	public void getOptions(Game game, Consumer<Option> list) {
		// Actions without any other modules
		if (! (game.hasModule(ModulePoints.class) || game.hasModule(ModuleStars.class))) {
			list.accept(InstantWin.create(game));
		}
		// Rules
		list.accept(AcceptZeroVotes.create(game));
	}
	// === ACTIONS ===
	public static class InstantWin extends Option.Action {
		public Player target;
		public Game game;
		public InstantWin(Game game, Player target) {
			this.game = game;
			this.target = target;
		}
		public static InstantWin create(Game game) {
			if (game.players.size() == 0) return null;
			Player target = random.choice(game.players);
			return new InstantWin(game, target);
		}
		public String getName() { return target.name + " wins!"; }
		public String execute() { game.winner = target; return target.name + " wins!"; }
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
