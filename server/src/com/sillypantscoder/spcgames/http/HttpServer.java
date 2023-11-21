package com.sillypantscoder.spcgames.http;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpServer {
	public HttpServer(RequestHandler handler) {
		try {
			InetSocketAddress addr = new InetSocketAddress("0.0.0.0", 9374);
			com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(addr, 0);
			server.createContext("/", new ProxyHttpHandler(handler));
			server.setExecutor(null);
			server.start();
			System.out.println("Server started at: https://" + addr.getHostName() + ":" + addr.getPort() + "/");
		} catch (IOException e) {
			System.out.println("Server failed to start:");
			e.printStackTrace();
		}
	}
}
