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

package com.birkettenterprise.phonelocator.broadcastreceiver;

import com.commonsware.cwac.locpoll.LocationPoller;
import com.commonsware.cwac.locpoll.LocationPollerParameter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
	
	private static long TIMEOUT = 20 * 1000;
	private static String GPS_TIMEOUT_KEY = "gps_timeout";
	private static String GPS_ENABLED = "gps_enabled";
	
	@Override
	public void onReceive(Context context, Intent i) {
		

		Bundle bundle = new Bundle();
	
		LocationPollerParameter parameter = new LocationPollerParameter(bundle);
		parameter.setTimeout(getGpsTimeout(context));
		if (isGpsEnabled(context)) {
			parameter.addProvider(LocationManager.GPS_PROVIDER);
		}
		parameter.addProvider(LocationManager.NETWORK_PROVIDER);
		parameter.setIntentToBroadcastOnCompletion(new Intent(context, LocationBroadcastReceiver.class));
		
		Intent intent = new Intent(context, LocationPoller.class);
		intent.putExtras(bundle);
		
		context.sendBroadcast(intent);
	}
	
	private long getGpsTimeout(Context context) {
		SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(context);
		String timeOutString = preferenceManager.getString(GPS_TIMEOUT_KEY,TIMEOUT+ "");
		long timeout = Long.parseLong(timeOutString);
		return timeout;
	}
	
	private boolean isGpsEnabled(Context context) {
		SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(context);
		boolean gpsEnabled = preferenceManager.getBoolean(GPS_ENABLED, false);
		return gpsEnabled;
	}
}