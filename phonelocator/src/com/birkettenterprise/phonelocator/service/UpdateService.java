/**
 * 
 *  Copyright 2011 Birkett Enterprise Ltd
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
import java.util.Vector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import com.birkettenterprise.phonelocator.application.PhonelocatorApplication;
import com.birkettenterprise.phonelocator.database.UpdateLogDatabase;
import com.birkettenterprise.phonelocator.database.UpdateLogDatabaseContentProvider;
import com.birkettenterprise.phonelocator.domain.BeaconList;
import com.birkettenterprise.phonelocator.domain.GpsBeacon;
import com.birkettenterprise.phonelocator.protocol.AuthenticationFailedException;
import com.birkettenterprise.phonelocator.protocol.CorruptStreamException;
import com.birkettenterprise.phonelocator.protocol.Session;
import com.birkettenterprise.phonelocator.settings.EnvironmentalSettingsSetter;
import com.birkettenterprise.phonelocator.settings.Setting;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;
import com.birkettenterprise.phonelocator.settings.SettingsManager;
import com.commonsware.cwac.locpoll.LocationPollerResult;
import com.commonsware.cwac.wakeful.WakefulIntentService;

public class UpdateService extends WakefulIntentService {

	public static final String COMMAND = "command";
		
	public static final int UPDATE_LOCATION = 1;
	public static final int SYNCHRONIZE_SETTINGS = 2;

	private static final String LOG_TAG = PhonelocatorApplication.LOG_TAG + "_UPDATE_SERVICE";
	
	private UpdateLogDatabase mDatabase;
	
	public UpdateService() {
		super("PhonelocatorSerivce");
		//android.os.Debug.waitForDebugger();
		mDatabase = new UpdateLogDatabase(this);
	}

	@Override
	protected void doWakefulWork(Intent intent) {
	//	int command = intent.getIntExtra(COMMAND, -1);		
		handleUpdateLocation(intent);
		mDatabase.close();
	}
	
	
	
	private void handleUpdateLocation(Intent intent) {
		Log.v(LOG_TAG, "handleUpdateLocation");
		
		Session session = new Session();
		SettingsManager settingsManager = SettingsManager.getInstance(this, this);
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		EnvironmentalSettingsSetter.updateEnvironmentalSettingsIfRequired(sharedPreferences, this);
		
		try {
			session.connect();
			session.authenticate(SettingsHelper.getAuthenticationToken(sharedPreferences));
			
			synchronizeSettings(session, settingsManager);
			try {
				Location location = getLocationFromIntent(intent);
				sendUpdate(session, location, null);
				updateLog(location);
			} catch (LocationPollFailedException e) {
				sendUpdate(session, null, e.getMessage());
				updateLog(e);
			}
			
		} catch (IOException e) {
			updateLog(e);
			Log.d(LOG_TAG,"error sending updates settings " + e.toString());
		} catch (CorruptStreamException e) {
			updateLog(e);
			Log.d(LOG_TAG,"error sending updates settings " + e.toString());
		} catch (AuthenticationFailedException e) {
			updateLog(e);
			Log.d(LOG_TAG,"authentication failed");
		} finally {
			try {
				settingsManager.releaseInstance(this);
				session.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
   
	private static Location getLocationFromIntent(Intent intent) throws LocationPollFailedException {
		LocationPollerResult locationPollerResult = new LocationPollerResult(intent.getExtras());
		Location location = locationPollerResult.getBestAvailableLocation();
		if (location == null) {
			throw new LocationPollFailedException(locationPollerResult.getError());
		}
		return location;
	}
	
	private static void synchronizeSettings(Session session, SettingsManager settingsManager) throws IOException {
		Vector<Setting> settings = session.synchronizeSettings(settingsManager.getSettingsModifiedSinceLastSyncrhonization());
		settingsManager.updateSettingsSynchronizationTimestamp();
		settingsManager.setSettings(settings);

	}
	
	private static  void sendUpdate(Session session, Location location, String error) throws IOException, CorruptStreamException {
		BeaconList beaconList = new BeaconList();
		beaconList.add(new GpsBeacon(location, error));
		session.sendPositionUpdate(beaconList);
	}
	
	private void updateLog(Location location) {
		mDatabase.updateLog(location);
		
		getContentResolver().notifyChange(UpdateLogDatabaseContentProvider.URI, null);
	}
	
	private void updateLog(Exception e) {
		mDatabase.updateLog(e);
	}
}
