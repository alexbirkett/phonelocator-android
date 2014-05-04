/**
 * 
 *  Copyright 2011-2012 Birkett Enterprise Ltd
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

package com.birkettenterprise.phonelocator.service;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import com.birkettenterprise.phonelocator.protocol.RegistrationResponse;
import com.birkettenterprise.phonelocator.protocol.Session;
import com.birkettenterprise.phonelocator.settings.DefaultSettingsSetter;
import com.birkettenterprise.phonelocator.settings.EnvironmentalSettingsSetter;
import com.birkettenterprise.phonelocator.settings.Setting;
import com.birkettenterprise.phonelocator.settings.SettingSynchronizationHelper;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class RegistrationService extends Service {

    private final IBinder mBinder = new RegistrationServiceBinder();
    
    private Handler handler;
    private Session session;
    private Throwable exception;
    private Vector<Runnable> observers;
    private RegistrationResponse registrationResponse;
    
    private RegisrationRunnable regisrationRunnable;
    private SynchronizeRunnable synchronizeRunnable;
    private Thread workerThread;
    
    private static final String LOG_TAG = "REGISTATION_SERVICE";
    
    private static void synchronizeSettings(Session session) throws IOException {
		Vector<Setting> settings = session.synchronizeSettings(SettingSynchronizationHelper.getSettingsModifiedSinceLastSyncrhonization());
		SettingSynchronizationHelper.setSettings(settings);
		SettingSynchronizationHelper.updateSettingsSynchronizationTimestamp();
    }
    
    private class SynchronizeRunnable implements Runnable {

		public void run() {
		
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RegistrationService.this);
			
			try {
				session.connect();
				session.authenticate(SettingsHelper.getAuthenticationToken());
				synchronizeSettings(session);
			} catch (Throwable e) {
				exception = e;
			} finally {
				session.close();
			}
			
			synchronized(this) {
				workerThread = null;
			}
			
			updateObservers();
		}
    	
    }
    
    private class RegisrationRunnable implements Runnable {

		public void run() {
			try {

				DefaultSettingsSetter.setDefaultSettings(RegistrationService.this);
				EnvironmentalSettingsSetter.updateEnvironmentalSettingsIfRequired(RegistrationService.this);
				SettingSynchronizationHelper.resetSettingsSynchronizationTimestamp();
				session.connect();
				registrationResponse = session.register();
				session.authenticate(registrationResponse.getAuthenticationToken());
				SettingsHelper.storeResponse(registrationResponse.getAuthenticationToken(), registrationResponse.getRegistrationUrl());
				Log.d(LOG_TAG, "storing authentication token " + registrationResponse.getAuthenticationToken());
				Log.d(LOG_TAG, "storing registration url " + registrationResponse.getRegistrationUrl());

				synchronizeSettings(session);
				
			} catch (Throwable e) {
				exception = e;
			} finally {
				session.close();
			}
			
			synchronized(this) {
				workerThread = null;
			}
			
			updateObservers();

		}
    	
    }
    
	public class RegistrationServiceBinder extends Binder {
        public RegistrationService getService() {
            return RegistrationService.this;
        }
    }
	
	private void updateObservers() {
		synchronized (observers) {
			Iterator<Runnable> iterator = observers.iterator();
			while (iterator.hasNext()) {
				Runnable nextRunnable = iterator.next();
				handler.post(nextRunnable);
			}
		}

	}

    @Override
    public void onCreate() {
		//android.os.Debug.waitForDebugger();
    	super.onCreate();
    	handler = new Handler();
    	session = new Session();
    	observers = new Vector<Runnable>();
    	regisrationRunnable = new RegisrationRunnable();
    	synchronizeRunnable = new SynchronizeRunnable();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	return START_STICKY;
    }

    @Override
    public void onDestroy() {
    	super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    public void register() {
    	startRunnable(regisrationRunnable);
    }
    
    public void synchronize() {
    	startRunnable(synchronizeRunnable);
    }
    
    private void startRunnable(Runnable runnable) {
    	if (workerThread != null) {
    		throw new RuntimeException();
    	}
    	clearResponse();
    	workerThread = new Thread(runnable);
    	workerThread.start();
    }
    
    public void addObserver(Runnable observer) {
    	synchronized (observers) {
    		observers.remove(observer);
    		observers.add(observer);
    	}
    }
    
    public void clearResponse() {
    	registrationResponse = null;
    	exception = null;
    }
    
    public void removeObserver(Runnable observer) {
    	synchronized (observers) {
    		observers.remove(observer);
    	}
    }
    
    public boolean isSuccess() {
    	return registrationResponse != null && exception == null;
    }
    
    public RegistrationResponse getResponse() {
    	return registrationResponse;
    }
    
    public Throwable getException() {
    	return exception;
    }
    
    public boolean isErrorOccured() {
    	return exception != null;
    }
    
    public boolean isRunning() {
    	synchronized(this) {
    	return workerThread != null;
    	}
    }
}
