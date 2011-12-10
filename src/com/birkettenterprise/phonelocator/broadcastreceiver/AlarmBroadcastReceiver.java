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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;


public class AlarmBroadcastReceiver extends BroadcastReceiver {
	
	private long TIMEOUT = 20 * 1000;
	
	@Override
	public void onReceive(Context context, Intent i) {
		
		Intent intent = new Intent(context, LocationPoller.class);
		intent.putExtra(LocationPoller.EXTRA_INTENT_TO_BROADCAST_ON_COMPLETION, new Intent(context, LocationBroadcastReceiver.class));
		intent.putExtra(LocationPoller.EXTRA_PROVIDER, LocationManager.GPS_PROVIDER);
		intent.putExtra(LocationPoller.EXTRA_TIMEOUT, TIMEOUT);
		context.sendBroadcast(intent);
	}
}