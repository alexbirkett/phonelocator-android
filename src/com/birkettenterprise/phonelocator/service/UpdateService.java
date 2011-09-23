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

package com.birkettenterprise.phonelocator.service;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import com.birkettenterprise.phonelocator.broadcastreceiver.LocationPollerBroadcastReceiver;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class UpdateService extends WakefulIntentService {

	public static final String COMMAND = "command";
		
	public static final int UPDATE_LOCATION = 1;
	public static final int SYNCHRONIZE_SETTINGS = 2;

	private static final String TAG = "PhonelocatorService";
	
	public UpdateService() {
		super("PhonelocatorSerivce");
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		int command = intent.getIntExtra(COMMAND, -1);
			
		//if ((command & UPDATE_LOCATION) == UPDATE_LOCATION) {
			handleUpdateLocation(intent);
		//}

		if ((command & SYNCHRONIZE_SETTINGS) == SYNCHRONIZE_SETTINGS) {
			handleSynchronizeSettings();
		}
	}
	
	private void handleUpdateLocation(Intent intent) {
		Log.v(TAG, "handleUpdateLocation");
		File log = new File(Environment.getExternalStorageDirectory(),
				"PhonelocatorLocationLog.txt");

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(
					log.getAbsolutePath(), log.exists()));

			out.write(new Date().toString());
			out.write(" : ");

			Bundle b = intent.getExtras();
			Location loc = (Location) b
					.get(LocationPollerBroadcastReceiver.EXTRA_LOCATION);
			String msg;

			if (loc == null) {
				loc = (Location) b
						.get(LocationPollerBroadcastReceiver.EXTRA_LASTKNOWN);

				if (loc == null) {
					msg = intent
							.getStringExtra(LocationPollerBroadcastReceiver.EXTRA_ERROR);
				} else {
					msg = "TIMEOUT, lastKnown=" + loc.toString();
				}
			} else {
				msg = loc.toString();
			}

			if (msg == null) {
				msg = "Invalid broadcast received!";
			}

			out.write(msg);
			out.write("\n");
			out.close();
		} catch (IOException e) {
			Log.e(getClass().getName(), "Exception appending to log file", e);
		}
	}
	
	private void handleSynchronizeSettings() {
		
	}
}
