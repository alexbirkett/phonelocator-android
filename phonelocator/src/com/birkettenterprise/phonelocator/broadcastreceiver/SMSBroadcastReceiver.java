package com.birkettenterprise.phonelocator.broadcastreceiver;

import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.service.AudioAlarmService;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;
import com.birkettenterprise.phonelocator.utility.UpdateUtility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;


public class SMSBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SmsMessage smsMessage = getMessageBody(intent);
		String smsBody = smsMessage.getDisplayMessageBody();
		boolean messageMatch = false;
		if (smsBody != null) {
			if (smsBody.toLowerCase().contains(context.getString(R.string.trigger_message_udpate))) {
				UpdateUtility.update(context);
				messageMatch = true;
			} else if (smsBody.toLowerCase().contains(context.getString(R.string.trigger_message_alarm))) {
				AudioAlarmService.startAlarmService(context);
				messageMatch = true;
			}
		}
		
		if (messageMatch && hiddeTriggerMessages(context)) {
			abortBroadcast();
		}
	}
	
	private SmsMessage getMessageBody(Intent intent) {
		Bundle pudsBundle = intent.getExtras();
	    Object[] pdus = (Object[]) pudsBundle.get("pdus");
	    SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[0]);
	    return message;
	}
	
	private static boolean hiddeTriggerMessages(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return SettingsHelper.isHideTriggerMessage(sharedPreferences);

	}
}

