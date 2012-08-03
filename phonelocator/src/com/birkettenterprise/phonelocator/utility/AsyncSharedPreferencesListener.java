package com.birkettenterprise.phonelocator.utility;


import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Handler;

/**
 * Workaround for problem on Android 2.1 where onSharedPreferenceChanged was not
 * always called from the main thread
 * 
 * Using AsyncSharedPreferencesListener one can guarantee that the onSharedPreferenceChanged callback is
 * called from the thread from which the class was instantiated from.
 * 
 */
public class AsyncSharedPreferencesListener implements OnSharedPreferenceChangeListener {

	private Handler mHandler;
	private SharedPreferences mSharedPreferences;
	private OnSharedPreferenceChangeListener mListener;
	
	public AsyncSharedPreferencesListener(SharedPreferences sharedPreferences) {
		mSharedPreferences = sharedPreferences;
		mHandler = new Handler();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
			mHandler.post(new PreferencesChangedRunnable(sharedPreferences, key));
	
	}
	
	private class PreferencesChangedRunnable implements Runnable {

		private SharedPreferences mSharedPreferences;
		private String mKey;
		
		public PreferencesChangedRunnable(SharedPreferences sharedPreferences, String key) {
			mSharedPreferences = sharedPreferences;
			mKey = key;
		}
		@Override
		public void run() {
			mListener.onSharedPreferenceChanged(mSharedPreferences, mKey);
		}
		
	}
	
	/**
     * Registers a callback to be invoked when a change happens to a preference.
     * 
     * @param listener The callback that will run.
     * @see #unregisterOnSharedPreferenceChangeListener
     */
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
    	mListener = listener;
    	mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }
    
    /**
     * Unregisters a previous callback.
     * 
     * @param listener The callback that should be unregistered.
     * @see #registerOnSharedPreferenceChangeListener
     */
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
    	mListener = null;
    	mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
	
}
