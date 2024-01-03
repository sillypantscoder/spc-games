Array.prototype.choice = function () { return this[Math.floor(Math.random() * this.length)]; }

function random() {
	var size = [
		[4, 6], // 38
		[6, 4], // 38
		[6, 4],
		[6, 6], // 62
		[8, 6]  // 86
	].choice()
	var board = []
	for (var y = 0; y < size[1]; y++) {
		board.push([])
		for (var x = 0; x < size[0]; x++) {
			board[y].push([
				"X", "A", "A"
			].choice())
		}
	}
	// spawn
	board[size[1] - 1][0] = "X"
	board[size[1] - 2][0] = "P"
	board[0][size[0] - 1] = "G"
	board[1][size[0] - 1] = "X"
	board[0][size[0] - 2] = "A"
	// finish
	var b = []
	for (var y = 0; y < board.length; y++) b.push(board[y].join(""))
	var s = b.join("R")
	location.replace("/game2.xml?" + s)
}