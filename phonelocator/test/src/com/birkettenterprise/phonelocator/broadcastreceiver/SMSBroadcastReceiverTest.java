package com.birkettenterprise.phonelocator.broadcastreceiver;

import com.birkettenterprise.phonelocator.broadcastreceiver.sms.UpdateAlarmBroadcastReceiver;
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
		editor.putBoolean(BooleanSettings.PINCODE_REQUIRED_ON_STARTUP, true);
		editor.commit();
		
		UpdateAlarmBroadcastReceiver broadcastReciver = new UpdateAlarmBroadcastReceiver();
		broadcastReciver.setSharedPreferences(sharedPreferences);
		
		assertEquals(UpdateAlarmBroadcastReceiver.Action.Update, broadcastReciver.parseMessage("update 1234", getContext()));
		assertEquals(UpdateAlarmBroadcastReceiver.Action.Update, broadcastReciver.parseMessage("Update 1234", getContext()));
		assertEquals(UpdateAlarmBroadcastReceiver.Action.Update, broadcastReciver.parseMessage("Update1234", getContext()));
		assertEquals(UpdateAlarmBroadcastReceiver.Action.None, broadcastReciver.parseMessage("Update1235", getContext()));
		assertEquals(UpdateAlarmBroadcastReceiver.Action.None, broadcastReciver.parseMessage("Update", getContext()));
		
		
		assertEquals(UpdateAlarmBroadcastReceiver.Action.Alarm, broadcastReciver.parseMessage("alarm 1234", getContext()));
		assertEquals(UpdateAlarmBroadcastReceiver.Action.Alarm, broadcastReciver.parseMessage("Alarm 1234", getContext()));
		assertEquals(UpdateAlarmBroadcastReceiver.Action.Alarm, broadcastReciver.parseMessage("Alarm1234", getContext()));
		assertEquals(UpdateAlarmBroadcastReceiver.Action.None, broadcastReciver.parseMessage("Alarm1235", getContext()));
		assertEquals(UpdateAlarmBroadcastReceiver.Action.None, broadcastReciver.parseMessage("Alarm", getContext()));
		
		editor.putBoolean(BooleanSettings.PINCODE_REQUIRED_ON_STARTUP, false);
		editor.apply();

		assertEquals(UpdateAlarmBroadcastReceiver.Action.Update, broadcastReciver.parseMessage("Update", getContext()));
		assertEquals(UpdateAlarmBroadcastReceiver.Action.Alarm, broadcastReciver.parseMessage("Alarm", getContext()));
		
		
	}
}
