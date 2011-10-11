/**
 * 
 *  Copyright 2011 Birkett Enterprise Ltd
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

package com.birkettenterprise.phonelocator.util;

import java.util.Map;
import java.util.Vector;

import com.birkettenterprise.phonelocator.broadcastreceiver.AlarmBroadcastReceiver;
import com.birkettenterprise.phonelocator.broadcastreceiver.LocationPollerBroadcastReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsHelper implements OnSharedPreferenceChangeListener {

	private SharedPreferences mSharedPreferences;
	private Context mContext;
	private static int mUsageCount; 
	private static SettingsHelper mInstance;
	
	private static final int MILLISECONDS_IN_SECOND = 1000;
	private static final long DEFAULT_UPDATE_FREQUENCY = 3600;
	private static final String TIMESTAMP = "_timestamp";
	private static final String SETTINGS_SYNCHRONIZATION_TIMESTAMP = "settings_synchronization_timestamp";
	
	private static final String TAG = "Phonelocator";

	
	public static SettingsHelper getInstance(Context context) {
		mUsageCount++;
		if (mInstance == null) {
			mInstance = new SettingsHelper(context);
		}
		return mInstance;
	}
	
	public static void releaseInstance() {
		mUsageCount--;
		if (mUsageCount == 0) {
			mInstance.destroy();
		}
		mInstance = null;
		System.gc();
	}
	
	SettingsHelper(Context context) {
		mContext = context;
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}

	private void destroy() {
		mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
	}
		
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (!isTimeStamp(key)) {
			storeTimeStamp(key);
		}
		if (key.equals(Setting.UPDATE_FREQUENCY) || key.equals(Setting.PERIODIC_UPDATES_ENABLED)) {
			scheduleUpdates();
		}
	}
	
	private void storeTimeStamp(String key) {
	      SharedPreferences.Editor editor = mSharedPreferences.edit();
	      editor.putLong(key + TIMESTAMP, System.currentTimeMillis());
	      editor.commit();
	}

	private long getTimeStamp(String key) {
		return getSettingAsLong(key + TIMESTAMP, 0);
	}
	
	private static boolean isTimeStamp(String key) {
		return key.endsWith(TIMESTAMP);
	}
	
	public Vector<Setting> getSettingsModifiedSinceLastSyncrhonization() {
		long lastSyncrhonizationTimeStamp = getLastSynchronizationTimeStamp();
		Vector<Setting> settingsModifiedSinceLastSyncrhonization =  new Vector<Setting>();
		Map<String, ?> settings = mSharedPreferences.getAll();
		
		for (String key : settings.keySet()) {
			if (!isTimeStamp(key)) {
				long timeStamp = getTimeStamp(key);
				if (timeStamp >= lastSyncrhonizationTimeStamp) {
					settingsModifiedSinceLastSyncrhonization.add(new Setting(key, (Object)settings.get(key), timeStamp));
				}
			}
		}
		return settingsModifiedSinceLastSyncrhonization;
	}

	public void updateSettingsSynchronizationTimestamp() {
		storeTimeStamp(SETTINGS_SYNCHRONIZATION_TIMESTAMP);
	}
	
	public long getLastSynchronizationTimeStamp() {
		return getSettingAsLong(SETTINGS_SYNCHRONIZATION_TIMESTAMP, 0);
	}
	private long getUpdateIntervalInMicroSeconds(Context context) {
		return getSettingAsLong(Setting.UPDATE_FREQUENCY,
								DEFAULT_UPDATE_FREQUENCY)  * MILLISECONDS_IN_SECOND;
	}
	
	private long getSettingAsLong(String key, long defaultValue) {
		try {
			return mSharedPreferences.getLong(
					key,
					defaultValue);
		} catch (ClassCastException e) {
			String value = mSharedPreferences.getString(
					key,
					defaultValue + "");
			return Long.parseLong(value);
		}
	}
	
	private static boolean periodicUpdatesEnabled(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager
		.getDefaultSharedPreferences(context);
		return sharedPreferences.getBoolean(Setting.PERIODIC_UPDATES_ENABLED, false);
	}

	
	public void scheduleUpdates() {
		AlarmManager alamManager = (AlarmManager) mContext
				.getSystemService(Context.ALARM_SERVICE);
				
		Intent intent = new Intent(mContext, LocationPollerBroadcastReceiver.class);
		
		intent.putExtra(LocationPollerBroadcastReceiver.EXTRA_INTENT,
							 new Intent(mContext, AlarmBroadcastReceiver.class));
		intent.putExtra(LocationPollerBroadcastReceiver.EXTRA_PROVIDER,
							 LocationManager.GPS_PROVIDER);
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		if (periodicUpdatesEnabled(mContext)) {
			long intervalInMicroseconds = getUpdateIntervalInMicroSeconds(mContext);
			alamManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime(),
					intervalInMicroseconds, pendingIntent);
			Log.d(TAG, "updates scheduled every " + intervalInMicroseconds + " microseconds");
		} else {
			alamManager.cancel(pendingIntent);
			Log.d(TAG, "canceled updates");

		}
	}

}
