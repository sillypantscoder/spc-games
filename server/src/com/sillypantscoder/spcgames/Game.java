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
	/**
	 * Send a command to the subprocess, and get the HTTP response back.
	 *
	 * Data is sent using JSON, for instance:
	 * <pre>
	 * {"method": "POST", "path": "/whatever", "body": "stuff"}
	 * </pre>
	 * Data is recieved using packets. Each packet is the length of the packet, followed by a period, followed by the packet's contents.
	 * The HTTP response is sent back in three packets: First the status code, then the headers, and then the body.
	 * The header names are separated from their values with a colon, and the header entries are separated with commas.
	 * An example of an HTTP response would look like:
	 * <pre>3.20048.Content-Type:text/html,X-Random-Header:something76.&lt;!DOCTYPE html&gt;
	 * &lt;html&gt;
	 * 	&lt;head&gt;
	 * 	&lt;/head&gt;
	 * 	&lt;body&gt;
	 * 		something
	 * 	&lt;/body&gt;
	 * &lt;/html&gt;</pre>
	 * @param message
	 * @return
	 */
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
	/**
	 * Send a GET request.
	 * @param path The path the request should go to.
	 * @return
	 */
	public HttpResponse get(String path) {
		return communicate("{\"method\":\"GET\",\"path\":\"" + path + "\",\"body\":\"\"}\n");
	}
	/**
	 * Send a POST request.
	 * @param path The path the request should go to.
	 * @param body The body of the POST request.
	 * @return
	 */
	public HttpResponse post(String path, String body) {
		return communicate("{\"method\":\"POST\",\"path\":\"" + path + "\",\"body\":\"" + body + "\"}\n");
	}
	/**
	 * Get the short status to be shown on the home page.
	 * @return
	 */
	public String getStatus() {
		return this.type.getStatus(this);
	}
	/**
	 * Get the moderator status to be shown on the /host page. This can (and should) contain HTML.
	 * @return
	 */
	public String getModStatus() {
		return this.type.getModStatus(this);
	}
	/**
	 * Check whether the game should be deleted because it was marked for deletion.
	 * @return
	 */
	public boolean stillValid() {
		if (this.deletionTime == 0) return true;
		return this.deletionTime > new Date().getTime();
	}
}
