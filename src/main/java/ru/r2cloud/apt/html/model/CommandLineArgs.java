package ru.r2cloud.apt.html.model;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

public class CommandLineArgs {

	@Parameter(names = "--url", description = "Url of APT repository. For example, http://s3.amazonaws.com/r2cloud", required = true)
	private String url;

	@Parameter(names = "--include-arch", description = "Comma separated list of archs to include into the search. Example: amd64,armhf", required = true)
	private List<String> includeArch = new ArrayList<>();

	@Parameter(names = "--include-codename", description = "Comma separated list of codenames to include into the search. Example: stretch,bionic", required = true)
	private List<String> includeCodename = new ArrayList<>();

	@Parameter(names = "--include-package", description = "Comma separated list of packages to include into the search. Example: sdr-server,r2cloud")
	private List<String> includePackage = new ArrayList<>();

	@Parameter(names = "--output-dir", description = "Output directory for the generated web site. For example, target/generated", required = true)
	private String outputDir;

	@Parameter(names = "--help", help = true)
	private boolean help;

	public List<String> getIncludeCodename() {
		return includeCodename;
	}

	public void setIncludeCodename(List<String> includeCodename) {
		this.includeCodename = includeCodename;
	}

	public List<String> getIncludePackage() {
		return includePackage;
	}

	public void setIncludePackage(List<String> includePackage) {
		this.includePackage = includePackage;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public boolean isHelp() {
		return help;
	}

	public void setHelp(boolean help) {
		this.help = help;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<String> getIncludeArch() {
		return includeArch;
	}

	public void setIncludeArch(List<String> includeArch) {
		this.includeArch = includeArch;
	}

}
