package com.sillypantscoder.spcgames;

import com.sillypantscoder.spcgames.http.HttpResponse;

public abstract class Game {
	public Subprocess process;
	public Game() {
		process = new Subprocess(new String[] {"python3", "main.py"}, "../coltsuperexpress");
	}
	public HttpResponse communicate(String message) {
		process.writeStdin(message);
		// System.out.println("before");
		String[] s = process.readStdoutLine().split("\\|\\|\\|");
		if (s.length == 1) s = new String[] { s[0],  "" , "" };
		if (s.length == 2) s = new String[] { s[0], s[1], "" };
		// Utils.log(s);
		// System.out.println("after");
		int status = Integer.parseInt(s[0]);
		String[] headerStrings = s[1].split(",");
		if (s[1].length() == 0) headerStrings = new String[] {};
		String content = s[2].replaceAll("\\\\\\\\n", "[[REAL NEWLINE]]").replaceAll("\\\\n", "\n").replaceAll("\\[\\[REAL NEWLINE\\]\\]", "\\\\n").replaceAll("\\\\t", "\t").replaceAll("\\\\'", "'").replaceAll("\\\\\\\\", "\\\\");
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
