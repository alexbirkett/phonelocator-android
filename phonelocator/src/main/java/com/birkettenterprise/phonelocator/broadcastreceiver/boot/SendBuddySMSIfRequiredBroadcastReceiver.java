package com.birkettenterprise.phonelocator.broadcastreceiver.boot;

import com.birkettenterprise.phonelocator.broadcastreceiver.sms.HandleBuddyMessageSentBroadcastReceiver;
import com.birkettenterprise.phonelocator.settings.EnvironmentalSettingsSetter;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;
import com.birkettenterprise.phonelocator.utility.StringUtil;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class SendBuddySMSIfRequiredBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		EnvironmentalSettingsSetter.setIMSIIfRequiredAndDetectSIMCardChanged((TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE));
		sendSMSIfRequired(context);
		
	}
      
	private void sendSMSIfRequired(Context context) {
		String buddyTelephoneNumber = SettingsHelper
				.getBuddyTelephoneNumber();
		String buddyMessage = SettingsHelper.getBuddyMessage();
		
		if (SettingsHelper.isBuddyMessageEnabled()
				&& SettingsHelper.isSendBuddyMessage() &&
				!StringUtil.isNullOrWhiteSpace(buddyTelephoneNumber)
				&& !StringUtil.isNullOrWhiteSpace(buddyMessage)) {
			sendSMS(context, buddyTelephoneNumber, buddyMessage);
		}
	}
	private void sendSMS(Context context, String buddyTelephoneNumber, String buddyMessage) {

		PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
				new Intent(context, HandleBuddyMessageSentBroadcastReceiver.class), 0);
		
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(buddyTelephoneNumber, null, buddyMessage, sentPI, null);
	}

}
