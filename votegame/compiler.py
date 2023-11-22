import os
import subprocess

# 1. Get the files to compile

files: "list[str]" = []
def checkfolder(folder):
	for file in os.listdir(folder):
		newname = os.path.join(folder, file)
		if os.path.isdir(newname):
			checkfolder(newname)
		else:
			if newname.split(".")[-1] in ["java", "html"]:
				files.append(newname)
checkfolder(".")

# 2. Compile the files

print("Compiling the voting game...")
os.makedirs("compiled_output", exist_ok=True)
cmd = ["javac", "-g", "-d", "compiled_output", "-cp", "src"]
for filename in files:
	if filename.endswith(".java"):
		cmd.append(filename)
	else:
		subprocess.run(["mkdir", "-p", "compiled_output/" + filename[4:filename.rfind("/")]])
		subprocess.run(["cp", filename, "compiled_output/" + filename[4:]])
subprocess.run(cmd)

# 3. Zip the files

print("Saving the voting game...")
f = open("compiled_output/manifest", "w")
f.write(f"""Manifest-Version: 1.0
Created-By: 17.0.3 (GraalVM Community)
Main-Class: Main

""")
f.close()
subprocess.run(["bash", "-c", "jar -c -v -f compiled.jar -m compiled_output/manifest -C compiled_output/ . > /dev/null"])
subprocess.run(["rm", "-r", "compiled_output"])

subprocess.run(["java", "-jar", "compiled.jar"])