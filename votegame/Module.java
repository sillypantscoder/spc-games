import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class Module extends Option.Rule {
	public Game game;
	public Module(Game game) {
		this.game = game;
	}
	public String getName() {
		return "Add " + getModuleName() + " to the game";
	}
	public abstract String getModuleName();
	public void accept() {}
	public void repeal() {
		Option[] rules = getOptions(game);
		for (int i = 0; i < game.rules.size(); i++) {
			for (int a = 0; a < rules.length; a++) {
				if (game.rules.get(i).getClass().equals(rules[a].getClass())) {
					game.rules.get(i).repeal();
					game.rules.remove(i);
					i -= 1;
					break;
				}
			}
		}
	}
	public Option[] getOptions(Game game) {
		ArrayList<Option> options = new ArrayList<Option>();
		getOptions(game, (x) -> options.add(x));
		Option[] result = new Option[options.size()];
		for (int i = 0; i < result.length; i++) result[i] = options.get(i);
		return result;
	}
	public abstract void getOptions(Game game, Consumer<Option> list);
	// Might be added soon!
	//
	// public abstract void getGEs(Game game, Consumer<GeneralEffect> list);
	// public static GeneralEffect[] getAllGE(Game game) {
	// 	ArrayList<GeneralEffect> options = new ArrayList<GeneralEffect>();
	// 	for (Option.Rule rule : game.rules) {
	// 		if (rule instanceof Module mod) mod.getGEs(game, (x) -> options.add(x));
	// 	}
	// 	GeneralEffect[] result = new GeneralEffect[options.size()];
	// 	for (int i = 0; i < result.length; i++) result[i] = options.get(i);
	// 	return result;
	// }
	// public static abstract class GeneralEffect {
	// 	public abstract void execute(Game game, Player p);
	// 	public abstract void getName();
	// 	public static abstract class MultiplayerGE extends GeneralEffect {
	// 		public void execute(Game game, ArrayList<Player> p) {
	// 			for (Player m : p) execute(game, m);
	// 		}
	// 	}
	// }
}