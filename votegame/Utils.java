import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.net.URLDecoder;

public class Utils {
	public static String readFile(File file) {
		try {
			Scanner fileReader = new Scanner(file);
			String allData = "";
			while (fileReader.hasNextLine()) {
				String data = fileReader.nextLine();
				allData += data + "\n";
			}
			fileReader.close();
			// Parse the data
			return allData;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "Error finding file...";
		}
	}
	public static String readFile(String filename) {
		return readFile(new File(filename));
	}
	public static String decodeURIComponent(String in) {
		try {
			return URLDecoder.decode(in, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return in;
		}
	}
}
