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

import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.service.RegistrationService;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class SigninActivity extends Activity {
    
	private RegistrationService mRegisrationService;
	
	private static final int RETRY_DIALOG = 1;
	private static final String TAG = "SIGNIN_ACTIVITY";
	private static final String PHONELOCATOR_SCHEME = "phonelocator";
	
	private boolean activityInvokedFromWebLink;
	
	ProgressDialog mProgressDialog;
	
	private Runnable mRegistationObsever = new Runnable() {
		public void run() {
			handleRegistrationComplete();
		}
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityInvokedFromWebLink = isActivityInvokedFromWebLink(getIntent());
    }

	@Override
	public void onPause() {
		super.onPause();
		if (isServiceBound() && mRegisrationService.isRunning()) {
			mRegisrationService.removeObserver(mRegistationObsever);		
		}
		stopProgressIfNotStopped();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		performNextRegistrationStep();
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    doUnbindService();
	}
	
	@Override
	protected void onNewIntent (Intent intent) {
		activityInvokedFromWebLink = isActivityInvokedFromWebLink(intent);
	}
	
	private static boolean isActivityInvokedFromWebLink(Intent intent) {
		Uri data = intent.getData();
		if (data!=null) {
			String scheme = data.getScheme();
			if (scheme.equals(PHONELOCATOR_SCHEME)) {
				return true;
			}
		}
		return false;
	}
	
	private void startStatusActvity() {
		Intent intent = new Intent(this, UpdateLogActivity.class);
        startActivity(intent);
		finish();
	}
	
	private void openUrl(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	 	startActivity(intent);
		finish();
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        mRegisrationService = ((RegistrationService.RegistrationServiceBinder)service).getService();
	        mRegisrationService.addObserver(mRegistationObsever);
	        performNextRegistrationStep();
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
		performNextRegistrationStep();
	}

	private void openRegistrationUrl() {
		openUrl(mRegisrationService.getResponse().getRegistrationUrl());
	}
		
	private boolean isRegistered() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		return SettingsHelper.isRegistered(sharedPreferences);
	}
	
	private void performNextRegistrationStep() {

		if (isRegistered()) {
			Log.d(TAG, "performNextRegistrationStep registered");
			startStatusActvity();
			stopProgressIfNotStopped();
			return;
		} else {
			startProgressIfNotStarted();
			if (isServiceBound()) {
				if (mRegisrationService.isRunning()) {
					mRegisrationService.addObserver(mRegistationObsever);
					Log.d(TAG, "performNextRegistrationStep service running (adding observer");
				} else {
					if (activityInvokedFromWebLink) {
						Log.d(TAG, "performNextRegistrationStep activity is invoked from web link");
						mRegisrationService.synchronize();
					} else {
						if (mRegisrationService.isSuccess()) {
							Log.d(TAG, "performNextRegistrationStep registration success");
							openRegistrationUrl();
						} else if (mRegisrationService.isErrorOccured()) {
							Log.d(TAG, "performNextRegistrationStep registration error");
							showDialog(RETRY_DIALOG);
						} else {
							Log.d(TAG, "performNextRegistrationStep registering");
							mRegisrationService.register();
							// show retry dialog
						}
					}
				}
			} else {
				Log.d(TAG, "performNextRegistrationStep bind service");
				doBindService();
			}
		}
	}
	
	protected Dialog onCreateDialog(int id) {
		   switch(id) {
		      case RETRY_DIALOG:
		         return new AlertDialog.Builder(this).setTitle(R.string.connection_failed)
		         .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
	
		                public void onClick(DialogInterface dialog, int which) {
		                	mRegisrationService.clearResponse();
		                	performNextRegistrationStep();
		                }
		            })
		            .setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                    finish();
		                }
		            })
		            .create();
		    }

		    return null;
		}
	
	private void startProgressIfNotStarted() {

		if (!isProgressDialogActive()) {
			DialogInterface.OnCancelListener listener = new DialogInterface.OnCancelListener() {

				public void onCancel(DialogInterface dialog) {
					finish();
				}
			};

			mProgressDialog = ProgressDialog.show(this, "",getString(R.string.registering_progress_dialog_message),
					true, true, listener);
		}
	}
	
	private void stopProgressIfNotStopped() {
		if (isProgressDialogActive()) {
			mProgressDialog.dismiss();
		}
	}
	
	private boolean isProgressDialogActive() {
		return mProgressDialog != null;
	}
}
