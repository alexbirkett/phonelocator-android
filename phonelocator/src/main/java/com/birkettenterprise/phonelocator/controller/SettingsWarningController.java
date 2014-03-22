package com.birkettenterprise.phonelocator.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.birkett.controllers.Controller;
import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.settings.Setting;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;
import com.birkettenterprise.phonelocator.utility.AsyncSharedPreferencesListener;

/**
 * Warns the user using toasts when about potentially undesirable settings
 * @author alex
 *
 */
public class SettingsWarningController extends Controller implements OnSharedPreferenceChangeListener{

	private AsyncSharedPreferencesListener mAsyncSharedPreferencesListener;
	private static final long MINIMUM_RECOMENDED_UPDATE_FREQUENCY = 10 * 60 * 1000;
	private Context mContext;
	public SettingsWarningController(Context context) {
		super();
        this.mContext = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		mAsyncSharedPreferencesListener = new AsyncSharedPreferencesListener(PreferenceManager.getDefaultSharedPreferences(mContext));
	}

	@Override
	public void onResume() {
		mAsyncSharedPreferencesListener.registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onPause() {
		mAsyncSharedPreferencesListener.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (Setting.StringSettings.BUDDY_TELEPHONE_NUMBER.equals(key)) {
			if (!SettingsHelper.getBuddyTelephoneNumber(sharedPreferences).startsWith("+")) {
				showToast(R.string.settings_warning_controller_non_international_format_buddy_number_warning);
			}
		} else if (Setting.StringSettings.UPDATE_FREQUENCY.equals(key)) {
			long updateFrequency = SettingsHelper.getUpdateFrequencyInMilliSeconds(sharedPreferences);
			if (updateFrequency < MINIMUM_RECOMENDED_UPDATE_FREQUENCY) {
				showToast(R.string.settings_warning_controller_frequent_update_warning);
			}
		}
	}
	
	private void showToast(int resourceId) {
		Toast.makeText(mContext, resourceId, Toast.LENGTH_LONG).show();
	}
	
	

}
