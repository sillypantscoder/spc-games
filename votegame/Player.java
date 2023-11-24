import java.util.ArrayList;

public class Player {
	public String name;
	public float score;
	public boolean hasStar;
	public ArrayList<String> events;
	public int vote;
	public Player(String name) {
		this.name = name;
		this.score = 0;
		this.hasStar = false;
		this.events = new ArrayList<String>();
		this.vote = -1;
	}
	public void fire(String event) {
		events.add(event);
	}
	public String getMessagesString() {
		String r = "";
		for (int i = 0; i < events.size(); i++) {
			r += events.get(i) + "\n";
		}
		events = new ArrayList<String>();
		return r;
	}
}
