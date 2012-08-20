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

package com.birkettenterprise.phonelocator.application;

import com.birkettenterprise.phonelocator.settings.SettingTimestampListener;

//import net.hockeyapp.android.CrashManager;
import android.app.Application;

public class PhonelocatorApplication extends Application {

	public static String LOG_TAG = "PHONELOCATOR";
	
	private static PhonelocatorApplication mInstance;
	private SettingTimestampListener mSettingTimestampListener;
	
	@Override
	public void onCreate() {
		super.onCreate();
		setInstanceVariable();
//		CrashManager.registerHandler();
		createTimestampListener();
	}
	
	private void setInstanceVariable() {
		if (mInstance != null) {
			throw new RuntimeException();
		}
		mInstance = this;
	}
	
	public static PhonelocatorApplication getInstance() {
		return mInstance;
	}
	
	private void createTimestampListener() {
		// A setting timestamp listener needs to exist while the app process is running.
		// A service is not used because we don't want to indicate to the framework that an operation is in progress
		mSettingTimestampListener = new SettingTimestampListener(this);
	}
	
	public SettingTimestampListener getSettingTimestampListener() {
		return mSettingTimestampListener;
	}
}
