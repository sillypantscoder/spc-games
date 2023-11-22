import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class Main {
	public static void main(String[] args) throws IOException {
		// Start the server
		InetSocketAddress addr = new InetSocketAddress("0.0.0.0", 9375);
		HttpServer server = HttpServer.create(addr, 0);
		server.createContext("/", new VoteHttpHandler(new Game()));
		server.setExecutor(null);
		server.start();
		System.out.println("Server started at: https://" + addr.getHostName() + ":" + addr.getPort() + "/");
	}
}