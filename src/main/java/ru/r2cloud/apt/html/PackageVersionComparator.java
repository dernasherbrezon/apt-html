package ru.r2cloud.apt.html;

import java.util.Comparator;

import ru.r2cloud.apt.html.model.PackageVersion;

public class PackageVersionComparator implements Comparator<PackageVersion> {
	
	public static final PackageVersionComparator INSTANCE = new PackageVersionComparator();

	@Override
	public int compare(PackageVersion o1, PackageVersion o2) {
		int result = Long.compare(o1.getMajor(), o2.getMajor());
		if (result != 0) {
			return result;
		}
		result = Integer.compare(o1.getMinor(), o2.getMinor());
		if (result != 0) {
			return result;
		}
		return Integer.compare(o1.getPatch(), o2.getPatch());
	}
}
