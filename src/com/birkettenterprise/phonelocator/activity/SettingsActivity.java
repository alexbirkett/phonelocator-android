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

package com.birkettenterprise.phonelocator.activity;

import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.broadcastreceiver.AlarmBroadcastReceiver;
import com.birkettenterprise.phonelocator.broadcastreceiver.LocationPollerBroadcastReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	public static final String TIMESTAMP = "_timestamp";
	
	private static final int MILLISECONDS_IN_SECOND = 1000;
	private static final String DEFAULT_UPDATE_FREQUENCY = "3600";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (!key.endsWith(TIMESTAMP)) {
			storeTimeStamp(sharedPreferences, key);
		}
		if (key.equals(getString(R.string.update_frequency_key))) {
			handleUpdateFrequencyChanged(this);
		}
	}

	private void storeTimeStamp(SharedPreferences sharedPreferences,
			String key) {
	      SharedPreferences.Editor editor = sharedPreferences.edit();
	      editor.putLong(key + TIMESTAMP, System.currentTimeMillis());
	      editor.commit();
	}
	
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}
	
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}
	
	
	public static long getUpdateIntervalInMicroSeconds(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return Long.parseLong(sharedPreferences.getString(
				context.getString(R.string.update_frequency_key),
				DEFAULT_UPDATE_FREQUENCY)) * MILLISECONDS_IN_SECOND;
	}
	
	public static void handleUpdateFrequencyChanged(Context context) {

		long intervalInMicroseconds = getUpdateIntervalInMicroSeconds(context);

		AlarmManager alamManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		
		/*Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
		
		intent.putExtra(UpdateService.COMMAND, UpdateService.SYNCHRONIZE_SETTINGS | UpdateService.UPDATE_LOCATION);
		// Extras not sent if no action is set: http://stackoverflow.com/questions/3127957/why-the-pendingintent-doesnt-send-back-my-custom-extras-setup-for-the-intent
		intent.setAction("foo");*/
		
		Intent intent = new Intent(context, LocationPollerBroadcastReceiver.class);
		
		intent.putExtra(LocationPollerBroadcastReceiver.EXTRA_INTENT,
							 new Intent(context, AlarmBroadcastReceiver.class));
		intent.putExtra(LocationPollerBroadcastReceiver.EXTRA_PROVIDER,
							 LocationManager.GPS_PROVIDER);
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		alamManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime(),
				intervalInMicroseconds, pendingIntent);
	}
	
}
