import subprocess

subprocess.run(["git", "clone", "https://github.com/sillypantscoder/coltsuperexpress"])
f = open("coltsuperexpress/main.py", "w")
f.write('''import typing
import os
import json
import sys
from game import Game, Player, MoveForwardsCard, TurnCard, ChangeLevelCard, ShootCard, RevengeCard

class URLQuery:
\tdef __init__(self, q):
\t\tself.orig = q
\t\tself.fields = {}
\t\tfor f in q.split("&"):
\t\t\ts = f.split("=")
\t\t\tif len(s) >= 2:
\t\t\t\tself.fields[s[0]] = s[1]
\tdef get(self, key):
\t\tif key in self.fields:
\t\t\treturn self.fields[key]
\t\telse:
\t\t\treturn \'\'

def read_file(filename: str) -> bytes:
\t"""Read a file and return the contents."""
\tf = open(filename, "rb")
\tt = f.read()
\tf.close()
\treturn t

def write_file(filename: str, content: bytes):
\t"""Write data to a file."""
\tf = open(filename, "wb")
\tf.write(content)
\tf.close()

class HttpResponse(typing.TypedDict):
\t"""A dict containing an HTTP response."""
\tstatus: int
\theaders: dict[str, str]
\tcontent: str | bytes

game: Game = Game()

# game.addPlayer(Player("someone"))
# game.addPlayer(Player("someone else"))
# game.addPlayer(Player("a third person"))

def get(path: str, query: URLQuery) -> HttpResponse:
\t# playername = query.get("name")
\tif os.path.isfile("public_files" + path):
\t\treturn {
	\t\t\t"status": 200,
\t\t\t"headers": {
	\t\t\t\t"Content-Type": {
	\t\t\t\t\t"html": "text/html",
\t\t\t\t\t"js": "text/javascript",
\t\t\t\t\t"css": "text/css",
\t\t\t\t\t"svg": "image/svg+xml"
\t\t\t\t}[path.split(".")[-1]]
\t\t\t},
\t\t\t"content": read_file("public_files" + path)
\t\t}
\telif os.path.isdir("public_files" + path):
\t\treturn {
	\t\t\t"status": 200,
\t\t\t"headers": {
	\t\t\t\t"Content-Type": "text/html"
\t\t\t},
\t\t\t"content": read_file("public_files" + path + "index.html")
\t\t}
\telif path == "/status":
\t\treturn {
	\t\t\t"status": 200,
\t\t\t"headers": {
	\t\t\t\t"Content-Type": "application/json"
\t\t\t},
\t\t\t"content": json.dumps(game.toDict(), indent=\'\\t\')
\t\t}
\telif path.startswith("/card/"):
\t\tdata = path.split("/")[2:]
\t\tcard = {
	\t\t\t"move_forwards": MoveForwardsCard,
\t\t\t"turn": TurnCard,
\t\t\t"change_level": ChangeLevelCard,
\t\t\t"shoot": ShootCard,
\t\t\t"revenge": RevengeCard
\t\t}[data[0]]
\t\tfigure = game.players[int(data[1])].figure
\t\tcard(figure, game).execute()
\t\treturn {
	\t\t\t"status": 200,
\t\t\t"headers": {
	\t\t\t\t"Content-Type": "text/html"
\t\t\t},
\t\t\t"content": ""
\t\t}
\telse: # 404 page
\t\tprint("404 GET " + path, file=sys.stderr)
\t\treturn {
	\t\t\t"status": 404,
\t\t\t"headers": {
	\t\t\t\t"Content-Type": "text/html"
\t\t\t},
\t\t\t"content": ""
\t\t}

def post(path: str, body: str) -> HttpResponse:
\tbodydata = body.split("\\n")
\tif path == "/join_game":
\t\tgame.addPlayer(Player(bodydata[0]))
\t\treturn {
	\t\t\t"status": 200,
\t\t\t"headers": {},
\t\t\t"content": ""
\t\t}
\telif path == "/ready":
\t\tgame.readyPlayer(bodydata[0])
\t\treturn {
	\t\t\t"status": 200,
\t\t\t"headers": {},
\t\t\t"content": ""
\t\t}
\telif path == "/submit_plan":
\t\tgame.setPlan(bodydata)
\t\treturn {
	\t\t\t"status": 200,
\t\t\t"headers": {},
\t\t\t"content": ""
\t\t}
\telse:
\t\tprint("404 POST " + path, file=sys.stderr)
\t\treturn {
	\t\t\t"status": 404,
\t\t\t"headers": {
	\t\t\t\t"Content-Type": "text/html"
\t\t\t},
\t\t\t"content": ""
\t\t}

class MyServer:
\tdef handle_request(self):
\t\tr = json.loads(input())
\t\tres: HttpResponse = {
	\t\t\t"status": 404,
\t\t\t"headers": {},
\t\t\t"content": ""
\t\t}
\t\tif r["method"] == "GET":
\t\t\tres = self.do_GET(r["path"])
\t\tif r["method"] == "POST":
\t\t\tres = self.do_POST(r["path"], r["body"])
\t\tprint(res["status"], end="")
\t\tprint("|||", end="")
\t\tprint(",".join([f"{a}:{b}" for a, b in res["headers"].items()]), end="")
\t\tprint("|||", end="")
\t\tprint(repr(res["content"])[1:-1], end="")
\t\tprint()
\t\tsys.stdout.flush()
\t\t# print(f"Sent response", file=sys.stderr)
\tdef do_GET(self, path):
\t\tsplitpath = path.split("?")
\t\tres = get(splitpath[0], URLQuery(\'\'.join(splitpath[1:])))
\t\tc = res["content"]
\t\tif isinstance(c, bytes): c = c.decode("utf-8")
\t\treturn {
	\t\t\t"status": res["status"],
\t\t\t"headers": res["headers"],
\t\t\t"content": c
\t\t}
\tdef do_POST(self, path: str, body: bytes):
\t\tres = post(path, body)
\t\tc = res["content"]
\t\tif isinstance(c, bytes): c = c.decode("utf-8")
\t\treturn {
	\t\t\t"status": res["status"],
\t\t\t"headers": res["headers"],
\t\t\t"content": c
\t\t}

if __name__ == "__main__":
\trunning = True
\twebServer = MyServer()
\tprint(f"Fake server started", file=sys.stderr)
\t# sys.stdout.flush()
\twhile running:
\t\ttry:
\t\t\twebServer.handle_request()
\t\texcept KeyboardInterrupt:
\t\t\trunning = False
\tprint("Server stopped", file=sys.stderr)
''')
f.close()

f = open("coltsuperexpress/public_files/script.js", "r")
t = f.read().split("\n")
f.close()

t[38] = r'		x.send(body.replaceAll("\n", "\\n"))'
t[302] = r'	var d = await request("./status")'
t[502] = r'				if (! playerData.ready) post("./ready", playername)'
t[653] = r'	post("./join_game", newname).then((e) => {'
t[654] = r'		if (query.bot == "true") location.replace("./?name=" + newname + "&bot=true")'
t[655] = r'		else location.replace("./?name=" + newname)'
t[660] = r'	post("./ready", playername)'
t[693] = r'	post("./submit_plan", `${playername}\n${plan.join("\n")}`)'

f = open("coltsuperexpress/public_files/script.js", "w")
f.write("\n".join(t))
f.close()

f = open("coltsuperexpress/game.py", "r")
t = f.read().split("\n")
f.close()

t.pop(342)
t.pop(342)
t.pop(342)
t.pop(342)
t.pop(343)
t.pop(343)

f = open("coltsuperexpress/game.py", "w")
f.write("\n".join(t))
f.close()