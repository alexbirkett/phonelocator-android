package com.birkettenterprise.phonelocator.settings;

import android.content.SharedPreferences;
import android.telephony.TelephonyManager;


public class EnvironmentalSettingsSetter {
	
	public static void setIMEIIMSI(SharedPreferences sharedPreferences, TelephonyManager telephonyManager) {
		String imei = telephonyManager.getDeviceId();
		setIfRequired(sharedPreferences, Setting.Integer64Settings.IMEI, imei);
		
		String imsi = telephonyManager.getSimSerialNumber();
		setIfRequired(sharedPreferences, Setting.Integer64Settings.IMSI, imsi);
	}
	
	private static void setIfRequired(SharedPreferences sharedPreferences, String key, String value) {
		String valueStoredInSettings = sharedPreferences.getString(key, "");

		if (!valueStoredInSettings.equals(value)) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(key, value);
			editor.commit();
		}
	}
}
