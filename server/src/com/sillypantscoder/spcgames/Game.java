package com.sillypantscoder.spcgames;

import java.util.Date;
import java.util.Random;

import com.sillypantscoder.spcgames.http.HttpResponse;

public class Game {
	public GameType type;
	public Subprocess process;
	public String name;
	public String id;
	public long deletionTime;
	public Game(GameType type, String name) {
		this.type = type;
		process = type.getProcess();
		this.name = name;
		id = type.getID() + new Random().nextInt(Integer.MAX_VALUE);
		this.deletionTime = 0;
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
		return communicate("{\"method\":\"GET\",\"path\":\"" + path + "\",\"body\":\"\"}\n");
	}
	public HttpResponse post(String path, String body) {
		return communicate("{\"method\":\"POST\",\"path\":\"" + path + "\",\"body\":\"" + body + "\"}\n");
	}
	public String getStatus() {
		return this.type.getStatus(this);
	}
	public String getModStatus() {
		return this.type.getModStatus(this);
	}
	public boolean stillValid() {
		if (this.deletionTime == 0) return true;
		return this.deletionTime > new Date().getTime();
	}
}
