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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

/**
 * An instance of SettingTimestampListener should always exist when settings are modified
 * (either locally or remotely). It's responsible for updating time stamps when
 * settings are modified.
 * 
 * When settings are modified locally, the corresponding timestamps are set to
 * the current time. When settings are received from the server, the timestamp
 * set on the server is preserved.
 *
 */
public class SettingTimestampListener implements OnSharedPreferenceChangeListener {

	
	private SharedPreferences sharedPreferences;
	private Context context;
	
	public SettingTimestampListener(Context context) {
		this.context = context;
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}
		
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		scheduleUpdatesIfRequired(key);
		updateTimestamp(key);
	}
	
	private void scheduleUpdatesIfRequired(String key) {
		if (key.equals(Setting.StringSettings.UPDATE_FREQUENCY) 
		 || key.equals(Setting.BooleanSettings.PERIODIC_UPDATES_ENABLED)) {
					SettingsHelper.scheduleUpdates(context);
		}
	}
	private void updateTimestamp(String key) {
		// timestamps (any key that ends in _timestamp) don't have timestamps of their own
		
		if (!SettingTimestampHelper.isTimestamp(key)) {
			// pending timestamps are set when remote settings are received. The remote timestamp is stored
			long timestamp = SettingTimestampHelper.getPendingTimestamp(sharedPreferences, key);

			if (SettingTimestampHelper.isInvalidTimestamp(timestamp)) {
				timestamp = System.currentTimeMillis();
			} else {
				// set pending timestamp to invalid so that it won't be used in
				// the future
				SettingTimestampHelper.setTimesampInvalid(sharedPreferences, key);
			}
			SettingTimestampHelper.storeTimestamp(sharedPreferences, key, timestamp);
		}
	}
}
