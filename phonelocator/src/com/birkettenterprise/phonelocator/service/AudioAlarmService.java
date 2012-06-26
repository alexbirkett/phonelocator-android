package com.birkettenterprise.phonelocator.service;

import com.birkettenterprise.phonelocator.R;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.IBinder;
import android.util.Log;

public class AudioAlarmService extends Service {
	
	private static final String LOG_TAG = "AudioAlarmService";
	
	private int mStreamId;
	private SoundPool mSoundPool;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		mSoundPool = new SoundPool (1, AudioManager.STREAM_ALARM, 0);
		
		mStreamId = mSoundPool.load(this, R.raw.alarm, 1);
		
		Log.d(LOG_TAG, "onCreate()");
	}
	
	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "onDestroy()");
		super.onDestroy();
		stopAlarm();
		releaseSoundPool();
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(LOG_TAG, "onStartCommand()");
		startAlarm();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private void startAlarm() {
		Log.d(LOG_TAG, "startAlarm()");
		mSoundPool.play(mStreamId, 1.0F, 1.0F, 0, -1, 1.0F);
		Log.d(LOG_TAG, "playing stream " + mStreamId);
	}
	
	private void stopAlarm() {
		Log.d(LOG_TAG, "stopping stream " + mStreamId);
		mSoundPool.stop(mStreamId);
	}
	
	private void releaseSoundPool() {
		mSoundPool.release();
		mSoundPool = null;
		mStreamId = 0;
	}

}
