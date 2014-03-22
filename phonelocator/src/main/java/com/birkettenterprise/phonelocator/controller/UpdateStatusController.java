package com.birkettenterprise.phonelocator.controller;

import android.content.Context;
import android.os.Bundle;

import com.birkett.controllers.ViewController;
import com.birkettenterprise.phonelocator.R;

public class UpdateStatusController extends ViewController {

	public UpdateStatusController(Context context) {
		super(context);
	}

	@Override 
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.update_status_controller);
	}
}