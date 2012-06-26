package com.birkettenterprise.phonelocator.broadcastreceiver;

import com.birkettenterprise.phonelocator.service.AudioAlarmService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StopAudioAlarmBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		AudioAlarmService.stopAlarmService(context);
	}

}
