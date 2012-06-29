package com.birkettenterprise.phonelocator.controller;

import no.birkettconsulting.controllers.ViewController;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.activity.SettingsActivity;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;
import com.birkettenterprise.phonelocator.settings.SettingsManager;

public class UpdatesDisabledController extends ViewController {

	private SettingsManager mSettingsManager;
	
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
				SettingsHelper.setPeriodicUpdatesEnabled(mSettingsManager.getSharedPreferences(), true);
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
	
	protected void onResume() {
		mSettingsManager = SettingsManager.getInstance(this, mContext);
	}
	
	protected void onPause() {
		mSettingsManager.releaseInstance(this);
		mSettingsManager = null;
	}
	
}
