package com.birkettenterprise.phonelocator.controller;

import com.birkett.controllers.ViewController;
import com.birkettenterprise.phonelocator.R;

import android.content.Context;
import android.os.Bundle;

public class LocationStatusController extends ViewController {

	public LocationStatusController(Context context) {
		super(context);
	}

	@Override 
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.location_status_controller);
	}
}
