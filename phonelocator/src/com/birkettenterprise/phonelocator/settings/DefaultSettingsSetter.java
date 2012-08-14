package com.birkettenterprise.phonelocator.settings;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Set default settings to be synchronized with server during registration
 * @param sharedPreferences
 * @param context
 */
public class DefaultSettingsSetter {

	
	public static void setDefaultSettings(SharedPreferences sharedPreferences, Context context) {
		
		SettingsHelper.setPasscode(sharedPreferences, "");
		SettingsHelper.setBuddyTelephoneNumber(sharedPreferences, "");
		SettingsHelper.setBuddyMessage(sharedPreferences, "");
		SettingsHelper.setPeriodicUpdatesEnabled(sharedPreferences, false);
	}
}
