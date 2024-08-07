package com.sillypantscoder.votegame;

import java.util.ArrayList;
import java.util.Optional;

import com.sillypantscoder.spcgames.AssetLoader;
import com.sillypantscoder.spcgames.JSON;
import com.sillypantscoder.spcgames.Utils;
import com.sillypantscoder.spcgames.http.HttpResponse;
import com.sillypantscoder.votegame.modules.ModuleMain;
import com.sillypantscoder.votegame.modules.ModulePoints;

public class Game {
	/**
	 * The list of players in the game.
	 */
	public ArrayList<Player> players;
	/**
	 * The list of options in this voting round.
	 */
	public Option[] options;
	/**
	 * Whether the voting has finished this round.
	 */
	public boolean voteFinished;
	/**
	 * The list of rules which are currently present.
	 */
	public ArrayList<Option.Rule> rules;
	/**
	 * The event that should be sent to a player when they join.
	 */
	public JSON.JValue joinEvent;
	/**
	 * The current winner of the game. May be null.
	 */
	public Player winner;
	/**
	 * The current round number.
	 */
	public int roundNumber;
	public Game() {
		players = new ArrayList<Player>();
		options = new Option[] {};
		voteFinished = false;
		rules = new ArrayList<Option.Rule>();
		// The three basic rules:
		rules.add(new ModuleMain(this));
		rules.add(new ModulePoints(this));
		rules.add(new ModulePoints.WinOver35(this));
		// Init
		joinEvent = null;
		winner = null;
		roundNumber = 0;
	}
	/**
	 * Find a player based on a hash code.
	 * @param hashCode
	 * @return
	 */
	public Player findPlayer(int hashCode) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).hashCode() == hashCode) {
				return players.get(i);
			}
		}
		return null;
	}
	/**
	 * Create the options to be voted on this round.
	 */
	public void createOptions() {
		int numberOfOptions = random.choice(new Integer[] { 2, 3, 4, 5 });
		ArrayList<Option> selectedOptions = new ArrayList<Option>();
		for (int i = 0; i < numberOfOptions; i++) {
			Option option = Option.create(this);
			// not null
			if (option == null) continue;
			if (option instanceof Option.Rule.RepeatRule rrule) {
				if (rrule.action == null) continue;
			}
			// names are different
			boolean isDuplicate = false;
			for (int x = 0; x < selectedOptions.size(); x++) {
				if (selectedOptions.get(x).getName().equals(option.getName())) isDuplicate = true;
			}
			if (isDuplicate) continue;
			// finish
			selectedOptions.add(option);
		}
		options = new Option[selectedOptions.size()];
		for (int i = 0; i < options.length; i++) options[i] = selectedOptions.get(i);
		if (options.length == 1) createOptions();
	}
	/**
	 * Get the "player data".
	 * @return
	 */
	public JSON.JList<? extends JSON.JValue> getPlayerData() {
		JSON.JValue[] datas = new JSON.JValue[players.size()];
		for (int i = 0; i < players.size(); i++) {
			datas[i] = JSON.JObject.create(
				new String[] { "realName", "displayName", "score", "hasStar", "color", "hasVoted", "winner" },
				new JSON.JValue[] {
					new JSON.JString(players.get(i).realName),
					new JSON.JString(players.get(i).displayName),
					new JSON.JNumber(players.get(i).score),
					new JSON.JBoolean(players.get(i).hasStar),
					new JSON.JString(players.get(i).color.name()),
					new JSON.JBoolean(players.get(i).vote != -1),
					new JSON.JBoolean(players.get(i) == winner)
				}
			);
		}
		return new JSON.JList<JSON.JValue>(datas);
	}
	/**
	 * Get the types for the options this round.
	 * @return
	 */
	public JSON.JList<JSON.JString> getChoiceTypes() {
		JSON.JString[] datas = new JSON.JString[options.length];
		for (int i = 0; i < options.length; i++) {
			String type = "action";
			if (options[i] instanceof Option.Rule) {
				if (options[i] instanceof Module) {
					type = "module-";
					if (rules.contains(options[i])) type += "remove";
					else type += "add";
				} else {
					type = "rule-";
					if (rules.contains(options[i])) type += "repeal";
					else type += "accept";
				}
			}
			datas[i] = new JSON.JString(type);
		}
		return new JSON.JList<JSON.JString>(datas);
	}
	/**
	 * Check whether this game has a rule of the specified type.
	 * @param mod
	 * @return
	 */
	public boolean hasRule(Class<? extends Option.Rule> mod) {
		for (int i = 0; i < rules.size(); i++) {
			if (rules.get(i).getClass().equals(mod)) {
				return true;
			}
		}
		return false;
	}
	public float getPointMultiplier() {
		float result = 1;
		if (hasRule(ModulePoints.MultiplyPointChanges.class)) result *= 2;
		if (hasRule(ModulePoints.InvertPointChanges.class)) result *= -1;
		return result;
	}
	/**
	 * Go through the list of rules to find a rule that prevents this player from winning.
	 * @param p
	 * @return
	 */
	public Optional<Option.Rule.WinCondition> findWinInvalidations(Player p) {
		for (int i = 0; i < rules.size(); i++) {
			if (rules.get(i) instanceof Option.Rule.WinCondition condition) {
				if (! condition.isPlayerValid(p)) return Optional.ofNullable(condition);
			}
		}
		return Optional.empty();
	}
	public HttpResponse get(String path) {
		if (path.equals("/")) {
			// Login page
			return new HttpResponse()
				.addHeader("Content-Type", "text/html")
				.setBody(AssetLoader.getResource("com/sillypantscoder/votegame/assets/login.html"));
		} else if (path.startsWith("/createuser")) {
			// Find the player's name
			String[] names = Utils.decodeURIComponent(path.substring("/createuser?name=".length())).split("_____");
			String realName = names[0];
			String displayName = names[1];
			if (realName.length() >= 20 || displayName.length() >= 20) return new HttpResponse()
				.addHeader("Content-Type", "text/html")
				.setBody("<body><h3>Nice try!!!</h3><a href='/'>Back Home</a></body>");
			// Check if the player already exists
			for (int i = 0; i < this.players.size(); i++) {
				if (this.players.get(i).realName.equals(realName)) {
					System.err.println("[voting-game] Re-login old player - " + realName);
					return new HttpResponse()
						.addHeader("Content-Type", "text/html")
						.setBody("<script>location.replace('./game?user=" + this.players.get(i).hashCode() + "')</script>");
				}
			}
			// Create a new player
			Player newPlayer = new Player(realName, displayName);
			players.add(newPlayer);
			System.err.println("[voting-game] Added new player - " + realName);
			return new HttpResponse()
				.addHeader("Content-Type", "text/html")
				.setBody("<script>location.replace('./game?user=" + newPlayer.hashCode() + "')</script>");
		} else if (path.startsWith("/whoami")) {
			// Create a new player
			String hashString = path.substring("/whoami?user=".length());
			int hashCode = Integer.parseInt(hashString);
			// Make sure the player isn't null
			Player thisPlayer = findPlayer(hashCode);
			if (thisPlayer == null) return new HttpResponse()
				.setStatus(404)
				.setBody("???");
			// Return the name
			return new HttpResponse()
				.setStatus(200)
				.setBody(thisPlayer.realName);
		} else if (path.equals("/game.js")) {
			return new HttpResponse()
				.addHeader("Content-Type", "text/html")
				.setBody(AssetLoader.getResource("com/sillypantscoder/votegame/assets/game.js"));
		} else if (path.startsWith("/game")) {
			if (options.length == 0 && players.size() > 1) newVote();
			// Main game
			String hashString = path.substring("/game?user=".length());
			int hashCode = Integer.parseInt(hashString);
			// Make sure the player isn't null
			Player thisPlayer = findPlayer(hashCode);
			if (thisPlayer == null) return new HttpResponse()
				.setStatus(404)
				.setBody("<script>location.replace('/')</script>");
			// Determine what screen to send
			if (! voteFinished) {
				Game.queue(thisPlayer, JSON.JObject.create(
					new String[] { "type", "voteinfo" },
					new JSON.JValue[] {
						new JSON.JString("votingscreen"),
						JSON.JObject.create(
							new String[] { "users", "choices", "choiceTypes", "rules" },
							new JSON.JValue[] {
								getPlayerData(),
								JSON.makeStringList(options),
								getChoiceTypes(),
								JSON.makeStringList(rules)
							}
						)
					}
				).toString());
			} else {
				Game.queue(thisPlayer, joinEvent.toString());
			}
			// Send the game
			return new HttpResponse()
				.addHeader("Content-Type", "text/html")
				.setBody(AssetLoader.getResource("com/sillypantscoder/votegame/assets/game.html"));
		} else if (path.startsWith("/messages")) {
			// Get new messages
			String hashString = path.substring("/messages?user=".length());
			int hashCode = Integer.parseInt(hashString);
			Player thisPlayer = findPlayer(hashCode);
			if (thisPlayer == null) return new HttpResponse()
				.setStatus(404)
				.setBody("<script>location.replace('/')</script>");
			return new HttpResponse()
				.addHeader("Content-Type", "text/html")
				.setBody(thisPlayer.getMessagesString());
		} else if (path.equals("/status")) {
			return new HttpResponse()
				.addHeader("Content-Type", "text/html")
				.setBody(this.winner != null ? "Finished" : "In progress");
		} else if (path.equals("/status_mod")) {
			String playerString = "";
			if (this.winner != null) {
				playerString += "<button onclick='var x = new XMLHttpRequest(); x.open(\"POST\", `../../game/${game_id}/continue`); x.send()'>Continue Playing</button>\n";
			}
			playerString += "\nPlayers:";
			for (int i = 0; i < players.size(); i++) {
				playerString += "\n- <button onclick='var x = new XMLHttpRequest(); x.open(\"POST\", `../../game/${game_id}/leave`); x.send(" + players.get(i).hashCode() + ")'>Remove</button><button onclick='var x = new XMLHttpRequest(); x.open(\"POST\", `../../game/${game_id}/rename`); x.send(" + players.get(i).hashCode() + " + \"___separator___\" + prompt(\"Enter the new display name:\"))'>Rename</button> " +
					players.get(i).realName + " (" + players.get(i).displayName + ")";
				if (this.voteFinished) {
					if (this.players.get(i).vote == -1) {
						playerString += " <button onclick='var x = new XMLHttpRequest(); x.open(\"POST\", `../../game/${game_id}/sendmessage`); x.send(\"" + players.get(i).hashCode() + "MESSAGESEPARATORr\")'>Ready</button>";
					}
				} else {
					if (this.players.get(i).vote == -1) {
						playerString += " ";
						for (int o = 0; o < options.length; o++) {
							playerString += "<button onclick='var x = new XMLHttpRequest(); x.open(\"POST\", `../../game/${game_id}/sendmessage`); x.send(\"" + players.get(i).hashCode() + "MESSAGESEPARATORv" + o + "\")'>Vote " + o + "</button>";
						}
					} else {
						playerString += " v:" + this.players.get(i).vote;
					}
				}
			}
			if (! this.voteFinished) {
				playerString += "\n\nVoting:";
				for (int o = 0; o < options.length; o++) {
					playerString += "\n" + o + " - " + options[o].getName();
				}
			}
			return new HttpResponse()
				.addHeader("Content-Type", "text/html")
				.setBody(playerString);
		} else {
			// 404!
			return new HttpResponse()
				.setStatus(404)
				.setBody("404");
		}
	}
	private static void queue(Player target, String event) {
		Thread t = new Thread(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace(System.err);
			}
			target.fire(event);
		});
		t.start();
	}
	public HttpResponse post(String path, String body) {
		if (path.equals("/sendmessage")) {
			// Message!
			// Figure out which player is sending it
			String hashString = body.split("MESSAGESEPARATOR")[0];
			int hashCode = Integer.parseInt(hashString);
			Player thisPlayer = findPlayer(hashCode);
			if (thisPlayer == null) return new HttpResponse()
				.setStatus(404)
				.setBody("404");
			// Figure out what the message says
			String message = body.split("MESSAGESEPARATOR")[1];
			if (message.substring(0, 1).equals("v")) {
				// The player has voted!
				// - Validate the vote
				if (voteFinished) return new HttpResponse().setStatus(400).setBody("Voting has already finished!");
				if (thisPlayer.vote != -1) return new HttpResponse().setStatus(400).setBody("You have already voted!");
				// - Figure out what the vote was
				String voteStr = message.substring(1);
				int voteInt = Integer.parseInt(voteStr);
				if (voteInt < 0 || voteInt >= options.length) return new HttpResponse().setStatus(400).setBody("This is not a valid option!");
				// - Record the vote
				thisPlayer.vote = voteInt;
				// - Notify other players of the vote
				for (int i = 0; i < players.size(); i++) {
					players.get(i).fire(JSON.JObject.create(
						new String[] { "type", "users" },
						new JSON.JValue[] {
							new JSON.JString("playerupdate"),
							getPlayerData()
						}
					).toString());
				}
				checkForStateChange();
				return new HttpResponse();
			} else if (message.equals("r")) {
				if (! voteFinished) return new HttpResponse().setStatus(400).setBody("Voting has not finished yet!");
				thisPlayer.vote = 0;
				for (int i = 0; i < players.size(); i++) {
					players.get(i).fire(JSON.JObject.create(
						new String[] { "type", "users" },
						new JSON.JValue[] {
							new JSON.JString("playerupdate"),
							getPlayerData()
						}
					).toString());
				}
				checkForStateChange();
				return new HttpResponse();
			}
			// Finish
			return new HttpResponse().setStatus(400).setBody("That is not a valid message type!");
		} else if (path.equals("/leave")) {
			// Figure out which player is sending it
			int hashCode = Integer.parseInt(body);
			Player thisPlayer = findPlayer(hashCode);
			if (thisPlayer == null) return new HttpResponse()
				.setStatus(404)
				.setBody("404");
			// Remove the player
			this.players.remove(thisPlayer);
			// Check if everyone is ready now
			checkForStateChange();
			return new HttpResponse();
		} else if (path.equals("/rename")) {
			String[] parts = body.split("___separator___");
			// Figure out which player is sending it
			int hashCode = Integer.parseInt(parts[0]);
			Player thisPlayer = findPlayer(hashCode);
			if (thisPlayer == null) return new HttpResponse()
				.setStatus(404)
				.setBody("404");
			// Rename the player
			thisPlayer.displayName = parts[1];
			// Update player data on clients
			for (int i = 0; i < players.size(); i++) {
				players.get(i).fire(JSON.JObject.create(
					new String[] { "type", "users" },
					new JSON.JValue[] {
						new JSON.JString("playerupdate"),
						getPlayerData()
					}
				).toString());
			}
			// Return
			return new HttpResponse();
		} else if (path.equals("/continue")) {
			if (winner == null) return new HttpResponse()
				.setStatus(404)
				.setBody("404");
			winner = null;
			newVote();
			return new HttpResponse();
		} else {
			// 404!
			return new HttpResponse()
				.setStatus(404)
				.setBody("404");
		}
	}
	/**
	 * Check whether everyone is ready, and go to the next scene if so.
	 */
	public void checkForStateChange() {
		// - Check whether we are all done voting
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).vote == -1) return;
		}
		// - Everyone is ready if we get to this point!
		if (voteFinished) newVote();
		else finishVote();
	}
	/**
	 * Called at the beginning of each round. Start the round by creating the options and informing the players.
	 */
	public void newVote() {
		voteFinished = false;
		roundNumber += 1;
		createOptions();
		for (int i = 0; i < players.size(); i++) {
			players.get(i).vote = -1;
		}
		for (int i = 0; i < players.size(); i++) {
			players.get(i).fire(JSON.JObject.create(
				new String[] { "type", "voteinfo" },
				new JSON.JValue[] {
					new JSON.JString("votingscreen"),
					JSON.JObject.create(
						new String[] { "users", "choices", "choiceTypes", "rules" },
						new JSON.JValue[] {
							getPlayerData(),
							JSON.makeStringList(options),
							getChoiceTypes(),
							JSON.makeStringList(rules)
						}
					)
				}
			).toString());
		}
	}
	/**
	 * Called when everyone has finished voting.
	 */
	public void finishVote() {
		// 1. Figure out how many votes there were for each option
		int[] votes = new int[options.length];
		for (int i = 0; i < players.size(); i++) {
			votes[players.get(i).vote] += 1;
		}
		// 2. Figure out what options got accepted
		//		Effect sources: How did this option get accepted?
		ArrayList<String> effectSources = new ArrayList<String>();
		ArrayList<Option> accepted = new ArrayList<Option>();
		int maxVotes = 0;
		for (int i = 0; i < options.length; i++) {
			if (votes[i] > maxVotes) {
				maxVotes = votes[i];
				accepted = new ArrayList<Option>();
				effectSources = new ArrayList<String>();
			}
			if (votes[i] == maxVotes) {
				accepted.add(options[i]);
				effectSources.add("voted-for");
			}
		}
		if (hasRule(ModuleMain.AcceptZeroVotes.class)) {
			for (int i = 0; i < options.length; i++) {
				if (votes[i] == 0) {
					accepted.add(options[i]);
					effectSources.add("zero-votes");
				}
			}
		}
		// 3. Execute all the accepted options
		ArrayList<String> effects = new ArrayList<String>();
		//		Effect types: The bold text to display before the effect. One of "rule" "rule-accept" "rule-repeal" "module-add" "module-remove"
		ArrayList<String> effectTypes = new ArrayList<String>();
		// `playerDatas` and `rulesets` are there to help with displaying how the game changed.
		ArrayList<JSON.JList<? extends JSON.JValue>> playerDatas = new ArrayList<JSON.JList<? extends JSON.JValue>>();
		playerDatas.add(getPlayerData());
		ArrayList<JSON.JList<? extends JSON.JValue>> rulesets = new ArrayList<JSON.JList<? extends JSON.JValue>>();
		rulesets.add(JSON.makeStringList(rules));
		// Go through the options
		for (int i = 0; i < accepted.size(); i++) {
			Option item = accepted.get(i);
			// Execute the option
			if (item instanceof Option.Action actionItem) {
				effectTypes.add("action");
				effects.add(actionItem.execute());
			} else if (item instanceof Option.Rule ruleItem) {
				if (rules.contains(ruleItem)) {
					rules.remove(ruleItem);
					if (item instanceof Module moduleItem) {
						ArrayList<String> removed = moduleItem.repealAndGetRemovedRules();
						effectTypes.add("module-remove");
						if (removed.size() > 0) {
							effects.add(ruleItem.getName() + "<br>Removed rules:<ul>" + String.join("", removed) + "</ul>");
						} else {
							effects.add(ruleItem.getName());
						}
					} else {
						ruleItem.repeal();
						effectTypes.add("rule-repeal");
						effects.add(ruleItem.getName());
					}
				} else {
					rules.add(ruleItem);
					ruleItem.accept();
					if (item instanceof Module) effectTypes.add("module-add");
					else effectTypes.add("rule-accept");
					effects.add(ruleItem.getName());
				}
			}
			playerDatas.add(getPlayerData());
			rulesets.add(JSON.makeStringList(rules));
			// Check if anyone won
			if (winner != null) {
				// Aaaaaaa!
				break;
			}
		}
		// 4. Execute any repeat rules
		for (int i = 0; i < rules.size(); i++) {
			// Check if anyone won
			if (winner != null) {
				// Aaaaaaa!
				break;
			}
			// Execute the repeat rule
			if (rules.get(i) instanceof Option.Rule.RepeatRule r) {
				if (! r.isDelayed()) {
					effects.add(r.action.execute());
					effectSources.add(r.getSource());
					effectTypes.add("action");
					playerDatas.add(getPlayerData());
					rulesets.add(JSON.makeStringList(rules));
				}
			}
		}
		// Later repeat rules
		for (int i = 0; i < rules.size(); i++) {
			// Check if anyone won
			if (winner != null) {
				// Aaaaaaa!
				break;
			}
			// Execute the repeat rule
			if (rules.get(i) instanceof Option.Rule.RepeatRule r) {
				if (r.isDelayed()) {
					effects.add(r.action.execute());
					effectSources.add(r.getSource());
					effectTypes.add("action");
					playerDatas.add(getPlayerData());
					rulesets.add(JSON.makeStringList(rules));
				}
			}
		}
		// 4. Reset everyone's votes
		for (int i = 0; i < players.size(); i++) {
			players.get(i).vote = -1;
		}
		voteFinished = true;
		// 5. Notify all the players about the voting results
		joinEvent = JSON.JObject.create(
			new String[] { "type", "users", "info" },
			new JSON.JValue[] {
				new JSON.JString("votingfinished"),
				getPlayerData(),
				JSON.JObject.create(
					new String[] { "votes", "effects", "effectTypes", "effectSources", "rules", "playerDatas", "rulesets", "winner" },
					new JSON.JValue[] {
						JSON.makeIntList(votes),
						JSON.makeStringList(effects),
						JSON.makeStringList(effectTypes),
						JSON.makeStringList(effectSources),
						JSON.makeStringList(rules),
						new JSON.JList<JSON.JList<? extends JSON.JValue>>(playerDatas),
						new JSON.JList<JSON.JList<? extends JSON.JValue>>(rulesets),
						winner==null ? new JSON.JNull() : new JSON.JString(winner.realName)
					}
				)
			}
		);
		for (int i = 0; i < players.size(); i++) {
			players.get(i).fire(joinEvent.toString());
		}
	}
}
