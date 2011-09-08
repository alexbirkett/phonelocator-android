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
import com.birkettenterprise.phonelocator.service.PhonelocatorService;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class StatusActivity extends Activity implements OnClickListener {
    
	private PhonelocatorService mBoundService;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);
        doBindService();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.status_menu, menu);
        return true;
    }
    
    @Override
	protected void onDestroy() {
	    super.onDestroy();
	    doUnbindService();
	}
    
    @Override
	public Dialog onCreateDialog(int id) {
		Dialog dialog = new Dialog(this);

		dialog.setContentView(R.layout.signin);
		dialog.setTitle(getString(R.string.sign_in_dialog_title));
		dialog.findViewById(R.id.sign_in).setOnClickListener(this);
		dialog.findViewById(R.id.sign_up).setOnClickListener(this);
		setButtonWidths(dialog);
		return dialog;
	}
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		showDialog(1);
		return true;
	}
	
	public void onClick(View view) {
		if (view == findViewById(R.id.sign_in)) {
			activeStatus();
		} else {
			activateSignup();
		}
	}
	
	private void setButtonWidths(Dialog view) {
		View button1 = view.findViewById(R.id.sign_in);
		View button2 = view.findViewById(R.id.sign_up);
		if (button1.getWidth() > button2.getWidth()) {
			button2.setMinimumWidth(button1.getWidth());
			button2.invalidate();
		} else {
			button1.setMinimumWidth(button2.getWidth());
			button1.invalidate();
		}
	}
	
	private void activeStatus() {
		finish();
		Intent intent = new Intent(this, StatusActivity.class);
        startActivity(intent);
	}
	
	private void activateSignup() {
		Uri uri = Uri.parse(getString(R.string.sign_up_url));
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        mBoundService = ((PhonelocatorService.PhonelocatorServiceBinder)service).getService();
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        mBoundService = null;
	    }
	};

	void doBindService() {
		getApplicationContext().bindService(new Intent(StatusActivity.this, PhonelocatorService.class), mConnection, Context.BIND_AUTO_CREATE);
	}
	
	

	void doUnbindService() {
	    if (isServiceBound()) {
	        unbindService(mConnection);
	    }
	}

	private boolean isServiceBound() {
		return mBoundService != null;
	}
}
