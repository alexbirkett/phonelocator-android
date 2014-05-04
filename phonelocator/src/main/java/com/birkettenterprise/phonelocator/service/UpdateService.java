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
import java.util.List;
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
import com.birkettenterprise.phonelocator.settings.SettingSynchronizationHelper;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;
import com.birkettenterprise.phonelocator.utility.LocationMarshallingUtility;
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
		handleUpdateLocation(intent);
		mDatabase.close();
	}
	
	private void storeLocationBestEffort(Intent intent) {
		try {
			Location location = getLocationFromIntent(intent);
			LocationMarshallingUtility.storeLocation(this, location);
		} catch (LocationPollFailedException e) {
		} catch (IOException e) {
		}
	}

	private void handleUpdateLocation(Intent intent) {
		Log.v(LOG_TAG, "handleUpdateLocation");
		sendBroadcast(new Intent(
				"com.birkettenterprise.phonelocator.SENDING_UPDATE"));

		Session session = new Session();

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		EnvironmentalSettingsSetter.updateEnvironmentalSettingsIfRequired(this);

		try {
			// store the location in case the connection fails so that it can be
			// sent later
			storeLocationBestEffort(intent);

			session.connect();
			session.authenticate(SettingsHelper
					.getAuthenticationToken());

			synchronizeSettings(session);

			List<Location> locations = LocationMarshallingUtility
					.retrieveLocations(this);

			if (locations.size() < 1) {
				// if the location was not stored, perhaps because of an out of
				// disk condition
				Location location = getLocationFromIntent(intent);
				locations = new Vector<Location>();
				locations.add(location);
			}

			sendUpdate(session, locations);

			// delete the stored locations
			LocationMarshallingUtility.deleteLocations(this);

			updateLog(locations);

		} catch (IOException e) {
			updateLog(e);
			Log.d(LOG_TAG, "error sending updates settings " + e.toString());
		} catch (CorruptStreamException e) {
			updateLog(e);
			Log.d(LOG_TAG, "error sending updates settings " + e.toString());
		} catch (AuthenticationFailedException e) {
			updateLog(e);
			Log.d(LOG_TAG, "authentication failed");
		} catch (LocationPollFailedException e) {
			try {
				sendUpdate(session, e.getMessage());
			} catch (IOException e1) {
				updateLog(e1);
			} catch (CorruptStreamException e1) {
				updateLog(e1);
			}
			updateLog(e);
		} finally {
			session.close();
		}
		sendBroadcast(new Intent(
				"com.birkettenterprise.phonelocator.UPDATE_COMPLETE"));
	}
   
	private static Location getLocationFromIntent(Intent intent) throws LocationPollFailedException {
		LocationPollerResult locationPollerResult = new LocationPollerResult(intent.getExtras());
		Location location = locationPollerResult.getBestAvailableLocation();
		if (location == null) {
			throw new LocationPollFailedException(locationPollerResult.getError());
		}
		return location;
	}
	
	private static void synchronizeSettings(Session session) throws IOException {
		Vector<Setting> settings = session.synchronizeSettings(SettingSynchronizationHelper.getSettingsModifiedSinceLastSyncrhonization());
		SettingSynchronizationHelper.updateSettingsSynchronizationTimestamp();
		SettingSynchronizationHelper.setSettings(settings);
	}
	
	private static  void sendUpdate(Session session, List<Location> locations) throws IOException, CorruptStreamException {
		for (Location location : locations) {
			BeaconList beaconList = new BeaconList();
			beaconList.add(new GpsBeacon(location, null));
			session.sendPositionUpdate(beaconList);
		}
	}
	
	private static  void sendUpdate(Session session, String error) throws IOException, CorruptStreamException {
		// send empty beacon list
		BeaconList beaconList = new BeaconList();
		session.sendPositionUpdate(beaconList);
	}
	
	private void updateLog(List<Location> locations) {
		for (Location location : locations) {
			mDatabase.updateLog(location);
		}
		notifyDataChanged();
	}
	
	private void updateLog(Exception e) {
		mDatabase.updateLog(e);
		notifyDataChanged();
	}
	
	private void notifyDataChanged() {
		getContentResolver().notifyChange(UpdateLogDatabaseContentProvider.URI, null);
	}
}
