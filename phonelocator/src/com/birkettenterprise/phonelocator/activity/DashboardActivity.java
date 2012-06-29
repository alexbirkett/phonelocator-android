package com.birkettenterprise.phonelocator.activity;

import java.util.List;

import net.hockeyapp.android.HockeyAppController;
import no.birkettconsulting.controllers.ViewController;
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
import android.preference.PreferenceManager;
import android.widget.FrameLayout;

import com.actionbarsherlock.app.SherlockControllerActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.controller.CountdownController;
import com.birkettenterprise.phonelocator.controller.DatabaseController;
import com.birkettenterprise.phonelocator.controller.LocationStatusController;
import com.birkettenterprise.phonelocator.controller.UpdateStatusController;
import com.birkettenterprise.phonelocator.controller.UpdatesDisabledController;
import com.birkettenterprise.phonelocator.service.AudioAlarmService;
import com.birkettenterprise.phonelocator.settings.Setting;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;

public class DashboardActivity extends SherlockControllerActivity implements
		OnSharedPreferenceChangeListener {

	private static final String WEB_SITE_URL = "http://phonelocator.mobi";

	private CountdownController mCountdownController;
	private DatabaseController mDatabaseController;
	private SharedPreferences mSharedPreferences;
	private LocationStatusController mLocationStatusController;
	private UpdateStatusController mUpdateStatusController;
	private UpdatesDisabledController mUpdatesDisabledController;

	private List<ActivityManager.RunningServiceInfo> mRunningServices;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		mCountdownController = new CountdownController(this);
		mDatabaseController = new DatabaseController(this);
		mUpdateStatusController = new UpdateStatusController(this);
		mLocationStatusController = new LocationStatusController(this);
		mUpdatesDisabledController = new UpdatesDisabledController(this);

		addController(new HockeyAppController(this,
				"https://rink.hockeyapp.net/",
				"3f7ef8dc87d197b81fb86ff41dcc1314"));
		addController(mCountdownController);
		addController(mDatabaseController);
		addController(mLocationStatusController);
		addController(mUpdateStatusController);
		addController(mUpdatesDisabledController);

		super.onCreate(savedInstanceState);

		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		setContentView(R.layout.dashboard_activity);
	}

	@Override
	public void onResume() {
		super.onResume();
		setCountDownTimerEndTime();
		mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
		swapStatusController();
		registerBroadcastReceiver();
	}

	@Override
	public void onPause() {
		super.onPause();
		mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
		unregisterBroadcastReceiver();
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
		case R.id.update_log:
			startUpdateLog();
			return true;
		case R.id.test_alarm:
			AudioAlarmService.startAlarmService(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
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

	private void startWebSite() {
		Intent viewIntent = new Intent("android.intent.action.VIEW",
				Uri.parse(WEB_SITE_URL));
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

	private void swapStatusController() {

		if (SettingsHelper.periodicUpdatesEnabled(mSharedPreferences)) {
			refreshRunningServiceList();
			if (isUpdateServiceRunning()) {
				showController(mUpdateStatusController);
			} else if (isLocationPollerRunning()) {
				showController(mLocationStatusController);
			} else {
				showCountdownController();
			}
		} else {
			showController(mUpdatesDisabledController);
		}

	}

	private void showCountdownController() {
		showController(mCountdownController);
		mCountdownController.start();
		setCountDownTimerEndTime();
	}

	private void showController(ViewController controller) {
		detachStatusControllers();
		FrameLayout counterContainer = (FrameLayout) findViewById(R.id.status_container);
		detachStatusControllers();
		counterContainer.addView(controller.getView());
	}

	private void detachStatusControllers() {
		mCountdownController.stop();
		mCountdownController.detachViewFromParent();
		mUpdateStatusController.detachViewFromParent();
		mLocationStatusController.detachViewFromParent();
		mUpdatesDisabledController.detachViewFromParent();
	}

	private void setCountDownTimerEndTime() {
		mCountdownController.setEndtime(SettingsHelper
				.getUpdateIntervalInMilliSeconds(mSharedPreferences)
				+ mDatabaseController.getLastUpdateTimestamp());
	}

	public long getLastUpdateTimestamp() {
		return mDatabaseController.getLastUpdateTimestamp();
	}

	private void refreshRunningServiceList() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		mRunningServices = manager.getRunningServices(Integer.MAX_VALUE);
	}

	private boolean isLocationPollerRunning() {
		return isServiceRunning("com.commonsware.cwac.locpoll.LocationPollerService");
	}

	private boolean isUpdateServiceRunning() {
		return isServiceRunning("com.birkettenterprise.phonelocator.service.UpdateService");
	}

	private boolean isServiceRunning(String className) {
		for (RunningServiceInfo runningServiceInfo : mRunningServices) {
			if (className.equals(runningServiceInfo.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	private void registerBroadcastReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.birkettenterprise.phonelocator.UPDATE");
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
			if ("com.birkettenterprise.phonelocator.UPDATE_COMPLETE"
					.equals(intent.getAction())) {
				showCountdownController();
			} else if ("com.birkettenterprise.phonelocator.SENDING_UPDATE"
					.equals(intent.getAction())) {
				showController(mUpdateStatusController);
			} else if ("com.birkettenterprise.phonelocator.UPDATE"
					.equals(intent.getAction())) {
				showController(mLocationStatusController);
			}
		}

	};

}
