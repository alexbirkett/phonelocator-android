package com.birkettenterprise.phonelocator.service;

import android.content.Intent;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class BuddyMessageService extends WakefulIntentService {

	public BuddyMessageService() {
		super("BuddyMessageService");
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		
	}

}
