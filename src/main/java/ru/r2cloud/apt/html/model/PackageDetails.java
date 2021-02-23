package ru.r2cloud.apt.html.model;

import java.util.HashMap;
import java.util.Map;

public class PackageDetails {

	private String name;
	private String homepage;
	private Map<Column, PackageVersion> versions = new HashMap<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public Map<Column, PackageVersion> getVersions() {
		return versions;
	}

	public void setVersions(Map<Column, PackageVersion> versions) {
		this.versions = versions;
	}

}
