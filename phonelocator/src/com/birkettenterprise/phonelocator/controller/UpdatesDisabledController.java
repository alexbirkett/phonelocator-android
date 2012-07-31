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
import com.birkettenterprise.phonelocator.activity.SettingsActivity;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;

public class UpdatesDisabledController extends ViewController {

	
	public UpdatesDisabledController(Context context) {
		super(context);
	}

	@Override 
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.updates_disabled_controller);
		
		Button enableUpdatesButton = (Button) getView().findViewById(R.id.button_enable_updates);
		enableUpdatesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
				
				SettingsHelper.setPeriodicUpdatesEnabled(sharedPreferences, true);
			}
			
		});
		
		Button settingsButton = (Button) getView().findViewById(R.id.button_settings);
		settingsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mContext.startActivity(new Intent(mContext, SettingsActivity.class));
			}
			
		});
	}
	
}
