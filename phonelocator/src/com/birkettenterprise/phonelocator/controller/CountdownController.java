package com.birkettenterprise.phonelocator.controller;

import java.util.Calendar;
import java.util.TimeZone;

import com.birkettenterprise.phonelocator.R;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import no.birkettconsulting.controllers.ViewController;

public class CountdownController extends ViewController {

	private long mEndTimeInMillis;
	private boolean mRunning;
	
	private Handler mHandler;
	private TextView mTimerTextView;
	
	private static final long BEAT_INTERVAL = 1000;
	
	public CountdownController(Context context) {
		super(context);
	
	}

	@Override 
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.countdown_controller);
		mTimerTextView = (TextView) getView().findViewById(R.id.timer_text_view);
		mHandler = new Handler();
	}
	
	@Override
	protected void onResume() {
		if (mRunning) {
			startBeating();
		}
	}
	
	@Override
	protected void onPause() {
		if (mRunning) {
			stopBeating();
		}
	}
	
	@Override
	protected void onDestroy() {
	}
	
	
	public void setEndtime(long endTimeInMillis) {
		mEndTimeInMillis = endTimeInMillis;
	}
	
	public void start() {	
		mRunning = true;
		startBeating();
	}
	
	public void stop() {
		mRunning = false;
		stopBeating();
	}
	
	private void startBeating() {
		mHandler.postDelayed(mBeatRunnable, BEAT_INTERVAL);
	}
	
	private void stopBeating() {
		mHandler.removeCallbacks(mBeatRunnable);
	}
	
	private Runnable mBeatRunnable = new Runnable() {

		@Override
		public void run() {
			
			updateClock();
			if (mRunning) {
				startBeating();
			}
		}
	};
	
	
	private void updateClock() {
		long timeRemaing = mEndTimeInMillis - System.currentTimeMillis();
		mTimerTextView.setText(formatTime(timeRemaing));
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
	
}
