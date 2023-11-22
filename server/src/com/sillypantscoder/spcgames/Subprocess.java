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
	public BufferedReader stdoutReader;
	public Subprocess(String[] args, String cwd) {
		try {
			process = new ProcessBuilder(args).redirectError(ProcessBuilder.Redirect.INHERIT).directory(new File(cwd)).start();
			stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
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
	public String readPacket() {
		try {
			String sizeStr = "";
			char lastChar = 0;
			while (lastChar != '.') {
				lastChar = (char)(stdoutReader.read());
				sizeStr += lastChar;
			}
			sizeStr = sizeStr.substring(0, sizeStr.length() - 1);
			int packetSize = Integer.parseInt(sizeStr);
			String resultStr = "";
			for (int i = 0; i < packetSize; i++) {
				resultStr += (char)(stdoutReader.read());
			}
			return resultStr;
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
}
