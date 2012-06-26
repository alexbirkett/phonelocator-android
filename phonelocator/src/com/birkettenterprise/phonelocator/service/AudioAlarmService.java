package com.birkettenterprise.phonelocator.service;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.IBinder;
import android.util.Log;

public class AudioAlarmService extends Service {

	private static final String ALARM_ASSET_FILE_NAME = "alarm.mp3";
	
	private static final String LOG_TAG = "AudioAlarmService";

	
	private int mStreamId;
	private SoundPool mSoundPool;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		mSoundPool = new SoundPool (1, AudioManager.STREAM_ALARM, 0);
		
		AssetManager assetManager = getAssets();
		AssetFileDescriptor fd = null;
		try {
			fd = assetManager.openFd(ALARM_ASSET_FILE_NAME);
			Log.d(LOG_TAG, "opened asset file descriptor");
			mStreamId = mSoundPool.load(fd, 1);
			
			Log.d(LOG_TAG, "stream id is " + mStreamId);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				fd.close();
			} catch (IOException e) {
				// ignore
			}
		}
		
		Log.d(LOG_TAG, "onCreate()");
	}
	
	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "onDestroy()");
		super.onDestroy();
		stopAlarm();
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
		mSoundPool.play (mStreamId, 1.0F, 1.0F, 0, -1, 1.0F);
		Log.d(LOG_TAG, "playing stream " + mStreamId);
	}
	
	private void stopAlarm() {
		Log.d(LOG_TAG, "stopping stream " + mStreamId);
		mSoundPool.stop(mStreamId);
	}

}
