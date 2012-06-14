/**
 * 
 *  Copyright 2011, 2012 Birkett Enterprise Ltd
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

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.database.UpdateLogDatabase;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.view.ViewGroup;

public class UpdateLogActivity extends SherlockListActivity {
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.update_log_activity_menu, menu);
	    return true;
	}
	
	private class ErrorNotResolvedException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	private Cursor mCursor;
	
	//private static int ID_COLUMN_INDEX = 0;
	private static int UPDATE_TIMESTAMP_COLUMN_INDEX = 1;
	private static int ERROR_TYPE_COLUMN_INDEX = 2;
	private static int ERROR_MESSAGE_COLUMN_INDEX = 3;
	private static int LOCATION_TIMESTAMP_COLUMN_INDEX = 4;
	private static int PROVIDER_COLUMN_INDEX = 5;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.update_log);
        mCursor = new UpdateLogDatabase(this).getUpdateTable();
          
        startManagingCursor(mCursor);

        
        ResourceCursorAdapter resourceCursorAdapter = new ResourceCursorAdapter(UpdateLogActivity.this, R.layout.update_log_list_item, mCursor) {

     
            @Override
            public View newView(Context context, Cursor cur, ViewGroup parent) {
                LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                return li.inflate(R.layout.update_log_list_item, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
              String error = cursor.getString(ERROR_TYPE_COLUMN_INDEX);
     
              TextView updateTimestampView = (TextView) view.findViewById(R.id.update_timestamp);
              setTimeStamp(updateTimestampView,cursor,UPDATE_TIMESTAMP_COLUMN_INDEX);
              
              View errorRow = view.findViewById(R.id.error_row);
              View locationProviderRow = view.findViewById(R.id.location_provider_row);
              View locationTimestampRow = view.findViewById(R.id.location_timestamp_row);
              if (error == null) {
            	  errorRow.setVisibility(View.GONE); 
            	  locationProviderRow.setVisibility(View.VISIBLE);
            	  locationTimestampRow.setVisibility(View.VISIBLE);
            	  
            	  setTimeStamp((TextView) view.findViewById(R.id.location_timestamp), cursor, LOCATION_TIMESTAMP_COLUMN_INDEX);
            	  TextView locationProviderTextView = (TextView) view.findViewById(R.id.location_provider);
            	  locationProviderTextView.setText(cursor.getString(PROVIDER_COLUMN_INDEX));
                  
              } else {
            	  errorRow.setVisibility(View.VISIBLE);
            	  locationProviderRow.setVisibility(View.GONE);
            	  locationTimestampRow.setVisibility(View.GONE);
            	  
            	  TextView errorView = (TextView) view.findViewById(R.id.error);
            	  setError(errorView, cursor);
              }
            }
        };
       
        setListAdapter(resourceCursorAdapter);
        
    }
    
	private void setTimeStamp(TextView view, Cursor cursor, int columnIndex) {
		view.setText(new Date(cursor.getLong(columnIndex)).toLocaleString());

	}
	
	private void setError(TextView view, Cursor cursor) {
		String errorType = cursor.getString(ERROR_TYPE_COLUMN_INDEX);
		if (errorType == null) {
			view.setVisibility(View.GONE);
		} else {
			try {
				String resolvedError = resolveErrorType(errorType);
				view.setText(resolvedError);
			} catch (ErrorNotResolvedException e) {
				view.setText(cursor.getString(ERROR_MESSAGE_COLUMN_INDEX));	
			}
		}
	}
	
	private String resolveErrorType(String errorType) throws ErrorNotResolvedException {
		if (errorType.indexOf("IOException") > 0) {
			return getString(R.string.error_io_exception);
		} else if (errorType.indexOf("CorruptStreamException") > 0) {
			return getString(R.string.error_corrupt_stream);
		} else if (errorType.indexOf("AuthenticationFailedException") > 0) {
			return getString(R.string.error_authentication_failed);
		} else if (errorType.indexOf("LocationPollFailedException") > 0) {
			return getString(R.string.error_location_poll_failed);
		}  
		
		throw new ErrorNotResolvedException();
		
	}
    
    @Override
	protected void onDestroy() {
	    super.onDestroy();
	}
    
    @Override
    public void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
    }

    @Override
    public void onResume() {
    	super.onResume();
    	mCursor.requery();
    	((ResourceCursorAdapter)getListAdapter()).notifyDataSetChanged();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	// Handle item selection
        switch (item.getItemId()) {
        case R.id.web_site:
        	startWebSite();
        	return true;
        
        case R.id.settings:
        	startSettings();
        	return true;
        	
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private static final String WEB_SITE_URL = "http://phonelocator.mobi";
    void startWebSite() {
    	Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(WEB_SITE_URL));
    	startActivity(viewIntent);
    }
    
    void startSettings() {
    	Intent intent = new Intent(this, SettingsActivity.class);
    	startActivity(intent);
    }
}
