package com.birkettenterprise.phonelocator.activity;

import android.os.Bundle;

import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.controller.PasscodeController;

import no.birkettconsulting.controllers.BaseControllerActivity;

public class BuddyMessageActivity extends BaseControllerActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		addController(new PasscodeController(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buddy_message_activity);
	}
}
