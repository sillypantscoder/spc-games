package com.sillypantscoder.spcgames.http;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ProxyHttpHandler implements HttpHandler {
	public RequestHandler handler;
	public ProxyHttpHandler(RequestHandler handler) {
		super();
		this.handler = handler;
	}
	@Override
	public void handle(HttpExchange httpExchange) {
		try {
			if (httpExchange.getRequestMethod().equals("GET")) {
				handleGetRequest(httpExchange);
			}
			if (httpExchange.getRequestMethod().equals("POST")) {
				handlePostRequest(httpExchange);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	private void handleGetRequest(HttpExchange httpExchange) throws IOException {
		String path = httpExchange.getRequestURI().toString();
		HttpResponse response = handler.get(path);
		response.send(httpExchange);
	}
	private void handlePostRequest(HttpExchange httpExchange) throws IOException {
		String path = httpExchange.getRequestURI().toString();
		byte[] body = httpExchange.getRequestBody().readAllBytes();
		String bodys = new String(body, StandardCharsets.UTF_8);
		HttpResponse response = handler.post(path, bodys);
		response.send(httpExchange);
	}
}