package com.birkettenterprise.phonelocator.broadcastreceiver.sms;

import com.birkettenterprise.phonelocator.settings.SettingsHelper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class HandleBuddyMessageSentBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (getResultCode() == Activity.RESULT_OK) {
			SettingsHelper.setSendBuddyMessage(false);
		}
	
	}

}
