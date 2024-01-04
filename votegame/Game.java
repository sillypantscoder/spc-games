import java.util.ArrayList;
import java.util.Optional;

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
	public JsonEncoder.Value joinEvent;
	/**
	 * The current winner of the game. May be null.
	 */
	public Player winner;
	public Game() {
		players = new ArrayList<Player>();
		options = new Option[] {};
		voteFinished = false;
		rules = new ArrayList<Option.Rule>();
		// The three basic rules:
		rules.add(new ModuleMain(this));
		rules.add(new ModulePoints(this));
		rules.add(new ModulePoints.RequireHighestPoints(this));
		// Init
		joinEvent = null;
		winner = null;
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
		int n = random.choice(new Integer[] { 2, 3, 4, 5 });
		ArrayList<Option> oa = new ArrayList<Option>();
		for (int i = 0; i < n; i++) {
			Option r = Option.create(this);
			// not null
			if (r == null) continue;
			// names are different
			boolean hn = false;
			for (int x = 0; x < oa.size(); x++) {
				if (oa.get(x).getName().equals(r.getName())) hn = true;
			}
			if (hn) continue;
			// finish
			oa.add(r);
		}
		options = new Option[oa.size()];
		for (int i = 0; i < options.length; i++) options[i] = oa.get(i);
		if (options.length == 1) createOptions();
	}
	/**
	 * Get the "player data".
	 * @return
	 */
	public JsonEncoder.ArrayValue getPlayerData() {
		JsonEncoder.Value[] datas = new JsonEncoder.Value[players.size()];
		for (int i = 0; i < players.size(); i++) {
			datas[i] = new JsonEncoder.ObjectValue(
				new String[] { "name", "score", "hasStar", "color", "hasVoted", "winner" },
				new JsonEncoder.Value[] {
					new JsonEncoder.StringValue(players.get(i).name),
					new JsonEncoder.IntValue(players.get(i).score),
					new JsonEncoder.BooleanValue(players.get(i).hasStar),
					new JsonEncoder.StringValue(players.get(i).color.name()),
					new JsonEncoder.BooleanValue(players.get(i).vote != -1),
					new JsonEncoder.BooleanValue(players.get(i) == winner)
				}
			);
		}
		return new JsonEncoder.ArrayValue(datas);
	}
	/**
	 * Get the types for the options this round.
	 * @return
	 */
	public JsonEncoder.ArrayValue getChoiceTypes() {
		JsonEncoder.Value[] datas = new JsonEncoder.Value[options.length];
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
			datas[i] = new JsonEncoder.StringValue(type);
		}
		return new JsonEncoder.ArrayValue(datas);
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
				.setBody(Utils.readFile("assets/login.html"));
		} else if (path.startsWith("/createuser")) {
			// Create a new player
			String name = Utils.decodeURIComponent(path.substring("/createuser?name=".length()));
			if (name.length() >= 20) return new HttpResponse()
				.addHeader("Content-Type", "text/html")
				.setBody("<body><h3>Nice try!!!</h3><a href='/'>Back Home</a></body>");
			Player newPlayer = new Player(name);
			players.add(newPlayer);
			System.err.println("[voting-game] Added new player - " + name);
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
				.setBody(thisPlayer.name);
		} else if (path.equals("/game.js")) {
			return new HttpResponse()
				.addHeader("Content-Type", "text/html")
				.setBody(Utils.readFile("assets/game.js"));
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
				VoteHttpHandler.queue(3, thisPlayer, (new JsonEncoder.ObjectValue(
					new String[] { "type", "voteinfo" },
					new JsonEncoder.Value[] {
						new JsonEncoder.StringValue("votingscreen"),
						new JsonEncoder.ObjectValue(
							new String[] { "users", "choices", "choiceTypes", "rules" },
							new JsonEncoder.Value[] {
								getPlayerData(),
								JsonEncoder.stringList(options),
								getChoiceTypes(),
								JsonEncoder.stringList(rules)
							}
						)
					}
				)).encode());
			} else {
				VoteHttpHandler.queue(3, thisPlayer, joinEvent.encode());
			}
			// Send the game
			return new HttpResponse()
				.addHeader("Content-Type", "text/html")
				.setBody(Utils.readFile("assets/game.html"));
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
				playerString += "\n- <button onclick='var x = new XMLHttpRequest(); x.open(\"POST\", `../../game/${game_id}/leave`); x.send(" + players.get(i).hashCode() + ")'>Remove</button><button onclick='var x = new XMLHttpRequest(); x.open(\"POST\", `../../game/${game_id}/rename`); x.send(" + players.get(i).hashCode() + " + \"___separator___\" + prompt(\"Enter the new name:\"))'>Rename</button> " + players.get(i).name;
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
					players.get(i).fire((new JsonEncoder.ObjectValue(
						new String[] { "type", "users" },
						new JsonEncoder.Value[] {
							new JsonEncoder.StringValue("playerupdate"),
							getPlayerData()
						}
					)).encode());
				}
				checkForStateChange();
				return new HttpResponse();
			} else if (message.equals("r")) {
				if (! voteFinished) return new HttpResponse().setStatus(400).setBody("Voting has not finished yet!");
				thisPlayer.vote = 0;
				for (int i = 0; i < players.size(); i++) {
					players.get(i).fire((new JsonEncoder.ObjectValue(
						new String[] { "type", "users" },
						new JsonEncoder.Value[] {
							new JsonEncoder.StringValue("playerupdate"),
							getPlayerData()
						}
					)).encode());
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
			thisPlayer.name = parts[1];
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
		createOptions();
		for (int i = 0; i < players.size(); i++) {
			players.get(i).vote = -1;
		}
		for (int i = 0; i < players.size(); i++) {
			players.get(i).fire((new JsonEncoder.ObjectValue(
				new String[] { "type", "voteinfo" },
				new JsonEncoder.Value[] {
					new JsonEncoder.StringValue("votingscreen"),
					new JsonEncoder.ObjectValue(
						new String[] { "users", "choices", "choiceTypes", "rules" },
						new JsonEncoder.Value[] {
							getPlayerData(),
							JsonEncoder.stringList(options),
							getChoiceTypes(),
							JsonEncoder.stringList(rules)
						}
					)
				}
			)).encode());
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
		ArrayList<JsonEncoder.ArrayValue> playerDatas = new ArrayList<JsonEncoder.ArrayValue>();
		playerDatas.add(getPlayerData());
		ArrayList<JsonEncoder.ArrayValue> rulesets = new ArrayList<JsonEncoder.ArrayValue>();
		rulesets.add(JsonEncoder.stringList(rules));
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
					ruleItem.repeal();
					if (item instanceof Module) effectTypes.add("module-remove");
					else effectTypes.add("rule-repeal");
				} else {
					rules.add(ruleItem);
					ruleItem.accept();
					if (item instanceof Module) effectTypes.add("module-add");
					else effectTypes.add("rule-accept");
				}
				effects.add(ruleItem.getName());
			}
			playerDatas.add(getPlayerData());
			rulesets.add(JsonEncoder.stringList(rules));
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
				effects.add(r.action.execute());
				effectSources.add(r.getSource());
				effectTypes.add("action");
				playerDatas.add(getPlayerData());
				rulesets.add(JsonEncoder.stringList(rules));
			}
		}
		// 4. Reset everyone's votes
		for (int i = 0; i < players.size(); i++) {
			players.get(i).vote = -1;
		}
		voteFinished = true;
		// 5. Notify all the players about the voting results
		joinEvent = new JsonEncoder.ObjectValue(
			new String[] { "type", "users", "info" },
			new JsonEncoder.Value[] {
				new JsonEncoder.StringValue("votingfinished"),
				getPlayerData(),
				new JsonEncoder.ObjectValue(
					new String[] { "votes", "effects", "effectTypes", "effectSources", "rules", "playerDatas", "rulesets", "winner" },
					new JsonEncoder.Value[] {
						JsonEncoder.intList(votes),
						JsonEncoder.stringList(effects),
						JsonEncoder.stringList(effectTypes),
						JsonEncoder.stringList(effectSources),
						JsonEncoder.stringList(rules),
						JsonEncoder.objectList(playerDatas),
						JsonEncoder.objectList(rulesets),
						winner==null ?
							new JsonEncoder.Value() {
								public String encode() {
									return "null";
								}
							} : new JsonEncoder.StringValue(winner.name)
					}
				)
			}
		);
		for (int i = 0; i < players.size(); i++) {
			players.get(i).fire(joinEvent.encode());
		}
	}
}
