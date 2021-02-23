package ru.r2cloud.apt.html.model;

public enum Codename {
	
	stretch(OsType.debian, 9), buster(OsType.debian, 10), bullseye(OsType.debian, 11), bionic(OsType.ubuntu, 1804), focal(OsType.ubuntu, 2004);
	
	private final OsType type;
	private final int index;
	
	private Codename(OsType type, int index) {
		this.type = type;
		this.index = index;
	}
	
	public OsType getType() {
		return type;
	}
	
	public int getIndex() {
		return index;
	}

}
