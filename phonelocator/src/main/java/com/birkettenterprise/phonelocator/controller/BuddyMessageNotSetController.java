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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.birkett.controllers.ViewController;
import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.activity.BuddyMessageActivity;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;
import com.birkettenterprise.phonelocator.utility.AsyncSharedPreferencesListener;
import com.birkettenterprise.phonelocator.utility.StringUtil;

public class BuddyMessageNotSetController extends ViewController implements OnSharedPreferenceChangeListener{

	private AsyncSharedPreferencesListener asyncSharedPreferencesListener;
	
	public BuddyMessageNotSetController(Context context) {
		super(context);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		asyncSharedPreferencesListener = new AsyncSharedPreferencesListener();
		
		setContentView(R.layout.buddy_message_not_set_controller);


		Button button = (Button) getView().findViewById(
				R.id.buddy_message_not_set_controller_buddy_settings);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mContext.startActivity(new Intent(mContext,
						BuddyMessageActivity.class));
			}

		});
	}

	@Override
	public void onResume() {
		displayHideController();
		asyncSharedPreferencesListener.registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onPause() {
		asyncSharedPreferencesListener.unregisterOnSharedPreferenceChangeListener(this);
	}
	
	/**
	 * Only display the buddy message not set controller if updates are enabled
	 * because we don't want to overload the user with info when they first
	 * start the app
	 * 
	 * @return
	 */
	private boolean displayController() {

		return (SettingsHelper.isPeriodicUpdatesEnabled() &&
				(StringUtil.isNullOrWhiteSpace(SettingsHelper.getBuddyMessage()) ||
						StringUtil.isNullOrWhiteSpace(SettingsHelper.getBuddyTelephoneNumber())));
				
	}
	
	public void displayHideController() {
		if (displayController()) {
			getView().setVisibility(View.VISIBLE);
		} else {
			getView().setVisibility(View.GONE);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		displayHideController();
	}

}
