package ru.r2cloud.apt.html.model;

public class PackageVersion {

	private String color;
	private String version;

	private long major;
	private int minor;
	private int patch;
	private int distribId;

	public PackageVersion(String color, String version) {
		super();
		this.color = color;
		this.version = version;
		try {
			setupVersions(version);
		} catch (NumberFormatException e) {
		}
	}

	private void setupVersions(String version) {
		int majorIndex = version.indexOf('.');
		if (majorIndex == -1) {
			major = Long.valueOf(version);
			return;
		}
		major = Long.valueOf(version.substring(0, majorIndex));
		int minorIndex = version.indexOf('.', majorIndex + 1);
		if (minorIndex == -1) {
			minor = Integer.valueOf(version.substring(majorIndex + 1));
			return;
		}
		minor = Integer.valueOf(version.substring(majorIndex + 1, minorIndex));
		int patchIndex = version.indexOf('-', minorIndex + 1);
		if (patchIndex == -1) {
			patch = Integer.valueOf(version.substring(minorIndex + 1));
			return;
		}
		patch = Integer.valueOf(version.substring(minorIndex + 1, patchIndex));
		int distribIndex = version.indexOf('~', patchIndex + 1);
		if (distribIndex == -1) {
			distribId = Integer.valueOf(version.substring(patchIndex + 1));
			return;
		}
		distribId = Integer.valueOf(version.substring(patchIndex + 1, distribIndex));
	}

	public long getMajor() {
		return major;
	}

	public void setMajor(long major) {
		this.major = major;
	}

	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}

	public int getPatch() {
		return patch;
	}

	public void setPatch(int patch) {
		this.patch = patch;
	}

	public int getDistribId() {
		return distribId;
	}

	public void setDistribId(int distribId) {
		this.distribId = distribId;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
