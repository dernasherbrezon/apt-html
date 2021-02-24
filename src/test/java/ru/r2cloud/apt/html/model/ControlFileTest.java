package ru.r2cloud.apt.html.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ControlFileTest {
	
	private static final String STR = "Package: rtl-sdr-dbgsym\n"
			+ "Source: rtl-sdr\n"
			+ "Version: 0.6.5-1~stretch\n"
			+ "Auto-Built-Package: debug-symbols\n"
			+ "Architecture: armhf\n"
			+ "Maintainer: A. Maitland Bottoms <bottoms@debian.org>\n"
			+ "Installed-Size: 197\n"
			+ "Depends: rtl-sdr (= 0.6.5-1~stretch)\n"
			+ "Section: debug\n"
			+ "Priority: extra\n"
			+ "Homepage: http://sdr.osmocom.org/trac/wiki/rtl-sdr\n"
			+ "Description: Debug symbols for rtl-sdr\n"
			+ "Build-Ids: 11bdb6077324329f940a93eb941182197e449817 635a891c7ccaa9b5bbca1c63d8d57e9d824b2587 637008ecf6366fbe9b656c03b0fed827507dbce0 647ad87db232d2aa659c2256e86ace4b013e5796 6bf5586f32e1a90f0f09aef13509a6e973d47a99 9d02433b15feb90ebcdfc168d0c78b77c1a70164 d8842ff2db65b48f65e1f7540dd4fca4c3de42b4 ee61ce9b0e30f493fd8180f90ffc1aaf770b69ee\n"
			+ "Filename: pool/main/r/rtl-sdr-dbgsym/rtl-sdr-dbgsym_0.6.5-1~stretch_armhf.deb\n"
			+ "Size: 123102\n"
			+ "MD5sum: a4bda69bfaff4d28b48190b752806392\n"
			+ "SHA1: 17fe1091e9dcf87429f73699b4d9456127f49abd\n"
			+ "SHA256: 8316f999bd038b7b2368fc8f2a260ca023f44e909e4bd5908c0e572eb5543853\n"
			+ "";
	
	@Test
	public void testSuccess() {
		ControlFile control = new ControlFile();
		control.load(STR);
		assertEquals("rtl-sdr-dbgsym", control.getPackageName());
		assertEquals("http://sdr.osmocom.org/trac/wiki/rtl-sdr", control.getHomepage());
		assertEquals("0.6.5-1~stretch", control.getVersion());
	}

}
