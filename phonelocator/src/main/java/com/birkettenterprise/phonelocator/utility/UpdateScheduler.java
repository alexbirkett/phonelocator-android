package com.birkettenterprise.phonelocator.utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.birkettenterprise.phonelocator.broadcastreceiver.PollLocationAndSendUpdateBroadcastReceiver;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;

import java.util.Date;

/**
 * Created by alex on 01/07/14.
 */
public class UpdateScheduler {

    public static final String TAG = "UpdateScheduler";

    public static void scheduleUpdates(Context context) {
        AlarmManager alamManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(PollLocationAndSendUpdateBroadcastReceiver.ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (SettingsHelper.isPeriodicUpdatesEnabled()) {
            long intervalInMicroseconds = SettingsHelper.getUpdateFrequencyInMilliSeconds();
            if (intervalInMicroseconds < 1) {
                throw new RuntimeException("update frequency in ms is " + intervalInMicroseconds);
            }

            long nextUpdateTimestamp = SettingsHelper.getLastUpdateStartedTimeStamp() + intervalInMicroseconds;
            alamManager.set(AlarmManager.RTC_WAKEUP, nextUpdateTimestamp, pendingIntent);
            Log.d(TAG, "next update scheduled for  " + new Date(nextUpdateTimestamp));
        } else {
            alamManager.cancel(pendingIntent);
            Log.d(TAG, "canceled updates");

        }
    }
}
