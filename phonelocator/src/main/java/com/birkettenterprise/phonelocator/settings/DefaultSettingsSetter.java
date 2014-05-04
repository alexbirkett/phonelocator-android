package com.birkettenterprise.phonelocator.settings;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Set default settings to be synchronized with server during registration
 * @param sharedPreferences
 * @param context
 */
public class DefaultSettingsSetter {

	
	public static void setDefaultSettings(Context context) {
		
		SettingsHelper.setPasscode("");
		SettingsHelper.setBuddyTelephoneNumber("");
		SettingsHelper.setBuddyMessage("");
		SettingsHelper.setPeriodicUpdatesEnabled(false);
		SettingsHelper.setGpsTimeOut(SettingsHelper.DEFAULT_GPS_TIMEOUT);
		SettingsHelper.setUpdateFrequencyInSeconds(SettingsHelper.DEFAULT_UPDATE_FREQUENCY);
	}
}
