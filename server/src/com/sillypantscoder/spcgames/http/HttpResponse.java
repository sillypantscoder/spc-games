package com.sillypantscoder.spcgames.http;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class HttpResponse {
	public int status;
	public String body;
	public ArrayList<String> headerNames;
	public ArrayList<String> headerValues;
	public HttpResponse() {
		status = 200;
		body = "";
		headerNames = new ArrayList<String>();
		headerValues = new ArrayList<String>();
	}
	public HttpResponse setStatus(int newStatus) {
		status = newStatus;
		return this;
	}
	public HttpResponse setBody(String newBody) {
		body = newBody;
		return this;
	}
	public HttpResponse addHeader(String name, String value) {
		for (int i = 0; i < headerNames.size(); i++) {
			if (headerNames.get(i) == name) {
				headerValues.set(i, value);
				return this;
			}
		}
		headerNames.add(name);
		headerValues.add(value);
		return this;
	}
	public void send(HttpExchange exchange) throws IOException {
		OutputStream outputStream = exchange.getResponseBody();
		// Set headers
		Headers h = exchange.getResponseHeaders();
		for (int i = 0; i < headerNames.size(); i++) {
			h.add(headerNames.get(i), headerValues.get(i));
		}
		// Send headers then data
		byte[] bytes = body.getBytes();
		exchange.sendResponseHeaders(200, bytes.length);
		outputStream.write(bytes);
		// Finish
		outputStream.flush();
		outputStream.close();
	}
	public void printInfo() {
		System.out.println("[HTTP RESPONSE : " + status + "]\n(" + body.length() + ")>>>" + body + "<<<");
	}
	@Override
	public String toString() {
		return "[HTTP RESPONSE : " + status + "]\n(" + body.length() + ")>>>" + body + "<<<";
	}
}
