package com.birkettenterprise.phonelocator.controller;

import no.birkettconsulting.controllers.ViewController;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.activity.BuddyMessageActivity;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;
import com.birkettenterprise.phonelocator.utility.StringUtil;

public class BuddyMessageNotSetController extends ViewController{

	public BuddyMessageNotSetController(Context context) {
		super(context);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
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
	}
	
	/**
	 * Only display the buddy message not set controller if updates are enabled
	 * because we don't want to overload the user with info when they first
	 * start the app
	 * 
	 * @return
	 */
	private boolean displayController() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		
		return (SettingsHelper.isPeriodicUpdatesEnabled(sharedPreferences) && 
				(StringUtil.isNullOrWhiteSpace(SettingsHelper.getBuddyMessage(sharedPreferences)) || 
						StringUtil.isNullOrWhiteSpace(SettingsHelper.getBuddyTelephoneNumber(sharedPreferences))));
				
	}
	
	public void displayHideController() {
		if (displayController()) {
			getView().setVisibility(View.VISIBLE);
		} else {
			getView().setVisibility(View.GONE);
		}
	}

}
