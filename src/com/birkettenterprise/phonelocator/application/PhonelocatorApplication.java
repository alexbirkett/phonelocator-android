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

import com.birkettenterprise.phonelocator.service.PhonelocatorService;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class PhonelocatorApplication extends Application {

	private static PhonelocatorApplication mInstance;
	
	@Override
	public void onCreate() {
		super.onCreate();
		startService(this);
		setInstanceVariable();
	}
	
	/**
	 * Start service
	 * <p>
	 * The service should run even after the activities are destroyed
	 */
	public static void startService(Context context) {
		Intent startServiceIntent = new Intent(context, PhonelocatorService.class);
	    context.startService(startServiceIntent);
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
}
