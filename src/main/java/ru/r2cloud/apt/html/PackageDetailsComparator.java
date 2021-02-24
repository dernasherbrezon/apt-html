package ru.r2cloud.apt.html;

import java.util.Comparator;

import ru.r2cloud.apt.html.model.PackageDetails;

public class PackageDetailsComparator implements Comparator<PackageDetails> {
	
	public static final PackageDetailsComparator INSTANCE = new PackageDetailsComparator();

	@Override
	public int compare(PackageDetails o1, PackageDetails o2) {
		return o1.getName().compareTo(o2.getName());
	}
}
