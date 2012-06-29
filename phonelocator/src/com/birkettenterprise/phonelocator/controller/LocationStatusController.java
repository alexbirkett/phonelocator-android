package com.birkettenterprise.phonelocator.controller;

import com.birkettenterprise.phonelocator.R;

import android.content.Context;
import android.os.Bundle;
import no.birkettconsulting.controllers.ViewController;

public class LocationStatusController extends ViewController {

	public LocationStatusController(Context context) {
		super(context);
	}

	@Override 
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.location_status_controller);
	}
}
