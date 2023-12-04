import java.util.ArrayList;

public class HttpResponse {
	public int status;
	public String body;
	public ArrayList<String> headerNames;
	public ArrayList<String> headerValues;
	public HttpResponse() {
		status = 200;
		body = "";
		headerNames = new ArrayList<String>();
		headerValues = new ArrayList<String>();
	}
	public HttpResponse setStatus(int newStatus) {
		status = newStatus;
		return this;
	}
	public HttpResponse setBody(String newBody) {
		body = newBody;
		return this;
	}
	public HttpResponse addHeader(String name, String value) {
		for (int i = 0; i < headerNames.size(); i++) {
			if (headerNames.get(i) == name) {
				headerValues.set(i, value);
				return this;
			}
		}
		headerNames.add(name);
		headerValues.add(value);
		return this;
	}
	public void send() {
		send_packet(String.valueOf(status));
		String[] finalHeaders = new String[headerNames.size()];
		for (int i = 0; i < headerNames.size(); i++) {
			finalHeaders[i] = headerNames.get(i) + ":" + headerValues.get(i);
		}
		send_packet(String.join(",", finalHeaders));
		send_packet(body);
	}
	public void send_packet(String data) {
		System.out.print(String.valueOf(data.length()));
		System.out.print(".");
		System.out.print(data);
	}
	public void printInfo() {
		System.err.println("[HTTP RESPONSE : " + status + "]\n(" + body.length() + ")>>>" + body + "<<<");
	}
}
