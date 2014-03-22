package com.birkettenterprise.phonelocator.controller;

import android.content.Context;
import android.os.Bundle;

import com.birkett.controllers.Controller;
import com.birkettenterprise.phonelocator.database.UpdateLogDatabase;

public class DatabaseController extends Controller {
	
	private UpdateLogDatabase mDatabase;
    private Context mContext;
	
	public DatabaseController(Context context) {
		this.mContext = context;
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
