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

import com.birkettenterprise.phonelocator.service.RegistrationService;
import com.birkettenterprise.phonelocator.util.SettingsHelper;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class SigninActivity extends Activity {
    
	private RegistrationService mRegisrationService;
	public enum State {
	    IDLE,
	    REGISTERING
	};
	private State mState;
	
	private Runnable mRegistationObsever = new Runnable() {
		public void run() {
			handleRegistrationComplete();
		}
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.signin);
	    mState = State.IDLE;
        if (hasAuthenticationToken()) {
        	startStatusActvity();
        } else {
        	mState = State.REGISTERING;
            doBindService();
        }

    }

	@Override
	public void onPause() {
		super.onPause();
		if (isServiceBound() && mState == State.REGISTERING) {
			mRegisrationService.removeObserver(mRegistationObsever);		
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (isServiceBound() && mState == State.REGISTERING) {
			if (mRegisrationService.isRunning()) {
				mRegisrationService.addObserver(mRegistationObsever);						
			} else {
				// registration completed while we were away
				handleRegistrationComplete();
			}
		}		
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    doUnbindService();
	}
	
	private void startStatusActvity() {
		finish();
		Intent intent = new Intent(this, TabsAcitvity.class);
        startActivity(intent);
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        mRegisrationService = ((RegistrationService.RegistrationServiceBinder)service).getService();
	        if (!mRegisrationService.isRunning()) {
	            mRegisrationService.register();   	
	        }
	        mRegisrationService.addObserver(mRegistationObsever);
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        mRegisrationService = null;
	    }
	};

	void doBindService() {
		getApplicationContext().bindService(new Intent(SigninActivity.this, RegistrationService.class), mConnection, Context.BIND_AUTO_CREATE);
	}
	
	void doUnbindService() {
	    if (isServiceBound()) {
	    	getApplicationContext().unbindService(mConnection);
	    }
	}

	private boolean isServiceBound() {
		return mRegisrationService != null;
	}

	private void handleRegistrationComplete() {
		if (mRegisrationService.isSuccess()) {
			openRegistrationUrl();
		} else {
			// show re-try / exit dialog
		}
	}

	private void openRegistrationUrl() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		openUrl(SettingsHelper.getRegistrationUrl(sharedPreferences));
	}
	
	private void openUrl(String url) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
	}
	
	private boolean hasAuthenticationToken() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		return SettingsHelper.getAuthenticationToken(sharedPreferences) != null;
	}
}
