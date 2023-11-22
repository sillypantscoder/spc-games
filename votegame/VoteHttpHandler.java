import java.io.IOException;

import java.util.ArrayList;

public class VoteHttpHandler {
	public Game game;
	public static ArrayList<QueuedEvent> queue = new ArrayList<QueuedEvent>();
	public VoteHttpHandler(Game game) {
		super();
		this.game = game;
	}
	public void handle(String method, String path, String body) {
		try {
			if (method.equals("GET")) {
				handleGetRequest(path);
			}
			if (method.equals("POST")) {
				handlePostRequest(path, body);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		handleQueue();
	}
	private void handleGetRequest(String path) throws IOException {
		HttpResponse response = game.get(path);
		response.send();
	}
	private void handlePostRequest(String path, String body) throws IOException {
		HttpResponse response = game.post(path, body);
		response.send();
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