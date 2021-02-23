package ru.r2cloud.apt.html.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PackageVersionTest {

	@Test
	public void testDifferentVersions() {
		assertPackageVersion(1, 2, 0, 0, new PackageVersion(null, "1.2"));
		assertPackageVersion(1, 2, 3, 0, new PackageVersion(null, "1.2.3"));
		assertPackageVersion(1, 2, 3, 1, new PackageVersion(null, "1.2.3-1"));
		assertPackageVersion(1, 2, 3, 1, new PackageVersion(null, "1.2.3-1~stretch1"));
		assertPackageVersion(20210124131944L, 0, 0, 0, new PackageVersion(null, "20210124131944"));
	}

	private static void assertPackageVersion(long majorExpected, int minorExpected, int patchExpected, int distribExpected, PackageVersion actual) {
		assertEquals(majorExpected, actual.getMajor());
		assertEquals(minorExpected, actual.getMinor());
		assertEquals(patchExpected, actual.getPatch());
		assertEquals(distribExpected, actual.getDistribId());
	}

}
