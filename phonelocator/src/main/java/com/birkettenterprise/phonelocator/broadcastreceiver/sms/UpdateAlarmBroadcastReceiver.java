package com.birkettenterprise.phonelocator.broadcastreceiver.sms;

import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.service.AudioAlarmService;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;
import com.birkettenterprise.phonelocator.utility.UpdateUtility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;


public class UpdateAlarmBroadcastReceiver extends BroadcastReceiver {

	
	public enum Action {
		None,
		Update,
		Alarm
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String sms = getMessageBody(intent);
		
		Action action = parseMessage(sms, context);
		
		if (action == Action.Update) {
			UpdateUtility.pollLocationAndSendUpdate(context);			
		} else if (action == Action.Alarm){
			AudioAlarmService.startAlarmService(context);		
		}
		
		if (action != Action.None && SettingsHelper.isHideTriggerMessage()) {
			abortBroadcast();
		}
	}
	
	private boolean passcodeNotRequiredOrCorrect(String message) {
		boolean passcodeNotRequiredOrCorrect = false;
		
		if (isPasscodeEnabled()) {
			String passcode = getPasscode();
			if (passcode == null) {
				passcodeNotRequiredOrCorrect = true;
			} else {
				if (message.trim().startsWith(passcode)) {
					passcodeNotRequiredOrCorrect = true;
				}
			}
		} else {
			passcodeNotRequiredOrCorrect = true;
		}

		return passcodeNotRequiredOrCorrect;
	}
	
	public Action parseMessage(String sms, Context context) {
		sms = sms.toLowerCase();
		Action action = Action.None;
		String smsTriggerMessageUpdate = context.getString(R.string.trigger_message_udpate);
		String smsTriggerMessageAlarm = context.getString(R.string.trigger_message_alarm);
		
		if (sms.startsWith(smsTriggerMessageUpdate)) {
			if (passcodeNotRequiredOrCorrect(sms.substring(smsTriggerMessageUpdate.length()))) {
				action = Action.Update;
			}
		} else if (sms.startsWith(smsTriggerMessageAlarm)) {
			if (passcodeNotRequiredOrCorrect(sms.substring(smsTriggerMessageAlarm.length()))) {
				action = Action.Alarm;
			}
		}
		return action;
	
	}
	
	private String getMessageBody(Intent intent) {
		String smsAsString = "";
		Bundle pudsBundle = intent.getExtras();
	    Object[] pdus = (Object[]) pudsBundle.get("pdus");
	    SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[0]);
	    String smsBody = message.getDisplayMessageBody();
	    if (smsBody != null) {
	    	smsAsString = smsBody;
	    }
	    return smsAsString;
	}
	
	private String getPasscode() {
		return SettingsHelper.getPasscode();
	}
	
	private boolean isPasscodeEnabled() {
		return SettingsHelper.isPasscodeEnabled();
	}

}

