import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {
	public static void main(String[] args) throws IOException {
		VoteHttpHandler handler = new VoteHttpHandler(new Game());
		System.err.println("Fake server (voting game) started");
		while (true) {
			// Get data from STDIN
			String method = readPacket();
			String path = readPacket();
			String body = readPacket();
			// Use the extracted variables as needed
			handler.handle(method, path, body);
		}
	}
	public static String readPacket() throws IOException {
		String headers = "";
		String newChar = "";
		while (! newChar.equals(".")) {
			headers += newChar;
			newChar = new String(new byte[] { (byte)(System.in.read()) }, StandardCharsets.UTF_8);
		}
		int length = Integer.parseInt(headers);
		String result = "";
		for (int i = 0; i < length; i++) {
			result += new String(new byte[] { (byte)(System.in.read()) }, StandardCharsets.UTF_8);
		}
		return result;
	}
}