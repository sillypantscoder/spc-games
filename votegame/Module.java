import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * A Module is a special type of Rule which contains a list of Options and Rules to add to the game.
 */
public abstract class Module extends Option.Rule {
	public Game game;
	public Module(Game game) {
		this.game = game;
	}
	public String getName() {
		return "Add " + getModuleName() + " to the game";
	}
	/**
	 * The name of the module.
	 * @return
	 */
	public abstract String getModuleName();
	public void accept() {}
	public void repeal() {
		Option[] rules = getAllRules(null);
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
	/**
	 * Get a list of all the options this module provides.
	 * @param game
	 * @param list Register all of the options using this consumer.
	 */
	public abstract void getOptions(Game game, Consumer<Option> list);
	/**
	 * Get a list of all the rules this module contains, initialized with the provided game. If other parameters are required to initialize the rule, they should be set to a 'default' value. The provided game object may be null.
	 * This is used to erase all the rules when the module is removed.
	 * @return A list of all the options this module contains.
	 */
	public abstract Option.Rule[] getAllRules(Game game);
}