import typing
import os
import json
import sys

class FileEntry(typing.TypedDict):
	url: str
	filepath: str
	type: str
class ServerData(typing.TypedDict):
	name: str
	files: list[FileEntry]
	folders: list[FileEntry]
server_data: ServerData = json.loads(sys.argv[1])

class URLQuery:
	def __init__(self, q):
		self.orig = q
		self.fields = {}
		for f in q.split("&"):
			s = f.split("=")
			if len(s) >= 2:
				self.fields[s[0]] = s[1]
	def get(self, key):
		if key in self.fields:
			return self.fields[key]
		else:
			return ''

def read_file(filename: str) -> bytes:
	"""Read a file and return the contents."""
	f = open(filename, "rb")
	t = f.read()
	f.close()
	return t

def write_file(filename: str, content: bytes):
	"""Write data to a file."""
	f = open(filename, "wb")
	f.write(content)
	f.close()

class HttpResponse(typing.TypedDict):
	"""A dict containing an HTTP response."""
	status: int
	headers: dict[str, str]
	content: str | bytes

class HttpResponseStrict(typing.TypedDict):
	"""A dict containing an HTTP response. The content field is required to be bytes and not str."""
	status: int
	headers: dict[str, str]
	content: bytes

def get(path: str, query: URLQuery) -> HttpResponse:
	for entry in server_data["files"]:
		if path == entry["url"]:
			file = read_file(entry["filepath"])
			return {
				"status": 200,
				"headers": {
					"Content-Type": entry["type"]
				},
				"content": file
			}
	print(f"{server_data['name']} - 404 GET {path}", file=sys.stderr)
	return {
		"status": 404,
		"headers": {
			"Content-Type": "text/html"
		},
		"content": ""
	}

def post(path: str, body: str) -> HttpResponse:
	bodydata = body.split("\n")
	if False: pass
	else:
		print(f"{server_data['name']} - 404 GET {path}", file=sys.stderr)
		return {
			"status": 404,
			"headers": {
				"Content-Type": "text/html"
			},
			"content": ""
		}

class MyServer:
	def handle_request(self):
		r = json.loads(input())
		res: HttpResponseStrict = {
			"status": 404,
			"headers": {},
			"content": b""
		}
		if r["method"] == "GET":
			res = self.do_GET(r["path"])
		if r["method"] == "POST":
			res = self.do_POST(r["path"], r["body"])
		s: list[bytes] = [
			str(res["status"]).encode("UTF-8"),
			",".join([f"{a}:{b}" for a, b in res["headers"].items()]).encode("UTF-8"),
			res["content"]
		]
		for data in s:
			self.send_packet(data)
			# time.sleep(0.3)
	def send_packet(self, info: bytes):
		sys.stdout.buffer.write(str(len(info)).encode("UTF-8"))
		sys.stdout.buffer.write(b".")
		sys.stdout.buffer.write(info)
		sys.stdout.buffer.flush()
		# try: print("Printed[", str(len(info)), '.', info.decode("UTF-8"), "]", sep="", file=sys.stderr)
		# except UnicodeDecodeError: print("Printed[", str(len(info)), '.', info, "]", sep="", file=sys.stderr)
	def do_GET(self, path) -> HttpResponseStrict:
		splitpath = path.split("?")
		res = get(splitpath[0], URLQuery(''.join(splitpath[1:])))
		c: str | bytes = res["content"]
		if isinstance(c, str): c = c.encode("utf-8")
		return {
			"status": res["status"],
			"headers": res["headers"],
			"content": c
		}
	def do_POST(self, path: str, body: str) -> HttpResponseStrict:
		res = post(path, body)
		c: str | bytes = res["content"]
		if isinstance(c, str): c = c.encode("utf-8")
		return {
			"status": res["status"],
			"headers": res["headers"],
			"content": c
		}

if __name__ == "__main__":
	running = True
	webServer = MyServer()
	print(f"Fake server \"{server_data['name']}\" started", file=sys.stderr)
	# sys.stdout.flush()
	while running:
		try:
			webServer.handle_request()
		except KeyboardInterrupt:
			running = False
	print(f"Server \"{server_data['name']}\" stopped", file=sys.stderr)
