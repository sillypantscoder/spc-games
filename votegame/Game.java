import java.util.ArrayList;

public class Game {
	public ArrayList<Player> players;
	public Option[] options;
	public boolean voteFinished;
	public ArrayList<Option.Rule> rules;
	public float rulePointMultiplier;
	public boolean ruleZeroVotes;
	public Game() {
		players = new ArrayList<Player>();
		rules = new ArrayList<Option.Rule>();
		rules.add(new ModuleMain(this)); // The main module
		voteFinished = false;
		options = new Option[] {};
		// Rules
		rulePointMultiplier = 1;
	}
	public Player findPlayer(int hashCode) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).hashCode() == hashCode) {
				return players.get(i);
			}
		}
		return null;
	}
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
	}
	public void _createOptions() {
		int n = random.choice(new Integer[] { 2, 2, 2, 3, 3, 3, 4, 4, 5 });
		Option[] oa = new Option[n];
		int realN = 0;
		for (int i = 0; i < n; i++) {
			Option r = Option.create(this);
			oa[i] = r;
			if (r != null) realN += 1;
		}
		if (realN <= 1) {
			createOptions();
			return;
		}
		options = new Option[realN];
		int ci = 0;
		for (int i = 0; i < oa.length; i++) {
			if (oa[i] != null) {
				options[ci] = oa[i];
				ci += 1;
			}
		}
	}
	public JsonEncoder.ArrayValue getPlayerData() {
		JsonEncoder.Value[] datas = new JsonEncoder.Value[players.size()];
		for (int i = 0; i < players.size(); i++) {
			datas[i] = new JsonEncoder.ObjectValue(
				new String[] { "name", "score", "hasVoted" },
				new JsonEncoder.Value[] {
					new JsonEncoder.StringValue(players.get(i).name),
					new JsonEncoder.IntValue(players.get(i).score),
					new JsonEncoder.BooleanValue(players.get(i).vote != -1)
				}
			);
		}
		return new JsonEncoder.ArrayValue(datas);
	}
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
	public HttpResponse get(String path) {
		if (path.equals("/")) {
			// Login page
			return new HttpResponse()
				.addHeader("Content-Type", "text/html")
				.setBody(Utils.readFile("assets/login.html"));
		} else if (path.startsWith("/createuser")) {
			// Create a new player
			String name = path.substring("/createuser?name=".length());
			Player newPlayer = new Player(Utils.decodeURIComponent(name));
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
				.setStatus(404)
				.setBody(thisPlayer.name);
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
							new String[] { "users", "choices", "choiceTypes" },
							new JsonEncoder.Value[] {
								getPlayerData(),
								JsonEncoder.stringList(options),
								getChoiceTypes()
							}
						)
					}
				)).encode());
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
				// - Check whether we are all done voting
				for (int i = 0; i < players.size(); i++) {
					if (players.get(i).vote == -1) return new HttpResponse();
				}
				// - We are done voting if we get to this point!
				finishVote();
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
				// - Check whether we are all done looking at the results
				for (int i = 0; i < players.size(); i++) {
					if (players.get(i).vote == -1) return new HttpResponse();
				}
				// - We are done if we get to this point!
				newVote();
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
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i).vote == -1) return new HttpResponse();
			}
			if (voteFinished) newVote();
			else finishVote();
			return new HttpResponse();
		} else {
			// 404!
			return new HttpResponse()
				.setStatus(404)
				.setBody("404");
		}
	}
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
						new String[] { "users", "choices", "choiceTypes" },
						new JsonEncoder.Value[] {
							getPlayerData(),
							JsonEncoder.stringList(options),
							getChoiceTypes()
						}
					)
				}
			)).encode());
		}
	}
	public void finishVote() {
		// 1. Figure out how many votes there were for each option
		int[] votes = new int[options.length];
		for (int i = 0; i < players.size(); i++) {
			votes[players.get(i).vote] += 1;
		}
		// 2. Figure out what options got accepted
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
		if (ruleZeroVotes) {
			for (int i = 0; i < options.length; i++) {
				if (votes[i] == 0) {
					accepted.add(options[i]);
					effectSources.add("zero-votes");
				}
			}
		}
		// if (ruleRandomOption) {
		// 	accepted.add(random.choice(options));
		// 	effectSources.add("random");
		// }
		// 3. Execute all the accepted options
		ArrayList<String> effects = new ArrayList<String>();
		//		Effect types: The bold text to display before the effect. One of "rule" "rule-accept" "rule-repeal" "module-add" "module-remove"
		ArrayList<String> effectTypes = new ArrayList<String>();
		ArrayList<JsonEncoder.ArrayValue> playerDatas = new ArrayList<JsonEncoder.ArrayValue>();
		playerDatas.add(getPlayerData());
		for (int i = 0; i < accepted.size(); i++) {
			Option item = accepted.get(i);
			if (item instanceof Option.Action actionItem) {
				effectTypes.add("action");
				effects.add(actionItem.execute());
			} else if (item instanceof Option.Rule ruleItem) {
				if (rules.contains(ruleItem)) {
					ruleItem.repeal();
					rules.remove(ruleItem);
					if (item instanceof Module) effectTypes.add("module-remove");
					else effectTypes.add("rule-repeal");
				} else {
					ruleItem.accept();
					rules.add(ruleItem);
					if (item instanceof Module) effectTypes.add("module-add");
					else effectTypes.add("rule-accept");
				}
				effects.add(ruleItem.getName());
			}
			playerDatas.add(getPlayerData());
		}
		// 4. Execute any rules
		for (int i = 0; i < rules.size(); i++) {
			if (rules.get(i) instanceof Option.Rule.RepeatRule r) {
				effects.add(r.action.execute());
				effectSources.add(r.getSource());
				effectTypes.add("action");
				playerDatas.add(getPlayerData());
			}
		}
		// if (ruleRandomPenalty) {
		// 	Player r = random.choice(players);
		// 	r.score -= 6;
		// 	effects.add("Took 6 points from " + r.name);
		// 	effectSources.add("random-penalty");
		// 	effectTypes.add("action");
		// 	playerDatas.add(getPlayerData());
		// }
		// 4. Reset everyone's votes
		for (int i = 0; i < players.size(); i++) {
			players.get(i).vote = -1;
		}
		voteFinished = true;
		// 5. Notify all the players about the voting results
		for (int i = 0; i < players.size(); i++) {
			players.get(i).fire((new JsonEncoder.ObjectValue(
				new String[] { "type", "users", "info" },
				new JsonEncoder.Value[] {
					new JsonEncoder.StringValue("votingfinished"),
					getPlayerData(),
					new JsonEncoder.ObjectValue(
						new String[] { "votes", "effects", "effectTypes", "effectSources", "rules", "playerDatas" },
						new JsonEncoder.Value[] {
							JsonEncoder.intList(votes),
							JsonEncoder.stringList(effects),
							JsonEncoder.stringList(effectTypes),
							JsonEncoder.stringList(effectSources),
							JsonEncoder.stringList(rules),
							JsonEncoder.objectList(playerDatas)
						}
					)
				}
			)).encode());
		}
	}
}
