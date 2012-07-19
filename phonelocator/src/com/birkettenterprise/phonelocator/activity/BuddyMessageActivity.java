package com.birkettenterprise.phonelocator.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.controller.PasscodeController;

import no.birkettconsulting.controllers.BaseControllerActivity;

public class BuddyMessageActivity extends BaseControllerActivity {

	View.OnClickListener mSelectFromContactsButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
			startActivityForResult(intent, 1);
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		addController(new PasscodeController(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buddy_message_activity);
		
		View button = findViewById(R.id.buddy_message_select_from_contacts);
	    button.setOnClickListener(mSelectFromContactsButtonListener);
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
						String number = c.getString(0);
						int type = c.getInt(1);
						showSelectedNumber(type, number);
					}
				} finally {
					if (c != null) {
						c.close();
					}
				}
			}
		}
	}

	public void showSelectedNumber(int type, String number) {
	    Toast.makeText(this, type + ": " + number, Toast.LENGTH_LONG).show();      
	}
}
