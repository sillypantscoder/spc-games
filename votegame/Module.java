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
		repealAndGetRemovedRules();
	}
	public ArrayList<String> repealAndGetRemovedRules() {
		ArrayList<String> removed = new ArrayList<String>();
		Option[] rules = getAllRules();
		for (int i = 0; i < game.rules.size(); i++) {
			for (int a = 0; a < rules.length; a++) {
				if (game.rules.get(i).getClass().equals(rules[a].getClass())) {
					removed.add("<li>" + game.rules.get(i).getName() + "</li>");
					game.rules.get(i).repeal();
					game.rules.remove(i);
					i -= 1;
					break;
				}
			}
		}
		return removed;
	}
	public Option[] getOptions() {
		ArrayList<Option> options = new ArrayList<Option>();
		getOptions((x) -> options.add(x));
		Option[] result = new Option[options.size()];
		for (int i = 0; i < result.length; i++) result[i] = options.get(i);
		return result;
	}
	/**
	 * Get a list of all the options this module provides.
	 * @param list Register all of the options using this consumer.
	 */
	public abstract void getOptions(Consumer<Option> list);
	/**
	 * Get a list of all the rules this module contains, initialized with the source game. If other parameters are required to initialize the rule, they should be set to a random allowed value.
	 * This is used to erase all the rules when the module is removed.
	 * @return A list of all the options this module contains.
	 */
	public abstract Option.Rule[] getAllRules();
}