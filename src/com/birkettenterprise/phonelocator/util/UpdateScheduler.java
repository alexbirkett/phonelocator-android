package com.birkettenterprise.phonelocator.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.broadcastreceiver.AlarmBroadcastReceiver;
import com.birkettenterprise.phonelocator.broadcastreceiver.LocationPollerBroadcastReceiver;

public class UpdateScheduler {
	
	private static final int MILLISECONDS_IN_SECOND = 1000;
	private static final String DEFAULT_UPDATE_FREQUENCY = "3600";

	private static final String TAG = "Phonelocator";

	private static long getUpdateIntervalInMicroSeconds(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return Long.parseLong(sharedPreferences.getString(
				context.getString(R.string.update_frequency_key),
				DEFAULT_UPDATE_FREQUENCY)) * MILLISECONDS_IN_SECOND;
	}
	
	private static boolean periodicUpdatesEnabled(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager
		.getDefaultSharedPreferences(context);
		return sharedPreferences.getBoolean(context.getString(R.string.periodic_updates_key), false);
	}

	
	public static void scheduleUpdates(Context context) {
		AlarmManager alamManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
				
		Intent intent = new Intent(context, LocationPollerBroadcastReceiver.class);
		
		intent.putExtra(LocationPollerBroadcastReceiver.EXTRA_INTENT,
							 new Intent(context, AlarmBroadcastReceiver.class));
		intent.putExtra(LocationPollerBroadcastReceiver.EXTRA_PROVIDER,
							 LocationManager.GPS_PROVIDER);
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		if (periodicUpdatesEnabled(context)) {
			long intervalInMicroseconds = getUpdateIntervalInMicroSeconds(context);
			alamManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime(),
					intervalInMicroseconds, pendingIntent);
			Log.d(TAG, "updates scheduled every " + intervalInMicroseconds + " microseconds");
		} else {
			alamManager.cancel(pendingIntent);
			Log.d(TAG, "canceled updates");

		}
	}

}
