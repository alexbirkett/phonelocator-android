package com.birkettenterprise.phonelocator.controller;

import java.util.Calendar;
import java.util.TimeZone;

import com.birkett.controllers.ViewController;
import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;


public class CountdownController extends ViewController {
	
	private static final String LOG_TAG = "CountdownController";
	private boolean mBeating;
	
	private Handler mHandler;
	private TextView mTimerTextView;
	private SharedPreferences mSharedPreferences;
	
	private static final long BEAT_INTERVAL = 1000;
	
	public CountdownController(Context context) {
		super(context);
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
	}

	@Override 
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.countdown_controller);
		mTimerTextView = (TextView) getView().findViewById(R.id.timer_text_view);
		mHandler = new Handler();
	}
	
	@Override
	protected void onResume() {
		if (mBeating) {
			scheduleNextBeatDelayed();
		}
	}
	
	@Override
	protected void onPause() {
		if (mBeating) {
			cancelBeat();
		}
	}
	
	@Override
	protected void onDestroy() {
	}
	
	public void start() {	
		mBeating = true;
		scheduleNextBeatDelayed();
	}
	
	public void stop() {
		mBeating = false;
		cancelBeat();
	}
	
	private void scheduleNextBeatAfterBeatInterval() {
		mHandler.postDelayed(mBeatRunnable, BEAT_INTERVAL);
	}
	
	private void scheduleNextBeatDelayed() {
		mHandler.post(mBeatRunnable);
	}
	
	private void cancelBeat() {
		mHandler.removeCallbacks(mBeatRunnable);
	}
	
	private Runnable mBeatRunnable = new Runnable() {

		@Override
		public void run() {
			Log.d(LOG_TAG, "beat");
			updateClock();
			if (mBeating) {
				scheduleNextBeatAfterBeatInterval();
			}
		}
	};
	
	private void updateClock() {
		long timeRemaing = getCountDownTimerEndTime() - System.currentTimeMillis();
		String timeString = formatTime(timeRemaing);
		Log.d(LOG_TAG, "time string " + timeString);
		mTimerTextView.setText(timeString);
	}
	
	private String formatTime(long time) {
		
		boolean negative = false;
		if (time < 0) {
			time = -time;
			negative = true;
		}
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		calendar.setTimeInMillis(time);
		String timeString = String.format("%02d:%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
	
		if (negative) {
			timeString = "-" + timeString;
		}
		return timeString;
	}
	
	private long getCountDownTimerEndTime() {
		return (SettingsHelper.getUpdateFrequencyInMilliSeconds(mSharedPreferences)
				+ SettingsHelper.getLastUpdateTimeStamp(mSharedPreferences));
	}
}
