package com.birkettenterprise.phonelocator.settings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.birkettenterprise.phonelocator.application.PhonelocatorApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import android.util.Log;


public class EnvironmentalSettingsSetter {
	
	public static final String LOG_TAG = PhonelocatorApplication.LOG_TAG + "_ENVIRONMENTAL_SETTINGS";

	
	public static Pattern mPattern = Pattern.compile("(\\d+)\\.(\\d+)");
	
	public static void updateEnvironmentalSettingsIfRequired(SharedPreferences sharedPreferences, Context context) {

		
	    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		EnvironmentalSettingsSetter.setIMEIIMSIIfRequired(sharedPreferences, telephonyManager);
		
		try {
			EnvironmentalSettingsSetter.setVersionIfRequired(sharedPreferences, context.getPackageManager().getPackageInfo(context.getPackageName(), 0));
		} catch (NameNotFoundException e1) {
		}
		
	}
	
	public static void setIMEIIMSIIfRequired(SharedPreferences sharedPreferences, TelephonyManager telephonyManager) {
		String imei = telephonyManager.getDeviceId();
	
		Log.d(LOG_TAG, "imei " + imei);
		setStringIfRequired(sharedPreferences, Setting.Integer64Settings.IMEI, imei);
		
		String imsi = telephonyManager.getSubscriberId();
			
		Log.d(LOG_TAG, "imsi " + imsi);
		setStringIfRequired(sharedPreferences, Setting.Integer64Settings.IMSI, imsi);
	}
	
	public static void setVersionIfRequired(SharedPreferences sharedPreferences, PackageInfo packageInfo) {
		Version version = getVersion(packageInfo);
		setIntIfRequired(sharedPreferences, Setting.IntegerSettings.VERSION_MAJOR, version.mMajor);
		setIntIfRequired(sharedPreferences, Setting.IntegerSettings.VERSION_MINOR, version.mMinor);
		setIntIfRequired(sharedPreferences, Setting.IntegerSettings.VERSION_REVISION, version.mRevision);
	}
	
	private static void setStringIfRequired(SharedPreferences sharedPreferences, String key, String value) {
		String valueStoredInSettings = sharedPreferences.getString(key, "");

		if (!sharedPreferences.contains(key) || !valueStoredInSettings.equals(value)) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(key, value);
			editor.commit();
		}
	}
	
	private static void setIntIfRequired(SharedPreferences sharedPreferences, String key, int value) {
		int valueStoredInSettings = sharedPreferences.getInt(key, 0);

		if (!sharedPreferences.contains(key) || value != valueStoredInSettings) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putInt(key, value);
			editor.commit();
		}
	}
	
	
	static Version getVersion(PackageInfo packageInfo) {
		return getVersion(packageInfo.versionName, packageInfo.versionCode);	
	}
	
	static Version getVersion(String versionName, int versionCode) {
		Version version = new Version();
		version.mRevision = versionCode;
		Matcher matcher = mPattern.matcher(versionName);
		if (matcher.matches()) {
			version.mMajor = Integer.parseInt(matcher.group(1));
			version.mMinor =  Integer.parseInt(matcher.group(2));
		}
		return version;
	}
}
