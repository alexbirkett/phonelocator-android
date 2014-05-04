package com.birkettenterprise.phonelocator.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.birkett.controllers.ViewController;
import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.activity.BuddyMessageActivity;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;
import com.birkettenterprise.phonelocator.utility.AsyncSharedPreferencesListener;
import com.birkettenterprise.phonelocator.utility.StringUtil;

public class BuddyMessageNotSetController extends ViewController implements OnSharedPreferenceChangeListener{

	private AsyncSharedPreferencesListener mAsyncSharedPreferencesListener;
	
	public BuddyMessageNotSetController(Context context) {
		super(context);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		mAsyncSharedPreferencesListener = new AsyncSharedPreferencesListener();
		
		setContentView(R.layout.buddy_message_not_set_controller);


		Button button = (Button) getView().findViewById(
				R.id.buddy_message_not_set_controller_buddy_settings);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mContext.startActivity(new Intent(mContext,
						BuddyMessageActivity.class));
			}

		});
	}

	@Override
	public void onResume() {
		displayHideController();
		mAsyncSharedPreferencesListener.registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onPause() {
		mAsyncSharedPreferencesListener.unregisterOnSharedPreferenceChangeListener(this);
	}
	
	/**
	 * Only display the buddy message not set controller if updates are enabled
	 * because we don't want to overload the user with info when they first
	 * start the app
	 * 
	 * @return
	 */
	private boolean displayController() {

		return (SettingsHelper.isPeriodicUpdatesEnabled() &&
				(StringUtil.isNullOrWhiteSpace(SettingsHelper.getBuddyMessage()) ||
						StringUtil.isNullOrWhiteSpace(SettingsHelper.getBuddyTelephoneNumber())));
				
	}
	
	public void displayHideController() {
		if (displayController()) {
			getView().setVisibility(View.VISIBLE);
		} else {
			getView().setVisibility(View.GONE);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		displayHideController();
	}

}
