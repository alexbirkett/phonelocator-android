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


import java.io.IOException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.broadcastreceiver.LocationPollerBroadcastReceiver;
import com.birkettenterprise.phonelocator.database.Database;
import com.birkettenterprise.phonelocator.domain.BeaconList;
import com.birkettenterprise.phonelocator.domain.GpsBeacon;
import com.birkettenterprise.phonelocator.protocol.Session;

public class UpdateService extends WakefulIntentService {

	public static final String COMMAND = "command";
		
	public static final int UPDATE_LOCATION = 1;
	public static final int SYNCHRONIZE_SETTINGS = 2;

	private static final String TAG = "Phonelocator";
	private Database mDatabase;
	
	public UpdateService() {
		super("PhonelocatorSerivce");
		mDatabase = new Database(this);
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		int command = intent.getIntExtra(COMMAND, -1);
			
		//if ((command & UPDATE_LOCATION) == UPDATE_LOCATION) {
		handleUpdateLocation(intent);
	
		if ((command & SYNCHRONIZE_SETTINGS) == SYNCHRONIZE_SETTINGS) {
			handleSynchronizeSettings();
		}
	}
	
	private void handleUpdateLocation(Intent intent) {
		Log.v(TAG, "handleUpdateLocation");
		
		Session session = new Session();
		try {
			session.connect();
			session.authenticate(getAuthenticationToken());
			
			Bundle bundle = intent.getExtras();
			Location location = (Location) bundle.get(LocationPollerBroadcastReceiver.EXTRA_LOCATION);
			if (location == null) {
				location = (Location) bundle.get(LocationPollerBroadcastReceiver.EXTRA_LASTKNOWN);
			}
			BeaconList beaconList = new BeaconList();
			beaconList.add(new GpsBeacon(location, intent.getStringExtra(LocationPollerBroadcastReceiver.EXTRA_ERROR)));
			session.sendPositionUpdate(beaconList);
			
		} catch (Exception e) {
			
		} finally {
			try {
				session.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
   
	private String getAuthenticationToken() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		return sharedPreferences.getString(getString(R.string.authentication_token_key), "");
	}
	
	private void handleSynchronizeSettings() {
		
	}
}
