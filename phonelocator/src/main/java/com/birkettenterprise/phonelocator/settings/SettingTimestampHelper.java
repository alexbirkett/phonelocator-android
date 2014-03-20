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


import android.content.SharedPreferences;
import android.util.Log;

public class SettingTimestampHelper {
	
	private static final String TIMESTAMP = "_timestamp";
	public static final String PENDING_TIMESTAMP = "_pending" + TIMESTAMP;
	
	private static final long INVALID_PENDING_TIMESTAMP = -1;

	private static final String TAG = "Phonelocator";
	
	public static long getTimestamp(SharedPreferences sharedPreferences, String key) {
		return SettingsHelper.getSettingAsLong(sharedPreferences, key + TIMESTAMP, 0);
	}
	
	public static boolean isTimestamp(String key) {
		return key.endsWith(TIMESTAMP);
	}
	
	public static void storeTimestamp(SharedPreferences sharedPreferences, String key, long timestamp) {
		SettingsHelper.storeLong(sharedPreferences, key + TIMESTAMP, timestamp); 
	}

	public static long getPendingTimestamp(SharedPreferences sharedPreferences, String key) {
		long timestamp = sharedPreferences.getLong(
				key + PENDING_TIMESTAMP,
				INVALID_PENDING_TIMESTAMP);
		Log.d(TAG, "pending timestamp for " + key + " is " + timestamp);
		return timestamp;
	}
	
	public static void storePendingTimestamp(SharedPreferences sharedPreferences, String key, long pendingTimestamp) {
		SettingsHelper.storeLong(sharedPreferences, key + PENDING_TIMESTAMP, pendingTimestamp);
		Log.d(TAG, "stored pending timesamp for " + key + ": " + pendingTimestamp);
	}

	public static boolean isInvalidTimestamp(long timestamp) {
		return timestamp == INVALID_PENDING_TIMESTAMP;
	}

	public static void setTimesampInvalid(SharedPreferences sharedPreferences, String key) {
		storePendingTimestamp(sharedPreferences, key, INVALID_PENDING_TIMESTAMP);
	}
	
}
