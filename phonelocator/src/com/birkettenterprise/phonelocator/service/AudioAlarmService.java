package com.birkettenterprise.phonelocator.service;

import java.io.IOException;

import com.birkettenterprise.phonelocator.R;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class AudioAlarmService extends Service {
	
	private static final String LOG_TAG = "AudioAlarmService";
	
	private int mStreamId;
	private MediaPlayer mMediaPlayer;
	
	
	public static void startAlarmService(Context context) {
		Intent intent = new Intent(context, AudioAlarmService.class);
		context.startService(intent);
	}

	public static void stopAlarmService(Context context) {
		Intent intent = new Intent(context, AudioAlarmService.class);
    	context.stopService(intent);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mMediaPlayer = createAndConfigureMediaPlayer(this, R.raw.alarm);

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
		mMediaPlayer.start();
		Log.d(LOG_TAG, "playing stream " + mStreamId);
	}
	
	private void stopAlarm() {
		Log.d(LOG_TAG, "stopping stream " + mStreamId);
		mMediaPlayer.stop();
	}
	
	private void releaseSoundPool() {
		mMediaPlayer.release();
	}

	/*
	 * Create and configure a media player. Based on public static
	 * MediaPlayer create(Context context, int resid) in the Android framework.
	 * MediaPlayer.create can not be used because it calls prepare() before we
	 * get opportunity to call setAudioStreamType()
	 */
	private static MediaPlayer createAndConfigureMediaPlayer(Context context, int resid) {
		try {
			AssetFileDescriptor assetFileDescriptor = context.getResources().openRawResourceFd(resid);
			if (assetFileDescriptor == null) {
				throw new RuntimeException("missing audio file");
			}

			MediaPlayer mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
			assetFileDescriptor.close();
			
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
			mediaPlayer.setLooping(true);
			
			mediaPlayer.prepare();
			return mediaPlayer;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalArgumentException ex) {
			throw new RuntimeException(ex);
		} catch (SecurityException ex) {
			throw new RuntimeException(ex);
		}
	    
	}
}
