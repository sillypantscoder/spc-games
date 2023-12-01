/**
 * Data about a user's status.
 * @typedef {{name: string, score: number, hasStar: boolean, hasVoted: boolean, winner: boolean}} User
 */

var is_message_checker_broken = false
;(function messageChecker() {
	var x = new XMLHttpRequest()
	x.open("GET", "./messages" + location.search)
	x.addEventListener("loadend", (e) => {
		try {
			if (x.status != 200) throw new Error()
			var messages = x.responseText.split("\n")
			for (var i = 0; i < messages.length - 1; i++) {
				var info = JSON.parse(messages[i])
				switch (info.type) {
					case "votingscreen":
						showVotingScreen(info.voteinfo)
						break;
					case "playerupdate":
						updateUsers(info.users)
						break;
					case "votingfinished":
						showResults(info.info)
						updateUsers(info.users)
				}
			}
			if (! is_message_checker_broken) setTimeout(messageChecker, 400)
		} catch (e) {
			is_message_checker_broken = true
			document.querySelector('#broken').classList.remove('inactive')
			console.error(e)
		}
	})
	x.addEventListener("error", (e) => {
		is_message_checker_broken = true
		document.querySelector('#broken').classList.remove('inactive')
		console.error(e)
	})
	x.send()
})();
/**
 * Send a message to the server
 * @param {string} msg
 */
function sendMessage(msg) {
	var x = new XMLHttpRequest()
	x.open("POST", "./sendmessage")
	x.send(location.search.substring("?user=".length) + "MESSAGESEPARATOR" + msg)
}
;(() => {
	// @ts-ignore "userAgentData" doesn't exist
	if (navigator.userAgentData && navigator.userAgentData.mobile) {
		var value = 1.5
		// @ts-ignore
		document.body.parentNode.setAttribute('style', 'font-size: ' + String(value) + 'em;')
		// @ts-ignore
		document.querySelector("#text_size_control").value = value
	}
})();
var thisPlayerName = ""
/** @type {string[]} */
var player_rules = []
;(() => {
	var x = new XMLHttpRequest()
	x.open("GET", "./whoami" + location.search)
	x.addEventListener("loadend", (e) => {
		window.thisPlayerName = x.responseText
	})
	x.send()
})();
/**
 * Show the voting screen.
 * @param {{ users: User[], choices: string[], choiceTypes: string[], rules: string[] }} info
 */
function showVotingScreen(info) {
	player_rules = info.rules
	updateRules(player_rules)
	updateUsers(info.users)
	// UPDATE THE HEADERS
	document.querySelector("#header").innerHTML = "<h2>Voting</h2>"
	document.querySelector("#voting").innerHTML = ""
	document.querySelector("#desc").innerHTML = ""
	// GET THE OPTION LIST
	var choices = info.choices
	var m = document.createElement("table")
	m.appendChild(document.createElement("tbody"))
	for (var i = 0; i < choices.length; i++) {
		// SHOW THE OPTIONS
		var r = document.createElement("tr")
		r.appendChild(document.createElement("td"))
		r.children[0].innerHTML = "Vote"
		r.children[0].setAttribute("onclick", `vote(${i})`)
		r.appendChild(document.createElement("td"))
		r.children[1].innerHTML = ({
			"action": "",
			"rule-accept": "<div class='option-info' style='background: blue;'>New rule</div>",
			"rule-repeal": "<div class='option-info' style='background: red;'>Repeal a rule</div>",
			"module-add": "<div class='option-info' style='background: purple;'>Add to the game</div>",
			"module-remove": "<div class='option-info' style='background: magenta;'>Remove from the game</div>"
		}[info.choiceTypes[i]]) + choices[i]
		m.children[0].appendChild(r)
	}
	document.querySelector("#voting").appendChild(m)
}
/**
 * @param {number} n The index of the option that was voted for.
 */
function vote(n) {
	// CHANGE BUTTON TEXT TO VOTED
	// @ts-ignore
	document.querySelector(`#voting table tr:nth-child(${n + 1}) td[onclick]`).innerText = "Voted!"
	// SET SELECTED BUTTON
	document.querySelector(`#voting table tr:nth-child(${n + 1}) td[onclick]`).classList.add("selected")
	// SET NOT SELECTED BUTTONS
	document.querySelectorAll(`#voting table tr:not(:nth-child(${n + 1})) td[onclick]`).forEach((e) => e.classList.add("notselected"))
	// REMOVE CLICK HANDLERS
	document.querySelectorAll(`#voting table tr td[onclick]`).forEach((e) => e.removeAttribute("onclick"))
	// CREATE THE WAITING TEXT
	var e = document.createElement("div")
	e.innerHTML = `<div>Waiting for other players to vote...</div>`
	document.querySelector("#desc").appendChild(e)
	// TELL THE SERVER THAT WE VOTED
	sendMessage("v" + String(n))
}
/**
 * Show the results of the vote.
 * @param {{votes: number[], effects: string[], effectSources: string[], effectTypes: string[], rules: string[], playerDatas: User[][], rulesets: string[][], winner: string | null}} info The info about the results of the vote.
 */
