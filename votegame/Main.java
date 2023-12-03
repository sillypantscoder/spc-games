import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	public static void main(String[] args) throws IOException {
		VoteHttpHandler handler = new VoteHttpHandler(new Game());
		System.err.println("Fake server (voting game) started");
		while (true) {
			String in = "";
			char lastChar = 0;
			while (lastChar != '\n') {
				lastChar = (char)(System.in.read());
				in += lastChar;
			}
			String regex = "\\{\"method\":\"([A-Z]+)\",\"path\":\"([a-zA-Z0-9\\/\\?\\=%\\._]*)\",\"body\":\"([a-zA-Z0-9\\n]*)\"}\\n";
			Matcher matcher = Pattern.compile(regex).matcher(in);
			if (matcher.find()) {
				String method = matcher.group(1);
				String path = matcher.group(2);
				String body = matcher.group(3);
				// Use the extracted variables as needed
				handler.handle(method, path, body);
			} else {
				System.err.println("=== ERROR IN === " + in);
			}
		}
	}
}