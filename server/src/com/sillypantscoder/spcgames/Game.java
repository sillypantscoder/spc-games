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
		int status = Integer.parseInt(process.readPacket());
		String headerSection = process.readPacket();
		String[] headerStrings = headerSection.split(",");
		if (headerSection.length() == 0) headerStrings = new String[] {};
		String content = process.readPacket();
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
