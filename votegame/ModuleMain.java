public class ModuleMain extends Module {
	public ModuleMain(Game game) {
		super(game);
	}
	public String getModuleName() {
		return "Basic Actions";
	}
	public Option[] getOptions(Game game) {
		return new Option[] {
			AcceptZeroVotes.create(game)
		};
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
