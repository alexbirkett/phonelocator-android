/**
 *
 *  Copyright 2011-2014 Birkett Enterprise Ltd
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */


package com.birkettenterprise.phonelocator.controller;

import java.util.Calendar;
import java.util.TimeZone;

import com.birkett.controllers.ViewController;
import com.birkettenterprise.phonelocator.R;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class CountdownController extends ViewController {
	
	private static final String LOG_TAG = "CountdownController";
	private boolean beating;
	
	private Handler handler;
	private TextView timerTextView;
    private View nextUpdateLabel;
    private View progressBar;
    private View startingShortlyLabel;

	private static final long BEAT_INTERVAL = 1000;
	
	public CountdownController(Context context) {
		super(context);
	}

	@Override 
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.countdown_controller);
		timerTextView = (TextView) getView().findViewById(R.id.countdown_controller_timer_text_view);
        nextUpdateLabel = getView().findViewById(R.id.countdown_controller_next_update_label);
        progressBar = getView().findViewById(R.id.countdown_controller_progress_bar);
        startingShortlyLabel = getView().findViewById(R.id.countdown_controller_starting_shortly_label);


		handler = new Handler();
	}
	
	@Override
	protected void onResume() {
		if (beating) {
			scheduleNextBeatDelayed();
		}
	}
	
	@Override
	protected void onPause() {
		if (beating) {
			cancelBeat();
		}
	}
	
	@Override
	protected void onDestroy() {
	}
	
	public void start() {	
		beating = true;
		scheduleNextBeatDelayed();
	}
	
	public void stop() {
		beating = false;
		cancelBeat();
	}
	
	private void scheduleNextBeatAfterBeatInterval() {
		handler.postDelayed(mBeatRunnable, BEAT_INTERVAL);
	}
	
	private void scheduleNextBeatDelayed() {
		handler.post(mBeatRunnable);
	}
	
	private void cancelBeat() {
		handler.removeCallbacks(mBeatRunnable);
	}
	
	private Runnable mBeatRunnable = new Runnable() {

		@Override
		public void run() {
			Log.d(LOG_TAG, "beat");
			updateClock();
            showHideViews();
			if (beating) {
				scheduleNextBeatAfterBeatInterval();
			}
		}
	};
	
	private void updateClock() {
        timerTextView.setText(formatTime(getTimeRemaining()));
	}

    private void showHideViews() {
        boolean showCountdown = showCountdown();
        nextUpdateLabel.setVisibility(showCountdown ? View.VISIBLE : View.GONE);
        timerTextView.setVisibility(showCountdown ? View.VISIBLE : View.GONE);
        progressBar.setVisibility(showCountdown ? View.GONE : View.VISIBLE);
        startingShortlyLabel.setVisibility(showCountdown ? View.GONE : View.VISIBLE);
    }

    private boolean showCountdown() {
        return getTimeRemaining() > 0;
    }

    private long getTimeRemaining() {
        return getCountDownTimerEndTime() - System.currentTimeMillis();
    }
	
	private String formatTime(long time) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		calendar.setTimeInMillis(time);
		String timeString = String.format("%02d:%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
		return timeString;
	}
	
	private long getCountDownTimerEndTime() {
		return (SettingsHelper.getUpdateFrequencyInMilliSeconds()
				+ SettingsHelper.getLastUpdateStartedTimeStamp());
	}
}
