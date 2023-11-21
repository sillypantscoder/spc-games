package com.sillypantscoder.spcgames;

import com.sillypantscoder.spcgames.http.HttpServer;

public class Main {
	public static void main(String[] args) {
		new HttpServer(new GameHandler());
	}
}