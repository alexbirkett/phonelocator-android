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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SigninActivity extends Activity implements OnClickListener {
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);
        
       findViewById(R.id.sign_in).setOnClickListener(this);
       findViewById(R.id.sign_up).setOnClickListener(this);
    }

	public void onClick(View view) {
		if (view == findViewById(R.id.sign_in)) {
			activeStatus();
		} else {
			openUrl();
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
	
	private void openUrl() {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://10.0.0.3:3000"));
		startActivity(browserIntent);
	}
}
