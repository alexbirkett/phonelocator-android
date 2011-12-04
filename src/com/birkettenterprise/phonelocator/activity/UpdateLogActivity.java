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

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class UpdateLogActivity extends ListActivity {
    
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
	protected void onDestroy() {
	    super.onDestroy();
	}

    @Override
    public void onResume() {
    	super.onResume();
    	mCursor.requery();
    	((SimpleCursorAdapter)getListAdapter()).notifyDataSetChanged();
    }
    
}
