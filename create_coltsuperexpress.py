import subprocess

subprocess.run(["git", "clone", "https://github.com/sillypantscoder/coltsuperexpress"])

f = open("coltsuperexpress/public_files/script.js", "r")
t = f.read().split("\n")
f.close()

t[38] = r'		x.send(body.replaceAll("\n", "\\n"))'

f = open("coltsuperexpress/public_files/script.js", "w")
f.write("\n".join(t))
f.close()
