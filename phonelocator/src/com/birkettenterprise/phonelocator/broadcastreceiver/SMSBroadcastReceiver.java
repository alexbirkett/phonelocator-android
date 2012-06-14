package com.birkettenterprise.phonelocator.broadcastreceiver;

import com.birkettenterprise.phonelocator.utility.UpdateUtility;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;


public class SMSBroadcastReceiver extends BroadcastReceiver {

	private static final String UPDATE = "update";

	@Override
	public void onReceive(Context context, Intent intent) {
		SmsMessage smsMessage = getMessageBody(intent);
		String smsBody = smsMessage.getDisplayMessageBody();
		if (smsBody != null) {
			if (smsBody.toLowerCase().contains(UPDATE)) {
				UpdateUtility.update(context);
			}
		}
	}
	
	private SmsMessage getMessageBody(Intent intent) {
		Bundle pudsBundle = intent.getExtras();
	    Object[] pdus = (Object[]) pudsBundle.get("pdus");
	    SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[0]);
	    return message;
	}
}

