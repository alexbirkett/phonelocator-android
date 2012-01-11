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

package com.birkettenterprise.phonelocator.settings;

import java.util.Map;
import java.util.Vector;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * An instance of SettingsManager should always exist when settings are modified
 * (either locally or remotely). It's responsible for updating time stamps when
 * settings are modified.
 * 
 * When settings are modified locally, the corresponding timestamps are set to
 * the current time. When settings are received from the server, the timestamp
 * set on the server is preserved.
 *
 */
public class SettingsManager implements OnSharedPreferenceChangeListener {

	private static SettingsManager mInstance;
	
	private SharedPreferences mSharedPreferences;
	private Context mContext;
	private Vector<Object> mClients;

	private static final String TIMESTAMP = "_timestamp";
	private static final String PENDING_TIMESTAMP = "_pending" + TIMESTAMP;
	
	private static final String TAG = "Phonelocator";

	private static final String SETTINGS_SYNCHRONIZATION_TIMESTAMP = "settings_synchronization_timestamp";

	private static final long INVALID_PENDING_TIMESTAMP = -1;
	
	public static SettingsManager getInstance(Object client, Context context) {
		if (mInstance == null) {
			mInstance = new SettingsManager(context);
			mInstance.increamentClient(client);
		}
		return mInstance;
	}
	
	private void increamentClient(Object client) {
		if (!mClients.contains(client)) {
			mClients.add(client);
		}
	}
	
	public void releaseInstance(Object client) {
		mClients.remove(client);
		if (mClients.isEmpty()) {
			destroy();
			mInstance = null;
			System.gc();
		}
	}
	
	private SettingsManager(Context context) {
		mContext = context;
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
		mClients = new Vector<Object>(); 
	}

	private void destroy() {
		mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
	}
		
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		scheduleUpdatesIfRequired(key);
		updateTimestamp(key);
	}
	
	private void storeTimestamp(String key, long timestamp) {
		SettingsHelper.storeLong(mSharedPreferences, key + TIMESTAMP, timestamp); 
	}

	private void scheduleUpdatesIfRequired(String key) {
		if (key.equals(Setting.StringSettings.UPDATE_FREQUENCY) 
		 || key.equals(Setting.BooleanSettings.PERIODIC_UPDATES_ENABLED)) {
					SettingsHelper.scheduleUpdates(mContext);
		}
	}
	private void updateTimestamp(String key) {
		// timestamps (any key that ends in _timestamp) don't have timestamps of their own
		
		if (!isTimestamp(key)) {
			// pending timestamps are set when remote settings are received. The
			// remote timestamp is kept even thought the setting is updated
			long timestamp = getPendingTimestamp(key);

			if (timestamp == INVALID_PENDING_TIMESTAMP) {
				timestamp = System.currentTimeMillis();
			} else {
				// set pending timestamp to invalid so that it won't be used in
				// the future
				storePendingTimestamp(key, INVALID_PENDING_TIMESTAMP);
			}
			storeTimestamp(key, timestamp);
		}
	}
	private long getPendingTimestamp(String key) {
		long timestamp = mSharedPreferences.getLong(
				key + PENDING_TIMESTAMP,
				INVALID_PENDING_TIMESTAMP);
		Log.d(TAG, "pending timestamp for " + key + " is " + timestamp);
		
		return timestamp;
	}
	
	private void storePendingTimestamp(String key, long pendingTimestamp) {
		
		SettingsHelper.storeLong(mSharedPreferences, key + PENDING_TIMESTAMP, pendingTimestamp);
		Log.d(TAG, "stored pending timesamp for " + key + ": " + pendingTimestamp);
	}
	
	private long getTimestamp(String key) {
		return SettingsHelper.getSettingAsLong(mSharedPreferences, key + TIMESTAMP, 0);
	}
	
	private static boolean isTimestamp(String key) {
		return key.endsWith(TIMESTAMP);
	}
	
	public Vector<Setting> getSettingsModifiedSinceLastSyncrhonization() {
		long lastSyncrhonizationTimestamp = getLastSynchronizationTimestamp();
		Vector<Setting> settingsModifiedSinceLastSyncrhonization =  new Vector<Setting>();
		Map<String, ?> settings = mSharedPreferences.getAll();
		
		for (String key : settings.keySet()) {
			if (!isTimestamp(key)) {
				long timeStamp = getTimestamp(key);
				if (timeStamp >= lastSyncrhonizationTimestamp) {
					settingsModifiedSinceLastSyncrhonization.add(new Setting(key, (Object)settings.get(key), timeStamp));
				}
			}
		}
		return settingsModifiedSinceLastSyncrhonization;
	}
	
	public void setSettings(Vector<Setting> settings) {
		
		if (!settings.isEmpty()) {
			SharedPreferences.Editor editor = mSharedPreferences.edit();
			
			for (Setting setting : settings) {
				if (setting.getValue() instanceof String) {
					editor.putString(setting.getName(), (String) setting.getValue());
				} else if (setting.getValue() instanceof Integer) {
					editor.putInt(setting.getName(), (Integer) setting.getValue());
				} else if (setting.getValue() instanceof Boolean) {
					editor.putBoolean(setting.getName(), (Boolean) setting.getValue());
				}
				
				// we can't write timestamps directly, because they'll be
				// overwritten by the onSharedPreferenceChanged() callback
			    editor.putLong(setting.getName() + PENDING_TIMESTAMP, setting.getTimestamp());
			}
			editor.commit();
		}
		updateSettingsSynchronizationTimestamp(mSharedPreferences);
	}	
	
	public void updateSettingsSynchronizationTimestamp(SharedPreferences sharedPreferences) {
		SettingsHelper.storeLong(mSharedPreferences, SETTINGS_SYNCHRONIZATION_TIMESTAMP, System.currentTimeMillis());
	}
	
	public long getLastSynchronizationTimestamp() {
		return SettingsHelper.getSettingAsLong(mSharedPreferences, SETTINGS_SYNCHRONIZATION_TIMESTAMP, 0);
	}
	
}
