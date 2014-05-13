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

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.birkett.controllers.ActivityThatSupportsControllers;
import com.birkett.controllers.ViewController;
import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.broadcastreceiver.PollLocationAndSendUpdateBroadcastReceiver;
import com.birkettenterprise.phonelocator.controller.BuddyMessageNotSetController;
import com.birkettenterprise.phonelocator.controller.CountdownController;
import com.birkettenterprise.phonelocator.controller.DatabaseController;
import com.birkettenterprise.phonelocator.controller.HockeyAppController;
import com.birkettenterprise.phonelocator.controller.LocationStatusController;
import com.birkettenterprise.phonelocator.controller.UpdateStatusController;
import com.birkettenterprise.phonelocator.controller.UpdatesDisabledController;
import com.birkettenterprise.phonelocator.service.AudioAlarmService;
import com.birkettenterprise.phonelocator.settings.Setting;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;
import com.birkettenterprise.phonelocator.utility.AsyncSharedPreferencesListener;

public class DashboardActivity extends ActivityThatSupportsControllers implements
        OnSharedPreferenceChangeListener {

    private CountdownController countdownController;
    private DatabaseController databaseController;
    private LocationStatusController locationStatusController;
    private UpdateStatusController updateStatusController;
    private UpdatesDisabledController updatesDisabledController;
    private BuddyMessageNotSetController buddyMessageNotSetController;

    private List<ActivityManager.RunningServiceInfo> runningServiceInfos;
    private AsyncSharedPreferencesListener asyncSharedPreferencesListener;


    private boolean phoneAdded;
    static final int START_ADD_PHONE_REQUEST = 1;  // The request code


    @Override
    public void onCreate(Bundle savedInstanceState) {

        countdownController = new CountdownController(this);
        databaseController = new DatabaseController(this);
        updateStatusController = new UpdateStatusController(this);
        locationStatusController = new LocationStatusController(this);
        updatesDisabledController = new UpdatesDisabledController(this);
        buddyMessageNotSetController = new BuddyMessageNotSetController(this);

		addController(new HockeyAppController(this));
        addController(countdownController);
        addController(databaseController);
        addController(locationStatusController);
        addController(updateStatusController);
        addController(updatesDisabledController);
        addController(buddyMessageNotSetController);
        super.onCreate(savedInstanceState);


        asyncSharedPreferencesListener = new AsyncSharedPreferencesListener();
        setContentView(R.layout.dashboard_activity);

        addBuddyNumberNotSetController();

    }

    private void addBuddyNumberNotSetController() {
        FrameLayout counterContainer = (FrameLayout) findViewById(R.id.buddy_message_not_set_container);
        counterContainer.addView(buddyMessageNotSetController.getView());
    }

    @Override
    public void onResume() {
        super.onResume();

        asyncSharedPreferencesListener.registerOnSharedPreferenceChangeListener(this);
        swapStatusController();
        registerBroadcastReceiver();

        if (SettingsHelper.hasAuthenticationToken()) {
            if (!phoneAdded) {
                startAddPhoneActivity();
            }
        } else {
            startAuthenticationActivity();
        }
        phoneAdded = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        asyncSharedPreferencesListener.unregisterOnSharedPreferenceChangeListener(this);
        unregisterBroadcastReceiver();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        int i = item.getItemId();
        if (i == R.id.web_site) {
            startWebSite();
            return true;
        } else if (i == R.id.settings) {
            startSettings();
            return true;
        } else if (i == R.id.update_log) {
            startUpdateLog();
            return true;
        } else if (i == R.id.test_alarm) {
            AudioAlarmService.startAlarmService(this);
            return true;
        } else if (i == R.id.buddy_message) {
            startBuddyMessageActivity();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(Setting.BooleanSettings.PERIODIC_UPDATES_ENABLED)) {
            swapStatusController();

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == START_ADD_PHONE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                phoneAdded = true;
            }
        }
    }

    private void startWebSite() {

        Intent viewIntent = new Intent("android.intent.action.VIEW",
                Uri.parse(getString(R.string.website_url)));

        startActivity(viewIntent);
    }

    private void startSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void startUpdateLog() {
        Intent intent = new Intent(this, UpdateLogActivity.class);
        startActivity(intent);
    }

    private void startBuddyMessageActivity() {
        Intent intent = new Intent(this, BuddyMessageActivity.class);
        startActivity(intent);
    }

    private void startAuthenticationActivity() {
        Intent intent = new Intent(this, AuthenticationActivity.class);
        // Use startActivityForResult to prevent flicker
        startActivityForResult(intent, 0);
        finish();
    }

    public void startAddPhoneActivity() {
        Intent intent = new Intent(this, AddPhoneActivity.class);
        startActivityForResult(intent, START_ADD_PHONE_REQUEST);
    }

    private void swapStatusController() {

        if (isUpdatesEnabled()) {
            refreshRunningServiceList();
            if (isUpdateServiceRunning()) {
                showStatusController(updateStatusController);
            } else if (isLocationPollerRunning()) {
                showStatusController(locationStatusController);
            } else {
                showCountdownController();
            }
        } else {
            showStatusController(updatesDisabledController);
        }

    }

    private boolean isUpdatesEnabled() {
        return SettingsHelper.isPeriodicUpdatesEnabled();
    }

    private void showCountdownController() {
        showStatusController(countdownController);
        countdownController.start();
    }

    private void showStatusController(ViewController controller) {
        detachStatusControllers();
        FrameLayout counterContainer = (FrameLayout) findViewById(R.id.status_container);
        counterContainer.addView(controller.getView());
    }

    private void detachStatusControllers() {
        countdownController.stop();
        countdownController.detachViewFromParent();
        updateStatusController.detachViewFromParent();
        locationStatusController.detachViewFromParent();
        updatesDisabledController.detachViewFromParent();
    }

    public long getLastUpdateTimestamp() {
        return databaseController.getLastUpdateTimestamp();
    }

    private void refreshRunningServiceList() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        runningServiceInfos = manager.getRunningServices(Integer.MAX_VALUE);
    }

    private boolean isLocationPollerRunning() {
        return isServiceRunning("com.commonsware.cwac.locpoll.LocationPollerService");
    }

    private boolean isUpdateServiceRunning() {
        return isServiceRunning("com.birkettenterprise.phonelocator.service.UpdateService");
    }

    private boolean isServiceRunning(String className) {
        for (RunningServiceInfo runningServiceInfo : runningServiceInfos) {
            if (className.equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(PollLocationAndSendUpdateBroadcastReceiver.ACTION);
        filter.addAction("com.birkettenterprise.phonelocator.UPDATE_COMPLETE");
        filter.addAction("com.birkettenterprise.phonelocator.SENDING_UPDATE");
        registerReceiver(mBroadcastReceiver, filter);
    }

    private void unregisterBroadcastReceiver() {
        unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (isUpdatesEnabled()) {
                if ("com.birkettenterprise.phonelocator.UPDATE_COMPLETE"
                        .equals(intent.getAction())) {
                    showCountdownController();
                } else if ("com.birkettenterprise.phonelocator.SENDING_UPDATE"
                        .equals(intent.getAction())) {
                    showStatusController(updateStatusController);
                } else if (PollLocationAndSendUpdateBroadcastReceiver.ACTION
                        .equals(intent.getAction())) {
                    showStatusController(locationStatusController);
                }
            }
        }

    };
}
