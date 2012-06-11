package com.birkettenterprise.phonelocator.broadcastreceiver;

import com.birkettenterprise.phonelocator.service.UpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LocationBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		intent.setClass(context, com.birkettenterprise.phonelocator.service.UpdateService.class);
		UpdateService.sendWakefulWork(context, intent);
	}

}
