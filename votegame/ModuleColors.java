import java.util.ArrayList;
import java.util.function.Consumer;

public class ModuleColors extends Module {
	public ModuleColors(Game game) {
		super(game);
	}
	public String getModuleName() {
		return "Colors";
	}
	public static enum Color {
		Red, Orange, Yellow, Green, Blue, Purple;
		public ModuleColors.Color inverted() {
			if (this == Red) return Green;
			if (this == Orange) return Blue;
			if (this == Yellow) return Purple;
			if (this == Green) return Red;
			if (this == Blue) return Orange;
			if (this == Purple) return Yellow;
			return random.choice(values());
		}
		public boolean type() {
			return this == Red || this == Orange || this == Yellow;
		}
	}
	public void accept() {
		for (int i = 0; i < game.players.size(); i++) {
			Player p = game.players.get(i);
			p.color = random.choice(Color.values());
		}
	}
	public void getOptions(Consumer<Option> list) {
		// Actions
		list.accept(ChangeColor.create(game));
		list.accept(ChangeColor.create(game));
		list.accept(ChangeColor.create(game));
		list.accept(ChangeColor.create(game));
		list.accept(RandomizeAllColors.create(game));
		list.accept(InvertAllColors.create(game));
		// Actions with Points
		if (game.hasRule(ModulePoints.class)) {
			list.accept(ColorToPoints.create(game));
			list.accept(ColorToPoints.create(game));
			list.accept(ColorToPoints.create(game));
			list.accept(ColorTypeToPoints.create(game));
			list.accept(ColorTypeToPoints.create(game));
			list.accept(ColorTypeToPoints.create(game));
			list.accept(ColorTypeToPoints.create(game));
			list.accept(ColorTypeToPoints.create(game));
		}
		// Rules
		list.accept(RepeatedColorToPoints.create(game));
		list.accept(RepeatedColorToPoints2.create(game));
		list.accept(RequireColor.create(game));
		list.accept(RequireColorType.create(game));
	}
	public Option.Rule[] getAllRules() {
		return new Option.Rule[] {
			new RepeatedColorToPoints(game),
			new RepeatedColorToPoints2(game),
			new RequireColor(game, Color.Red),
			new RequireColorType(game, random.choice(new Boolean[] { true, false }))
		};
	}
	// === ACTIONS ===
	public static class ChangeColor extends Option.Action {
		public Player target;
		public Color newColor;
		public Game game;
		public ChangeColor(Game game, Player target, Color color) {
			this.game = game;
			this.target = target;
			this.newColor = color;
		}
		public static ChangeColor create(Game game) {
			if (game.players.size() == 0) return null;
			Player target = random.choice(game.players);
			Color newColor = random.choice(Color.values());
			if (target.color == newColor) return null;
			return new ChangeColor(game, target, newColor);
		}
		public String getName() {
			return "Set " + target.displayName + "'s color to " + newColor.name();
		}
		public String execute() {
			target.color = newColor;
			return "Set " + target.displayName + "'s color to " + newColor.name();
		}
	}
	public static class RandomizeAllColors extends Option.Action {
		public Game game;
		public RandomizeAllColors(Game game) {
			this.game = game;
		}
		public static RandomizeAllColors create(Game game) {
			return new RandomizeAllColors(game);
		}
		public String getName() {
			return "Randomize everybody's colors!";
		}
		public String execute() {
			for (int i = 0; i < game.players.size(); i++) {
				Player p = game.players.get(i);
				p.color = random.choice(Color.values());
			}
			return "Randomized everybody's colors!";
		}
	}
	public static class InvertAllColors extends Option.Action {
		public Game game;
		public InvertAllColors(Game game) {
			this.game = game;
		}
		public static InvertAllColors create(Game game) {
			return new InvertAllColors(game);
		}
		public String getName() {
			return "Invert everybody's colors!";
		}
		public String execute() {
			for (int i = 0; i < game.players.size(); i++) {
				Player p = game.players.get(i);
				p.color = p.color.inverted();
			}
			return "Inverted everybody's colors!";
		}
	}
	public static class ColorToPoints extends Option.Action {
		public int amount;
		public Color color;
		public Game game;
		public ColorToPoints(Game game, int amount, Color color) {
			this.game = game;
			this.amount = amount;
			this.color = color;
		}
		public static ColorToPoints create(Game game) {
			int amount = random.choice(new Integer[] { 5, 10, 20 });
			Color c = random.choice(Color.values());
			for (var i = 0; i < game.players.size(); i++) {
				if (game.players.get(i).color == c) return new ColorToPoints(game, amount, c);
			}
			return null;
		}
		public String getName() {
			return "Everyone with the color " + color.name() + " gets " + amount + " points";
		}
		public String execute() {
			ArrayList<String> targets = new ArrayList<String>();
			for (int i = 0; i < game.players.size(); i++) {
				Player p = game.players.get(i);
				if (p.color == color) {
					p.score += amount * game.getPointMultiplier();
					targets.add(p.displayName);
				}
			}
			if (targets.size() == 0) return "Gave no one points";
			if (targets.size() == 1) return "Gave " + targets.get(0) + " " + amount + " points";
			return "Gave " + Utils.humanJoinList(targets) + " " + amount + " points each";
		}
	}
	public static class ColorTypeToPoints extends Option.Action {
		public int amount;
		public boolean color;
		public Game game;
		public ColorTypeToPoints(Game game, int amount, boolean color) {
			this.game = game;
			this.amount = amount;
			this.color = color;
		}
		public static ColorTypeToPoints create(Game game) {
			int amount = random.choice(new Integer[] { 5, 10, 20 });
			boolean c = random.choice(new Boolean[] { true, false });
			return new ColorTypeToPoints(game, amount, c);
		}
		public String getName() {
			return "Everyone with a " + (color ? "warm" : "cool") + " color gets " + amount + " points";
		}
		public String execute() {
			ArrayList<String> targets = new ArrayList<String>();
			for (int i = 0; i < game.players.size(); i++) {
				Player p = game.players.get(i);
				if (p.color.type() == color) {
					p.score += amount * game.getPointMultiplier();
					targets.add(p.displayName);
				}
			}
			if (targets.size() == 0) return "Gave no one points";
			if (targets.size() == 1) return "Gave " + targets.get(0) + " " + amount + " points";
			return "Gave " + Utils.humanJoinList(targets) + " " + amount + " points each";
		}
	}
	// === RULES ===
	public static class RepeatedColorToPoints extends Option.Rule.RepeatRule {
		public RepeatedColorToPoints(Game game) {
			super(game, ColorToPoints.create(game));
		}
		public static RepeatedColorToPoints create(Game game) {
			return new RepeatedColorToPoints(game);
		}
		public String getSource() { return "color-to-points"; }
	}
	public static class RepeatedColorToPoints2 extends Option.Rule.RepeatRule {
		public RepeatedColorToPoints2(Game game) {
			super(game, ColorToPoints.create(game));
		}
		public static RepeatedColorToPoints2 create(Game game) {
			return new RepeatedColorToPoints2(game);
		}
		public String getSource() { return "color-to-points"; }
	}
	public static class RequireColor extends Option.Rule.WinCondition {
		public Color type;
		public RequireColor(Game game, Color type) {
			super(game);
			this.type = type;
		}
		public static RequireColor create(Game game) {
			Color type = random.choice(Color.values());
			return new RequireColor(game, type);
		}
		public String getName() { return "Require having a " + type.name() + " color to win"; }
		public boolean isPlayerValid(Player target) {
			return target.color == this.type;
		}
	}
	public static class RequireColorType extends Option.Rule.WinCondition {
		public boolean type;
		public RequireColorType(Game game, boolean type) {
			super(game);
			this.type = type;
		}
		public static RequireColorType create(Game game) {
			boolean type = random.choice(new Boolean[] { true, false });
			return new RequireColorType(game, type);
		}
		public String getName() { return "Require having a " + (type ? "warm" : "cool") + " color to win"; }
		public boolean isPlayerValid(Player target) {
			return target.color.type() == this.type;
		}
	}
}
