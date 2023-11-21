package com.sillypantscoder.spcgames;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
// import java.io.InputStream;
import java.io.InputStreamReader;
// import java.nio.charset.StandardCharsets;
import java.io.OutputStream;

public class Subprocess {
	public Process process;
	public Subprocess(String[] args, String cwd) {
		try {
			process = new ProcessBuilder(args).redirectError(ProcessBuilder.Redirect.INHERIT).directory(new File(cwd)).start();
		} catch (IOException e) {
			System.out.println("Error starting process");
			process = null;
		}
	}
	public void waitFor() {
		if (process == null) return;
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			return;
		}
	}
	public void writeStdin(String input) {
		try {
			OutputStream stream = process.getOutputStream();
			stream.write(input.getBytes());
			stream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String readStdoutLine() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			// StringBuilder builder = new StringBuilder();
			// String line = null;
			// while ((line = reader.readLine()) != null) {
			// 	builder.append(line);
			// 	builder.append(System.getProperty("line.separator"));
			// }
			return reader.readLine();
			// String result = builder.toString();
			// InputStream stream = process.getInputStream();
			// String s = "";
			// String lastChar = "";
			// while (! lastChar.equals("\n")) {
			// 	s += lastChar;
			// 	lastChar = new String(stream.readNBytes(1), StandardCharsets.UTF_8);
			// }
			// return s;
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
}
