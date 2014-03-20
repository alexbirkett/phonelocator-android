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
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		EnvironmentalSettingsSetter.setIMSIIfRequiredAndDetectSIMCardChanged(
				sharedPreferences, (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE));
		sendSMSIfRequired(context, sharedPreferences);
		
	}
      
	private void sendSMSIfRequired(Context context, SharedPreferences sharedPreferences) {		
		String buddyTelephoneNumber = SettingsHelper
				.getBuddyTelephoneNumber(sharedPreferences);
		String buddyMessage = SettingsHelper.getBuddyMessage(sharedPreferences);
		
		if (SettingsHelper.isBuddyMessageEnabled(sharedPreferences)
				&& SettingsHelper.isSendBuddyMessage(sharedPreferences) &&
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
