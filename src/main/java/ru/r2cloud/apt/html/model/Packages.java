package ru.r2cloud.apt.html.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Packages {

	private final Map<String, ControlFile> contents = new HashMap<String, ControlFile>();

	public void load(InputStream is) throws IOException {
		String curLine = null;
		BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		StringBuilder currentControl = new StringBuilder();
		while (true) {
			curLine = r.readLine();
			if (curLine == null || curLine.trim().length() == 0) {
				String currentControlStr = currentControl.toString();
				currentControl = new StringBuilder();
				if (currentControlStr.trim().length() != 0) {
					ControlFile curFile = new ControlFile();
					curFile.load(currentControlStr);
					contents.put(curFile.getPackageName(), curFile);
				}
				if (curLine == null) {
					break;
				}
				continue;
			}
			currentControl.append(curLine).append("\n");
		}
	}

	public Map<String, ControlFile> getContents() {
		return contents;
	}

}
