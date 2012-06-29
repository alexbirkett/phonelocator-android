package com.birkettenterprise.phonelocator.controller;

import no.birkettconsulting.controllers.ViewController;
import android.content.Context;
import android.os.Bundle;

import com.birkettenterprise.phonelocator.R;

public class UpdatesDisabledController extends ViewController {

	public UpdatesDisabledController(Context context) {
		super(context);
	}

	@Override 
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.updates_disabled_controller);
	}
}
