package com.sillypantscoder.spcgames;

import java.util.Random;

import com.sillypantscoder.spcgames.http.HttpResponse;

public class Game {
	public GameType type;
	public Subprocess process;
	public String id;
	public Game(GameType type) {
		this.type = type;
		process = type.getProcess();
		id = type.getID() + new Random().nextInt(Integer.MAX_VALUE);
	}
	public HttpResponse communicate(String message) {
		process.writeStdin(message);
		// System.out.println("before");
		String raw_out = process.readStdoutLine();
		if (raw_out == null) {
			System.err.println("ERROR! Raw out is null! Probably because of a server error...");
			return new HttpResponse().setStatus(500);
		}
		String[] s = raw_out.split("\\|\\|\\|");
		if (s.length == 1) s = new String[] { s[0],  "" , "" };
		if (s.length == 2) s = new String[] { s[0], s[1], "" };
		// Utils.log(s);
		// System.out.println("after");
		int status = Integer.parseInt(s[0]);
		String[] headerStrings = s[1].split(",");
		if (s[1].length() == 0) headerStrings = new String[] {};
		String content = "";
		for (int i = 0; i < s[2].length(); i += 2) {
			String hex = "" + s[2].charAt(i) + "" + s[2].charAt(i + 1) + "";
			int decimal = Integer.parseInt(hex, 16);
			content += Character.toString((char)(decimal));
		}
		HttpResponse res = new HttpResponse();
		res.setStatus(status);
		for (String header : headerStrings) {
			String[] parts = header.split(":");
			res.addHeader(parts[0], parts[1]);
		}
		res.setBody(content);
		return res;
	}
	public HttpResponse get(String path) {
		return communicate("{\"method\":\"GET\",\"path\":\"" + path + "\"}\n");
	}
	public HttpResponse post(String path, String body) {
		return communicate("{\"method\":\"POST\",\"path\":\"" + path + "\",\"body\":\"" + body + "\"}\n");
	}
}
