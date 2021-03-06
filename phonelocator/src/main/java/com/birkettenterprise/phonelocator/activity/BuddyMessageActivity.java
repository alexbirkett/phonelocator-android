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

package com.birkettenterprise.phonelocator.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;

import com.birkett.controllers.ActivityThatSupportsControllers;
import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.controller.PasscodeController;
import com.birkettenterprise.phonelocator.controller.SettingsWarningController;
import com.birkettenterprise.phonelocator.settings.Setting;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;

public class BuddyMessageActivity extends ActivityThatSupportsControllers {
	
	View.OnClickListener mSaveListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			save();
		}
	};
	
	View.OnClickListener mCancelListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			cancel();
		}
	};
	
	View.OnClickListener mSelectFromContactsListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			selectFromContacts();
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		addController(new PasscodeController(this));
		addController(new SettingsWarningController(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buddy_message_activity);
		setListeners();
		loadSettings();
	}
	
	private void setListeners() {
		findViewById(R.id.buddy_message_activity_cancel_button).setOnClickListener(mCancelListener);
		findViewById(R.id.buddy_message_activity_save_button).setOnClickListener(mSaveListener);
		findViewById(R.id.buddy_message_select_number_button).setOnClickListener(mSelectFromContactsListener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			Uri uri = data.getData();

			if (uri != null) {
				Cursor c = null;
				try {
					c = getContentResolver()
							.query(uri,
									new String[] {
											ContactsContract.CommonDataKinds.Phone.NUMBER,
											ContactsContract.CommonDataKinds.Phone.TYPE },
									null, null, null);

					if (c != null && c.moveToFirst()) {
						setBuddyNumber(c.getString(0));
					}
				} finally {
					if (c != null) {
						c.close();
					}
				}
			}
		}
	}
	
	
	
	public void selectFromContacts() {
		startContactsActivity();
	}

	public void save() {
		saveSettings();
		finish();
	}

	public void cancel() {
		finish();
	}

	private void startContactsActivity() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
		startActivityForResult(intent, 1);
	}
	
	private void saveSettings() {
		SharedPreferences sharedPreferences = getSharedPreferences();
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(Setting.StringSettings.BUDDY_MESSAGE, getBuddyMessage());
		editor.putString(Setting.StringSettings.BUDDY_TELEPHONE_NUMBER, getBuddyNumber());
		editor.commit();
	}
	
	private String getBuddyMessage() {
		EditText editText = (EditText)findViewById(R.id.buddy_message_edit_text);
		return editText.getText().toString();
	}
	
	private void setBuddyMessage(String number) {
		EditText editText = (EditText)findViewById(R.id.buddy_message_edit_text);
		editText.setText(number);
	}
	
	private String getBuddyNumber() {
		EditText editText = (EditText)findViewById(R.id.buddy_number_edit_text);
		return editText.getText().toString();
	}
	
	private void setBuddyNumber(String number) {
		EditText editText = (EditText)findViewById(R.id.buddy_number_edit_text);
		editText.setText(number);
	}
	
	private void loadSettings() {
		SharedPreferences sharedPreferences = getSharedPreferences();
		setBuddyMessage(sharedPreferences.getString(Setting.StringSettings.BUDDY_MESSAGE, getDefaultBuddyMessage()));
		setBuddyNumber(sharedPreferences.getString(Setting.StringSettings.BUDDY_TELEPHONE_NUMBER, ""));
	}
	
	private SharedPreferences getSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(this);
	}

    private String getDefaultBuddyMessage() {
        String defaultBuddyMessage = "";
        String trackerName = SettingsHelper.getTrackerName();
        if (trackerName != null) {
            defaultBuddyMessage = String.format(getString(R.string.buddy_message_default_message), trackerName);
        }
        return defaultBuddyMessage;
    }
}
