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
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.birkett.controllers.ViewController;
import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.activity.SettingsActivity;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;

public class UpdatesDisabledController extends ViewController {

	
	public UpdatesDisabledController(Context context) {
		super(context);
	}

	@Override 
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.updates_disabled_controller);
		
		Button enableUpdatesButton = (Button) getView().findViewById(R.id.button_enable_updates);
		enableUpdatesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SettingsHelper.setPeriodicUpdatesEnabled(true);
			}
			
		});
		
		Button settingsButton = (Button) getView().findViewById(R.id.button_settings);
		settingsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mContext.startActivity(new Intent(mContext, SettingsActivity.class));
			}
			
		});
	}
	
}
