import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.ArrayList;

public class VoteHttpHandler implements HttpHandler {
	public Game game;
	public static ArrayList<QueuedEvent> queue = new ArrayList<QueuedEvent>();
	public VoteHttpHandler(Game game) {
		super();
		this.game = game;
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
		handleQueue();
	}
	private void handleGetRequest(HttpExchange httpExchange) throws IOException {
		String path = httpExchange.getRequestURI().toString();
		HttpResponse response = game.get(path);
		response.send(httpExchange);
	}
	private void handlePostRequest(HttpExchange httpExchange) throws IOException {
		String path = httpExchange.getRequestURI().toString();
		byte[] body = httpExchange.getRequestBody().readAllBytes();
		String bodys = new String(body, StandardCharsets.UTF_8);
		HttpResponse response = game.post(path, bodys);
		response.send(httpExchange);
	}
	public static void queue(int time, Player targetPlayer, String eventString) {
		queue.add(new QueuedEvent(time, targetPlayer, eventString));
	}
	public static void handleQueue() {
		for (int i = 0; i < queue.size(); i++) {
			if (queue.get(i).tick()) {
				queue.remove(i);
				i -= 1;
			}
		}
	}
	public static class QueuedEvent {
		public int requestsLeft;
		public Player target;
		public String event;
		public QueuedEvent(int time, Player targetPlayer, String eventString) {
			requestsLeft = time;
			target = targetPlayer;
			event = eventString;
		}
		public boolean tick() {
			requestsLeft -= 1;
			if (requestsLeft <= 0) target.fire(event);
			return requestsLeft <= 0;
		}
	}
}