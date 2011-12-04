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

import java.util.Date;

import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.database.UpdateLogDatabase;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class UpdateLogActivity extends ListActivity implements OnClickListener {
    
	private Cursor mCursor;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        // We'll define a custom screen layout here (the one shown above), but
        // typically, you could just use the standard ListActivity layout.
        setContentView(R.layout.update_log);

        // Query for all people contacts using the Contacts.People convenience class.
        // Put a managed wrapper around the retrieved cursor so we don't have to worry about
        // requerying or closing it as the activity changes state.
        mCursor = new UpdateLogDatabase(this).getUpdateTable();
        startManagingCursor(mCursor);

        // Now create a new list adapter bound to the cursor.
        // SimpleListAdapter is designed for binding to a Cursor.
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this, // Context.
                R.layout.update_log_list_item,  // Specify the row template to use (here, two columns bound to the two retrieved cursor rows).
                mCursor,                                              // Pass in the cursor to bind to.
                new String[] { UpdateLogDatabase.ERROR_COLUMN, 
                		       UpdateLogDatabase.TIMESTAMP_COLUMN },           // Array of cursor columns to bind to.
                new int[] {R.id.error,
                		   R.id.timestamp});  // Parallel array of which template objects to bind to those columns.

        // Bind to our new adapter.
        setListAdapter(adapter);
        
        SimpleCursorAdapter.ViewBinder binder = new SimpleCursorAdapter.ViewBinder() {

			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if (columnIndex == 1) {
					TextView textView = (TextView) view;
					textView.setText(new Date(cursor.getLong(1)).toLocaleString());
					return true;
				}
				return false;
			}
        	
        };

        adapter.setViewBinder(binder);
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
		Intent intent = new Intent(this, UpdateLogActivity.class);
        startActivity(intent);
	}
	
	private void activateSignup() {
		Uri uri = Uri.parse(getString(R.string.sign_up_url));
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
}
