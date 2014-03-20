package com.birkettenterprise.phonelocator.controller;

import no.birkettconsulting.controllers.Controller;
import android.content.Context;
import android.os.Bundle;

import com.birkettenterprise.phonelocator.database.UpdateLogDatabase;

public class DatabaseController extends Controller {
	
	private UpdateLogDatabase mDatabase;
	
	public DatabaseController(Context context) {
		super(context);
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		mDatabase = new UpdateLogDatabase(mContext);
	}
	
	@Override
    public void onDestroy() {
		mDatabase.close();
	}
	
	public long getLastUpdateTimestamp() {
		return mDatabase.getLastUpdateTimestamp();
	}

}
