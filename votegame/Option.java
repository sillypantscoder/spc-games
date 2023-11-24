import java.util.ArrayList;

public abstract class Option {
	public abstract String getName();
	// Utils
	public static Option[] createOptionList(ArrayList<Option> items) {
		Option[] r = new Option[items.size()];
		for (int i = 0; i < items.size(); i++) {
			r[i] = items.get(i);
		}
		return r;
	}
	@Override
	public String toString() { return getName(); }
	// Actions
	public static abstract class Action extends Option {
		public abstract String execute();
	}
	// Rules
	public static abstract class Rule extends Option {
		public static Rule checkFor(Game game, Rule type) {
			Class<? extends Rule> ruleType = type.getClass();
			for (int i = 0; i < game.rules.size(); i++) {
				if (game.rules.get(i).getClass().equals(ruleType)) {
					return game.rules.get(i);
				}
			}
			return type;
		}
		public abstract void accept();
		public abstract void repeal();
		public static abstract class RepeatRule extends Rule {
			public Game target;
			public Action action;
			public RepeatRule(Game target, Action action) {
				this.target = target;
				this.action = action;
			}
			public String getName() {
				return this.action.getName() + " each round";
			}
			public abstract String getSource();
			public void accept() {}
			public void repeal() {}
		}
	}
	// Generate
	public static Option[] combineOptionLists(Option[] list1, Option[] list2) {
		ArrayList<Option> combined = new ArrayList<Option>();
		for (int o = 0; o < list1.length; o++) {
			combined.add(list1[o]);
		}
		for (int o = 0; o < list2.length; o++) {
			combined.add(list2[o]);
		}
		return optionArrayListToList(combined);
	}
	public static Option[] combineOptionLists(Option[] list1, Option[] list2, Option[] list3) {
		return combineOptionLists(combineOptionLists(list1, list2), list3);
	}
	public static Option[] optionArrayListToList(ArrayList<Option> list) {
		Option[] ol = list.toArray(new Option[0]);
		return ol;
	}
	public static Option create(Game game) {
		Option[] optionList = new Option[] {};
		for (Rule rule : game.rules) {
			if (rule instanceof Module mod) {
				optionList = combineOptionLists(optionList, mod.getActions(game));
				optionList = combineOptionLists(optionList, mod.getActions(game));
				optionList = combineOptionLists(optionList, mod.getRules(game), new Rule[] { mod });
			}
		}
		Option selectedOption = random.choice(optionList);
		if (selectedOption == null) return null;
		if (selectedOption instanceof Rule ruleOption) return Rule.checkFor(game, ruleOption);
		else return selectedOption;
	}
}