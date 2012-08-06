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

package com.birkettenterprise.phonelocator.utility;

import com.birkettenterprise.phonelocator.broadcastreceiver.SendWorkToUpdateServiceBroadcastReceiver;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;
import com.commonsware.cwac.locpoll.LocationPoller;
import com.commonsware.cwac.locpoll.LocationPollerParameter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class UpdateUtility {
	

	public static void pollLocationAndSendUpdate(Context context) {
		Bundle bundle = new Bundle();
		
		LocationPollerParameter parameter = new LocationPollerParameter(bundle);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		parameter.setTimeout(SettingsHelper.getGpsTimeOut(sharedPreferences));
		if (SettingsHelper.isGpsEnabled(sharedPreferences)) {
			parameter.addProvider(LocationManager.GPS_PROVIDER);
		}
		parameter.addProvider(LocationManager.NETWORK_PROVIDER);
		parameter.setIntentToBroadcastOnCompletion(new Intent(context, SendWorkToUpdateServiceBroadcastReceiver.class));
		
		Intent intent = new Intent(context, LocationPoller.class);
		intent.putExtras(bundle);
		
		context.sendBroadcast(intent);
	}
	
}
