import java.util.ArrayList;

public class Player {
	// basic things
	public String name;
	public ArrayList<String> events;
	public int vote;
	// module info
	public float score;
	public boolean hasStar;
	public ModuleColors.Color color;
	public Player(String name) {
		this.name = name;
		this.events = new ArrayList<String>();
		this.vote = -1;
		this.score = 0;
		this.hasStar = false;
		this.color = ModuleColors.Color.Red;
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
