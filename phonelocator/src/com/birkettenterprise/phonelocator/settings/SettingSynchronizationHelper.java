/**
 * 
 *  Copyright 2012 Birkett Enterprise Ltd
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

import android.content.SharedPreferences;

public class SettingSynchronizationHelper {
	
	private static final String SETTINGS_SYNCHRONIZATION_TIMESTAMP = "settings_synchronization_timestamp";

	public static Vector<Setting> getSettingsModifiedSinceLastSyncrhonization(SharedPreferences sharedPreferences) {
		long lastSyncrhonizationTimestamp = getLastSynchronizationTimestamp(sharedPreferences);
		Vector<Setting> settingsModifiedSinceLastSyncrhonization =  new Vector<Setting>();
		Map<String, ?> settings = sharedPreferences.getAll();
		
		for (String key : settings.keySet()) {
			if (!SettingTimestampHelper.isTimestamp(key)) {
				long timeStamp = SettingTimestampHelper.getTimestamp(sharedPreferences, key);
				if (timeStamp >= lastSyncrhonizationTimestamp) {
					settingsModifiedSinceLastSyncrhonization.add(new Setting(key, (Object)settings.get(key), timeStamp));
				}
			}
		}
		return settingsModifiedSinceLastSyncrhonization;
	}
	
	public static void setSettings(SharedPreferences sharedPreferences, Vector<Setting> settings) {
		
		if (!settings.isEmpty()) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			
			for (Setting setting : settings) {
				if (setting.getValue() instanceof String) {
					SettingsHelper.putStringIfRequired(editor, sharedPreferences, setting.getName(), (String) setting.getValue());
				} else if (setting.getValue() instanceof Integer) {
					SettingsHelper.putIntIfRequired(editor, sharedPreferences, setting.getName(), (Integer) setting.getValue());
				} else if (setting.getValue() instanceof Boolean) {
					SettingsHelper.putBooleanIfRequired(editor, sharedPreferences,  setting.getName(), (Boolean) setting.getValue());
				}
				
				// we can't write timestamps directly, because they'll be
				// overwritten by the onSharedPreferenceChanged() callback
			    editor.putLong(setting.getName() + SettingTimestampHelper.PENDING_TIMESTAMP, setting.getTimestamp());
			}
			editor.commit();
		}
	}	
	
	public static void updateSettingsSynchronizationTimestamp(SharedPreferences sharedPreferences) {
		SettingsHelper.storeLong(sharedPreferences, SETTINGS_SYNCHRONIZATION_TIMESTAMP, System.currentTimeMillis());
	}
	
	public static long getLastSynchronizationTimestamp(SharedPreferences sharedPreferences) {
		return SettingsHelper.getSettingAsLong(sharedPreferences, SETTINGS_SYNCHRONIZATION_TIMESTAMP, 0);
	}
}