function showResults(info) {
	// UPDATE THE HEADER
	document.querySelector("#header").innerHTML = ""
	var h = document.createElement("h2")
	h.innerHTML = "Results"
	document.querySelector("#header").appendChild(h)
	// SHOW THE NUMBER OF VOTES FOR EACH OPTION
	for (var i = 0; i < info.votes.length; i++) {
		/** @type {HTMLElement} */
		var target = document.querySelector(`#voting table tr:nth-child(${i + 1}) td:first-child`)
		if (target == null) target = document.querySelector("#voting").appendChild(document.createElement("div"))
		target.innerText = `${info.votes[i]} vote${info.votes[i]==1 ? '' : 's'}`
	}
	// SHOW THE LIST OF EFFECTS
	var infoElm = document.querySelector("#desc > div")
	if (infoElm == null) infoElm = document.querySelector("#desc").appendChild(document.createElement("div"))
	infoElm.innerHTML = ""
	for (var i = 0; i < info.effects.length; i++) {
		player_rules = info.rulesets[i]
		infoElm.appendChild(createUserElm(i != 0 ? info.playerDatas[i - 1] : null, info.playerDatas[i]))
		var c = document.createElement("div")
		c.setAttribute("class", "effect")
		// @ts-ignore
		var effectSource = {
			"voted-for": "<div class='icon' style='background: darkgreen;'>&#x2714; Voted for</div>",
			"zero-votes": "<div class='icon' style='background: cyan; color: black;'>&#x1F6AB; Zero votes</div>",
			"random": "<div class='icon' style='background: purple;'>&#x2684; Randomly selected</div>",
			"last-place-bonus": "<div class='icon' style='background: blue;'>&#x2913; Lowest score bonus</div>",
			"first-place-penalty": "<div class='icon' style='background: red;'>&#x21A5; Highest score penalty</div>",
			"random-penalty": "<div class='icon' style='background: bisque;'>&#x21BB; Random penalty</div>",
			"star-to-points": "<div class='icon' style='background: orange;'>&#9734; Star to points</div>",
			"score-wrap": "<div class='icon' style='background: teal;'><svg style='width: 1em; height: 1em;' viewBox='0 0 24 24'><path d='M 20 8 H 17.19 C 16.74 7.22 16.12 6.55 15.37 6.04 L 17 4.41 L 15.59 3 L 13.42 5.17 C 12.96 5.06 12.49 5 12 5 C 11.51 5 11.04 5.06 10.59 5.17 L 8.41 3 L 7 4.41 L 8.62 6.04 C 7.88 6.55 7.26 7.22 6.81 8 H 4 V 10 H 6.09 C 6.04 10.33 6 10.66 6 11 V 12 H 4 V 14 H 6 V 15 C 6 15.34 6.04 15.67 6.09 16 H 4 V 18 H 6.81 C 7.85 19.79 9.78 21 12 21 S 16.15 19.79 17.19 18 H 20 V 16 H 17.91 C 17.96 15.67 18 15.34 18 15 V 14 H 20 V 12 H 18 V 11 C 18 10.66 17.96 10.33 17.91 10 H 20 V 8 Z M 14 16 H 10 V 14 H 14 V 16 Z M 14 12 H 10 V 10 H 14 V 12 Z' fill='white' /></svg> Integer overflow</div>",
			"single-vote-bonus": "<div class='icon' style='background: aqua;'>&#128579; Creativity bonus</div>"
		}[info.effectSources[i]];
		c.innerHTML = effectSource + ({
			"action": "",
			"rule-accept": "<b style='color: blue;'>New rule: </b>",
			"rule-repeal": "<b style='color: red;'>Repealed a rule: </b>",
			"module-add": "<b style='color: purple;'>Added: </b>",
			"module-remove": "<b style='color: magenta;'>Removed: </b>"
		}[info.effectTypes[i]]) + info.effects[i]
		infoElm.appendChild(c)
	}
	player_rules = info.rules
	infoElm.appendChild(createUserElm(info.playerDatas[i - 1], info.playerDatas[i]))
	// ADD THE CONTINUE BUTTON
	if (info.winner == null) {
		var c = document.createElement("div")
		c.innerHTML = `<button onclick="ready(this.parentNode)">Continue</button>`
		infoElm.appendChild(c)
	} else {
		var c = document.createElement("div")
		c.innerHTML = `<b></b><span> is the winner!</span>`
		c.setAttribute("style", "display: inline-block; margin: 2em; border: 1em solid gold; border-radius: 2em; padding: 2em; background: #fd02; box-shadow: 0 0.5em 2em 0 black;")
		// @ts-ignore
		c.children[0].innerText = info.winner
		infoElm.appendChild(c)
	}
	// UPDATE THE RULE LIST
	updateRules(info.rules)
}
/**
 * @param {HTMLElement} e The button's parent.
 */
