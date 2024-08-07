package com.sillypantscoder.votegame.modules;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import com.sillypantscoder.votegame.Game;
import com.sillypantscoder.votegame.Module;
import com.sillypantscoder.votegame.Option;
import com.sillypantscoder.votegame.Player;
import com.sillypantscoder.votegame.random;

public class ModuleMain extends Module {
	public ModuleMain(Game game) {
		super(game);
	}
	public String getModuleName() {
		return "Basic Actions";
	}
	public void getOptions(Consumer<Option> list) {
		// Actions
		for (int i = 15; i < game.roundNumber; i++) list.accept(Victory.create(game));
		list.accept(AloneVictory.create(game));
		list.accept(AloneVictory.create(game));
		for (int i = 10; i < game.roundNumber; i += 2) {
			list.accept(AloneVictory.create(game));
		}
		// list.accept(SelectRule.create(game));
		// Rules
		if (game.players.size() >= 3) list.accept(AcceptZeroVotes.create(game));
		list.accept(RepeatedAloneVictory.create(game));
		if (game.roundNumber >= 10) list.accept(RepeatedAloneVictory.create(game));
		for (int i = 15; i < game.roundNumber; i++) list.accept(RepeatedAloneVictory.create(game));
	}
	public Option.Rule[] getAllRules() {
		return new Option.Rule[] {
			new AcceptZeroVotes(game)
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
		public String getName() { return target.displayName + " wins (if valid)"; }
		public String execute() {
			Optional<Option.Rule.WinCondition> invalidation = game.findWinInvalidations(target);
			if (invalidation.isEmpty()) {
				game.winner = target;
				return target.displayName + " wins!";
			} else return target.displayName + " cannot win because: " + invalidation.get();
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
			AtomicReference<String> results = new AtomicReference<String>("<span style='opacity: 0.3;'>");
			ArrayList<Player> validWinners = new ArrayList<Player>();
			for (Player target : game.players) {
				Optional<Option.Rule.WinCondition> inv = game.findWinInvalidations(target);
				inv.ifPresentOrElse((w) -> {
					// Not a valid winner
					results.set(results.get() + "- " + target.displayName + " cannot win because: " + w.getName() + "<br>");
				}, () -> {
					// Valid winner
					validWinners.add(target);
					results.set(results.get() + "- " + target.displayName + " can win!<br>");
				});
			}
			results.set(results.get() + "</span>");
			if (validWinners.size() == 1) {
				Player target = validWinners.get(0);
				game.winner = target;
				results.set(results.get() + target.displayName + " wins!");
			} else if (validWinners.size() == 0) results.set(results.get() + "No one can win!");
			else results.set(results.get() + "More than one player can win!");
			return results.get();
		}
	}
	public static class SelectRule extends Option.Action {
		public Option.Rule rule1;
		public Option.Rule rule2;
		public Game game;
		public SelectRule(Game game, Option.Rule rule1, Option.Rule rule2) {
			this.game = game;
			this.rule1 = rule1;
			this.rule2 = rule2;
		}
		public static SelectRule create(Game game) {
			ArrayList<Option.Rule> activeRules = game.rules;
			ArrayList<Option.Rule> possibleRules = new ArrayList<Option.Rule>();
			// Go through all the game's modules
			for (int i = 0; i < activeRules.size(); i++) {
				if (activeRules.get(i) instanceof Module mod) {
					// For each one...
					// Get the list of rules it provides
					Option.Rule[] newOptions = mod.getAllRules();
					// Go through the list
					for (Option.Rule newRule : newOptions) {
						// Don't include rules we already have
						if (activeRules.indexOf(newRule) == -1) {
							// Remember this rule
							possibleRules.add(newRule);
						}
					}
				}
			}
			// System.err.println("done");
			// Select some rules (from possibleRules)
			if (possibleRules.size() < 2) return null;
			random.shuffle(possibleRules);
			return new SelectRule(game, possibleRules.get(0), possibleRules.get(1));
		}
		public String getName() { return "Randomly add one of these two rules:\n- " + this.rule1.getName() + "\n- " + this.rule2.getName(); }
		public String execute() {
			Option.Rule selected = random.choice(new Option.Rule[] { rule1, rule2 });
			game.rules.add(selected);
			return "Added the rule: " + selected.getName();
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
		public void accept() {}
		public void repeal() {}
	}
	public static class RepeatedAloneVictory extends Option.Rule.RepeatRule {
		public RepeatedAloneVictory(Game game) {
			super(game, new AloneVictory(game));
		}
		public static RepeatedAloneVictory create(Game game) {
			return new RepeatedAloneVictory(game);
		}
		public String getName() {
			return "<span style='color: #200;'>At the end of every round, if there is exactly one player who can win, then they win</span>";
		}
		public String getSource() {
			return "alone-victory";
		}
		public boolean isDelayed() {
			return true;
		}
	}
}
