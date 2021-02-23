package ru.r2cloud.apt.html.model;

public class Column {

	private Codename codename;
	private Architecture arch;

	public Column(Codename codename, Architecture arch) {
		super();
		this.codename = codename;
		this.arch = arch;
	}

	public Codename getCodename() {
		return codename;
	}

	public void setCodename(Codename codename) {
		this.codename = codename;
	}

	public Architecture getArch() {
		return arch;
	}

	public void setArch(Architecture arch) {
		this.arch = arch;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arch == null) ? 0 : arch.hashCode());
		result = prime * result + ((codename == null) ? 0 : codename.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Column other = (Column) obj;
		if (arch != other.arch)
			return false;
		if (codename != other.codename)
			return false;
		return true;
	}

}
