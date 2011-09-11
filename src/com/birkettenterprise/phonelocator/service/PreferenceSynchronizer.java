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


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceSynchronizer implements SharedPreferences.OnSharedPreferenceChangeListener {

	private SharedPreferences mSharedPreferences;
	
	public PreferenceSynchronizer(Context context) {
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}

	public void destroy() {
		mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreference, String key) {

	}	

}	
