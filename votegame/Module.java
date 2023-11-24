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
		Option.Rule[] rules = getRules(game);
		for (int i = 0; i < game.rules.size(); i++) {
			for (int a = 0; a < rules.length; a++) {
				if (game.rules.get(i).getClass().equals(rules[i].getClass())) {
					game.rules.remove(i);
					i -= 1;
					break;
				}
			}
		}
	}
	public abstract Option.Action[] getActions(Game game);
	public abstract Option.Rule[] getRules(Game game);
}