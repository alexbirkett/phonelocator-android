package com.birkettenterprise.phonelocator.broadcastreceiver;

import com.birkettenterprise.phonelocator.settings.Setting.BooleanSettings;
import com.birkettenterprise.phonelocator.settings.Setting.StringSettings;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

public class SMSBroadcastReceiverTest extends AndroidTestCase {

	public void testParseMessage() {
		SharedPreferences sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(getContext());
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(StringSettings.PASSCODE, "1234");
		editor.putBoolean(BooleanSettings.PASSCODE_ENABLED, true);
		editor.apply();
		
		SMSBroadcastReceiver broadcastReciver = new SMSBroadcastReceiver();
		broadcastReciver.setSharedPreferences(sharedPreferences);
		
		assertEquals(SMSBroadcastReceiver.Action.Update, broadcastReciver.parseMessage("update 1234", getContext()));
		assertEquals(SMSBroadcastReceiver.Action.Update, broadcastReciver.parseMessage("Update 1234", getContext()));
		assertEquals(SMSBroadcastReceiver.Action.Update, broadcastReciver.parseMessage("Update1234", getContext()));
		assertEquals(SMSBroadcastReceiver.Action.None, broadcastReciver.parseMessage("Update1235", getContext()));
		assertEquals(SMSBroadcastReceiver.Action.None, broadcastReciver.parseMessage("Update", getContext()));
		
		
		assertEquals(SMSBroadcastReceiver.Action.Alarm, broadcastReciver.parseMessage("alarm 1234", getContext()));
		assertEquals(SMSBroadcastReceiver.Action.Alarm, broadcastReciver.parseMessage("Alarm 1234", getContext()));
		assertEquals(SMSBroadcastReceiver.Action.Alarm, broadcastReciver.parseMessage("Alarm1234", getContext()));
		assertEquals(SMSBroadcastReceiver.Action.None, broadcastReciver.parseMessage("Alarm1235", getContext()));
		assertEquals(SMSBroadcastReceiver.Action.None, broadcastReciver.parseMessage("Alarm", getContext()));
		
		editor.putBoolean(BooleanSettings.PASSCODE_ENABLED, false);
		editor.apply();

		assertEquals(SMSBroadcastReceiver.Action.Update, broadcastReciver.parseMessage("Update", getContext()));
		assertEquals(SMSBroadcastReceiver.Action.Alarm, broadcastReciver.parseMessage("Alarm", getContext()));
		
		
	}
}
