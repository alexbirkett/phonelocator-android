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

package com.birkettenterprise.phonelocator.activity;


import com.birkett.controllers.PreferenceActivityThatSupportsControllers;
import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.controller.PasscodeController;
import com.birkettenterprise.phonelocator.controller.SettingsWarningController;

import android.os.Bundle;

public class SettingsActivity extends PreferenceActivityThatSupportsControllers {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		addController(new PasscodeController(this));
		addController(new SettingsWarningController(this));
		super.onCreate(savedInstanceState);
	}
	
	protected void onResume() {
		super.onResume();	
		// If the shared preferences are updated elsewhere, i.e. from the network. The view does not automatically refresh to reflect the new values.
		// as a temporary work around, we add the settings every time the activity is resumed and remove them when it is destroyed
		addPreferencesFromResource(R.xml.preferences);
	}
	
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().removeAll();
	}
	
}