function ready(e) {
	e.setAttribute("style", "margin-top: 1em;")
	e.innerText = "Waiting for other players to finish..."
	sendMessage("r")
}
/**
 * Update the rule list with the list of rules.
 * @param {string[]} info The list of rules.
 */
function updateRules(info) {
	console.log(info)
	var ruleElm = document.querySelector("#rules")
	ruleElm.innerHTML = ""
	for (var i = 0; i < info.length; i++) {
		if (info[i] == "Add Basic Actions to the game") continue
		var c = document.createElement("li")
		c.innerText = info[i]
		ruleElm.appendChild(c)
	}
}
/**
 * Update the user list at the bottom of the screen.
 * @param {User[]} info
 */
function updateUsers(info) {
	/** @type {HTMLElement} */
	var t = document.querySelector("#userlist")
	t.innerText = ""
	t.appendChild(createUserElm(null, info))
}
/**
 * Create an element showing the players' status.
 * @param {User[] | null} prev The previous status to show changes from. May be null.
 * @param {User[]} info The player data.
 * @returns {HTMLDivElement} The created element
 */
function createUserElm(prev, info) {
	var t = document.createElement("div")
	t.classList.add("users")
	if (player_rules == undefined) return t
	for (var i = 0; i < info.length; i++) {
		var e = document.createElement("div")
		// Skeleton structure
		e.innerHTML = `<div>${info[i].name}</div><div></div>`
		// (You) marker
		if (thisPlayerName == info[i].name) e.children[0].appendChild(document.createElement("b")).innerText = " (You)"
		// Checkmark finished icon + winner icon
		if (info[i].hasVoted) e.classList.add("finished")
		if (info[i].winner) e.classList.add("finishicon-winner")
		// Score code
		if (player_rules.includes("Add Points to the game")) {
			e.children[1].appendChild(document.createElement("span")).innerText = info[i].score.toString()
			// Annotation showing how scores have changed
			if (prev != null && prev[i].score != info[i].score) {
				var x = document.createElement("span")
				x.classList.add("score-annotation")
				if (prev[i].score < info[i].score) {
					x.innerText = "+" + (info[i].score - prev[i].score)
					x.setAttribute("style", "color: green;")
				}
				if (prev[i].score > info[i].score) {
					x.innerText = "-" + (prev[i].score - info[i].score)
					x.setAttribute("style", "color: red;")
				}
				e.appendChild(x)
			}
		}
		// Star code
		if (player_rules.includes("Add Stars to the game")) {
			if (info[i].hasStar) e.children[1].appendChild(document.createElement("span")).innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" style="width: 1em; height: 1em;" viewBox="0 0 48 45"><path d="M 24 0 L 30 17 H 48 L 34 28 L 39 45 L 24 35 L 9 45 L 14 28 L 0 17 H 18 Z" stroke="red" fill="yellow" /></svg>`
							else e.children[1].appendChild(document.createElement("span")).innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" style="width: 1em; height: 1em;" viewBox="0 0 48 45"><path d="M 24 0 L 30 17 H 48 L 34 28 L 39 45 L 24 35 L 9 45 L 14 28 L 0 17 H 18 Z" stroke="black" fill="none" /></svg>`
			if (player_rules.includes("Add Points to the game")) e.children[1].children[1].setAttribute("style", `width: 1em; height: 1em; margin-left: 0.5em;`)
		}
		// Finish
		t.appendChild(e)
	}
	return t
}
function leave() {
	var x = new XMLHttpRequest()
	x.open("POST", "/leave")
	x.addEventListener("loadend", () => {
		location.assign("/")
	})
	x.send(location.search.substr(1))
}