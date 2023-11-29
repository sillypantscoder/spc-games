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
		Option[] rules = getAllOptions();
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
	public abstract Option[] getAllOptions();
}