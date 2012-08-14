package com.birkettenterprise.phonelocator.settings;

import android.test.AndroidTestCase;

public class EnvironmentalSettingSetterTest extends AndroidTestCase {

	public void testGetVersionOne() {
		//assertEquals()
		Version version = EnvironmentalSettingsSetter.getVersion("6.3.1");
		assertEquals(6, version.mMajor);
		assertEquals(3, version.mMinor);
		assertEquals(1, version.mRevision);
	}
	
	public void testGetVersionTwo() {
		//assertEquals()
		Version version = EnvironmentalSettingsSetter.getVersion("6121.213213.21323543");
		assertEquals(6121, version.mMajor);
		assertEquals(213213, version.mMinor);
		assertEquals(21323543, version.mRevision);
	}
}
