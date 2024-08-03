package com.sillypantscoder.spcgames.games;

import com.sillypantscoder.spcgames.GameInfo;
import com.sillypantscoder.spcgames.Subprocess;
import com.sillypantscoder.spcgames.WebProcess;
import com.sillypantscoder.spcgames.Game.StaticGame;
import com.sillypantscoder.spcgames.http.HttpResponse;

public class GeometryDash extends StaticGame {
	public WebProcess process;
	public GeometryDash() {
		super(getInfo());
		process = new WebProcess(new Subprocess(new String[] {"python3", "fakeserver.py"}, "../geometrydash"));
	}
	public static GameInfo getInfo() {
		return new GameInfo() {
			public String getName() {
				return "Geometry Dash clone";
			}
			public String getShortDescription() {
				return "A Geometry Dash clone that I made.";
			}
			public String getLongDescription() {
				return "A Geometry Dash clone that I made.";
			}
			public String getID() {
				return "geometrydash";
			}
		};
	}
	public HttpResponse get(String path) {
		return process.get(path);
	}
	public HttpResponse post(String path, String body) {
		return process.post(path, body);
	}
}
