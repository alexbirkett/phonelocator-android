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

import java.util.Date;

import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.database.UpdateLogDatabaseContentProvider;
import com.birkettenterprise.phonelocator.utility.StringUtil;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.view.ViewGroup;

public class UpdateLogActivity extends ListActivity implements LoaderCallbacks<Cursor>{

    private class ErrorNotResolvedException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    //private static int ID_COLUMN_INDEX = 0;
    private static int UPDATE_TIMESTAMP_COLUMN_INDEX = 1;
    private static int ERROR_TYPE_COLUMN_INDEX = 2;
    private static int ERROR_MESSAGE_COLUMN_INDEX = 3;
    private static int LOCATION_TIMESTAMP_COLUMN_INDEX = 4;
    private static int PROVIDER_COLUMN_INDEX = 5;

    private ResourceCursorAdapter resourceCursorAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // add controllers before you call super.onCreate()
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_log_activity);

        resourceCursorAdapter = new ResourceCursorAdapter(UpdateLogActivity.this, R.layout.update_log_list_item, null, true) {
            @Override
            public View newView(Context context, Cursor cur, ViewGroup parent) {
                LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                return li.inflate(R.layout.update_log_list_item, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                String errorType = cursor.getString(ERROR_TYPE_COLUMN_INDEX);

                TextView updateTimestampView = (TextView) view.findViewById(R.id.update_log_update_timestamp);
                setTimeStamp(updateTimestampView,cursor,UPDATE_TIMESTAMP_COLUMN_INDEX);

                TextView errorView = (TextView) view.findViewById(R.id.update_log_error);

                if (errorType == null) {
                    TextView locationProviderTextView = (TextView) view.findViewById(R.id.update_log_location_provider);
                    locationProviderTextView.setText(cursor.getString(PROVIDER_COLUMN_INDEX));
                    errorView.setVisibility(View.GONE);
                    view.findViewById(R.id.update_log_location_provider_layout).setVisibility(View.VISIBLE);
                } else {
                    errorView.setVisibility(View.VISIBLE);
                    setError(errorView,cursor, errorType);
                    view.findViewById(R.id.update_log_location_provider_layout).setVisibility(View.GONE);
                }
            }
        };

        getLoaderManager().initLoader(0, null, this);

        setListAdapter(resourceCursorAdapter);

    }

    private void setTimeStamp(TextView view, Cursor cursor, int columnIndex) {
        view.setText(new Date(cursor.getLong(columnIndex)).toLocaleString());
    }

    private void setError(TextView view, Cursor cursor, String errorType) {
        try {
            String resolvedError = resolveErrorType(errorType);
            view.setText(resolvedError);
        } catch (ErrorNotResolvedException e) {
            String errorMessage = cursor.getString(ERROR_MESSAGE_COLUMN_INDEX);
            if (StringUtil.isNullOrWhiteSpace(errorMessage)) {
                errorMessage = errorType;
            }
            view.setText(errorMessage);
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
        }  else if (errorType.indexOf("ConnectException") > 0) {
            return getString(R.string.error_connect_exception);
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
        ((ResourceCursorAdapter)getListAdapter()).notifyDataSetChanged();
    }


    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, UpdateLogDatabaseContentProvider.URI, null, null, null, null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int count = data.getCount();
        resourceCursorAdapter.changeCursor(data);

    }

    public void onLoaderReset(Loader<Cursor> loader) {
        resourceCursorAdapter.changeCursor(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.update_log_menu_item_clear_log:
                clearLog();
                return true;
            case R.id.update_log_menu_item_web_site:
                startWebSite();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearLog() {
        ContentResolver cr = getContentResolver();
        cr.delete(UpdateLogDatabaseContentProvider.URI, null, null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.update_log_menu, menu);
        return true;
    }

    private void startWebSite() {

        Intent viewIntent = new Intent("android.intent.action.VIEW",
                Uri.parse(getString(R.string.website_url)));

        startActivity(viewIntent);
    }
}
