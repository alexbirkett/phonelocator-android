/**
 *
 *  Copyright 2011-2014 Birkett Enterprise Ltd
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


package com.birkettenterprise.phonelocator.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.widget.Toast;

import com.birkett.controllers.Controller;
import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.settings.Setting;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;
import com.birkettenterprise.phonelocator.utility.AsyncSharedPreferencesListener;

/**
 * Warns the user using toasts when about potentially undesirable settings
 * @author alex
 *
 */
public class SettingsWarningController extends Controller implements OnSharedPreferenceChangeListener{

	private AsyncSharedPreferencesListener asyncSharedPreferencesListener;
	private static final long MINIMUM_RECOMENDED_UPDATE_FREQUENCY = 10 * 60 * 1000;
	private Context context;
	public SettingsWarningController(Context context) {
		super();
        this.context = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		asyncSharedPreferencesListener = new AsyncSharedPreferencesListener();
	}

	@Override
	public void onResume() {
		asyncSharedPreferencesListener.registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onPause() {
		asyncSharedPreferencesListener.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (Setting.StringSettings.BUDDY_TELEPHONE_NUMBER.equals(key)) {
			if (!SettingsHelper.getBuddyTelephoneNumber().startsWith("+")) {
				showToast(R.string.settings_warning_controller_non_international_format_buddy_number_warning);
			}
		} else if (Setting.StringSettings.UPDATE_FREQUENCY.equals(key)) {
			long updateFrequency = SettingsHelper.getUpdateFrequencyInMilliSeconds();
			if (updateFrequency < MINIMUM_RECOMENDED_UPDATE_FREQUENCY) {
				showToast(R.string.settings_warning_controller_frequent_update_warning);
			}
		}
	}
	
	private void showToast(int resourceId) {
		Toast.makeText(context, resourceId, Toast.LENGTH_LONG).show();
	}
	
	

}
