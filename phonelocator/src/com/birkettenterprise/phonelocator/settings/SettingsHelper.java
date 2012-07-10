/**
 * 
 *  Copyright 2011-2012 Birkett Enterprise Ltd
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 */

package com.birkettenterprise.phonelocator.settings;

import com.birkettenterprise.phonelocator.settings.Setting.BooleanSettings;
import com.birkettenterprise.phonelocator.settings.Setting.LongSettings;
import com.birkettenterprise.phonelocator.settings.Setting.StringSettings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsHelper {

	private static final String TAG = "Phonelocator";

	private static final int MILLISECONDS_IN_SECOND = 1000;
	private static final long DEFAULT_UPDATE_FREQUENCY = 3600;
	
	
	public static void scheduleUpdates(Context context) {
		AlarmManager alamManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
				
		Intent intent = new Intent("com.birkettenterprise.phonelocator.UPDATE");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		if (isPeriodicUpdatesEnabled(PreferenceManager.getDefaultSharedPreferences(context))) {
			long intervalInMicroseconds = getUpdateIntervalInMilliSeconds(PreferenceManager.getDefaultSharedPreferences(context));
			alamManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime(),
					intervalInMicroseconds, pendingIntent);
			Log.d(TAG, "updates scheduled every " + intervalInMicroseconds + " microseconds");
		} else {
			alamManager.cancel(pendingIntent);
			Log.d(TAG, "canceled updates");

		}
	}

	public static long getUpdateIntervalInMilliSeconds(SharedPreferences sharedPreferences) {
		return getSettingAsLong(sharedPreferences, Setting.StringSettings.UPDATE_FREQUENCY,
								DEFAULT_UPDATE_FREQUENCY)  * MILLISECONDS_IN_SECOND;
	}
	
	public static long getSettingAsLong(SharedPreferences sharedPreferences, String key, long defaultValue) {
		try {
			return sharedPreferences.getLong(
					key,
					defaultValue);
		} catch (ClassCastException e) {
			String value = sharedPreferences.getString(
					key,
					defaultValue + "");
			return Long.parseLong(value);
		}
	}
	
	public static void storeResponse(SharedPreferences sharedPreferences, String authenticationToken, String registrationUrl) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(Setting.StringSettings.AUTHENTICATION_TOKEN,
				authenticationToken);
		editor.putString(Setting.StringSettings.REGISTRATION_URL,
				registrationUrl);
		editor.commit();
	}
	
	public static String getAuthenticationToken(SharedPreferences sharedPreferences) {
		return sharedPreferences.getString(StringSettings.AUTHENTICATION_TOKEN, null);
	}

	public static String getRegistrationUrl(SharedPreferences sharedPreferences) {
		return sharedPreferences.getString(StringSettings.REGISTRATION_URL, null);
	}
	
	public static String getPasscode(SharedPreferences sharedPreferences) {
		return sharedPreferences.getString(StringSettings.PASSCODE, null);
	}
	
	public static boolean isPeriodicUpdatesEnabled(SharedPreferences sharedPreferences) {
		return sharedPreferences.getBoolean(BooleanSettings.PERIODIC_UPDATES_ENABLED, false);
	}	
	
	public static void setPeriodicUpdatesEnabled(SharedPreferences sharedPreferences, boolean enabled) {
		storeBoolean(sharedPreferences, BooleanSettings.PERIODIC_UPDATES_ENABLED, enabled);
	}
	
	public static boolean isRegistered(SharedPreferences sharedPreferences) {
		return sharedPreferences.getBoolean(BooleanSettings.REGISTERED, false);
	}
	
	public static boolean isHideTriggerMessage(SharedPreferences sharedPreferences) {
		return sharedPreferences.getBoolean(BooleanSettings.HIDE_SMS_TRIGGER, false);
	}
	
	public static boolean isPasscodeEnabled(SharedPreferences sharedPreferences) {
		return sharedPreferences.getBoolean(BooleanSettings.PASSCODE_ENABLED, false);
	}
	
	public static long getLastUpdateTimeStamp(SharedPreferences sharedPreferences) {
		return sharedPreferences.getLong(LongSettings.LAST_UPDATE_TIME_STAMP, 0L);
	}
	
	public static void setLastUpdateTimeStamp(SharedPreferences sharedPreferences, long value) {
		storeLong(sharedPreferences, LongSettings.LAST_UPDATE_TIME_STAMP, value);
	}
	
	public static void storeLong(SharedPreferences sharedPreferences, String key, long value) {
	      SharedPreferences.Editor editor = sharedPreferences.edit();
	      editor.putLong(key, value);
	      editor.commit();
	}
	
	public static void storeBoolean(SharedPreferences sharedPreferences, String key, boolean value) {
	      SharedPreferences.Editor editor = sharedPreferences.edit();
	      editor.putBoolean(key, value);
	      editor.commit();
	}
}
