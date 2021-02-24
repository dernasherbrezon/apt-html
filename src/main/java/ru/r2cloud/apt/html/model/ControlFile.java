package ru.r2cloud.apt.html.model;

import java.util.HashMap;
import java.util.Map;

public class ControlFile {

	private String packageName;
	private String version;
	private Architecture arch;
	private String contents;
	private String homepage;

	private final Map<String, String> payload = new HashMap<String, String>();

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public Map<String, String> getPayload() {
		return payload;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Architecture getArch() {
		return arch;
	}

	public void setArch(Architecture arch) {
		this.arch = arch;
	}

	public void load(String str) {
		contents = str.trim();
		if (contents.charAt(contents.length() - 1) != '\n') {
			contents += "\n";
		}
		String[] lines = str.split("\\r?\\n");
		for (String cur : lines) {
			int index = cur.indexOf(':');
			if (index == -1) {
				continue;
			}
			String name = cur.substring(0, index);
			String value = cur.substring(index + 1).trim();
			if (name.equalsIgnoreCase("Package")) {
				setPackageName(value);
				continue;
			}
			if (name.equalsIgnoreCase("Homepage")) {
				setHomepage(value);
				continue;
			}
			if (name.equalsIgnoreCase("Version")) {
				setVersion(value);
				continue;
			}
			if (name.equalsIgnoreCase("Architecture")) {
				setArch(Architecture.valueOf(value));
				continue;
			}
		}
	}

	public void append(String str) {
		contents += str + "\n";
	}
}
