package com.sillypantscoder.spcgames.http;

public abstract class RequestHandler {
	public abstract HttpResponse get(String path);
	public abstract HttpResponse post(String path, String body);
}
