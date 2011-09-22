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


import android.content.Intent;
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
		int command = intent.getIntExtra(COMMAND, 666);
			
		if ((command & UPDATE_LOCATION) == UPDATE_LOCATION) {
			handleUpdateLocation();
		}

		if ((command & SYNCHRONIZE_SETTINGS) == SYNCHRONIZE_SETTINGS) {
			handleSynchronizeSettings();
		}
	}
	
	private void handleUpdateLocation() {
		Log.v(TAG, "handleUpdateLocation");
	}
	
	private void handleSynchronizeSettings() {
		
	}
}
