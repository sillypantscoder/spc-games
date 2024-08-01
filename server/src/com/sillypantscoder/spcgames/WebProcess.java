package com.sillypantscoder.spcgames;

import com.sillypantscoder.spcgames.http.HttpResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class WebProcess {
	public Subprocess process;
	public WebProcess(Subprocess process) {
		this.process = process;
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
	public HttpResponse communicate(String method, String path, String body) {
		process.writePacket(method);
		process.writePacket(path);
		process.writePacket(body);
		int status = Integer.parseInt(process.readPacket());
		String headerSection = process.readPacket();
		String[] headerStrings = headerSection.split(",");
		if (headerSection.length() == 0) headerStrings = new String[] {};
		String content = process.readPacket();
		byte[] contentR = content.getBytes(StandardCharsets.UTF_8);
		if (content.length() > 0 && content.charAt(0) == '$') {
			contentR = Base64.getDecoder().decode(content.substring(1));
		}
		HttpResponse res = new HttpResponse();
		res.setStatus(status);
		for (String header : headerStrings) {
			String[] parts = header.split(":");
			res.addHeader(parts[0], parts[1]);
		}
		res.setBody(contentR);
		return res;
	}
	/**
	 * Send a GET request.
	 * @param path The path the request should go to.
	 * @return
	 */
	public HttpResponse get(String path) {
		return communicate("GET", path, "");
	}
	/**
	 * Send a POST request.
	 * @param path The path the request should go to.
	 * @param body The body of the POST request.
	 * @return
	 */
	public HttpResponse post(String path, String body) {
		return communicate("POST", path, body);
	}
	/**
	 * Kill the process.
	 */
	public void destroy() {
		process.process.destroy();
	}
}
