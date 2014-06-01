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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.birkettenterprise.phonelocator.application.PhonelocatorApplication;
import com.birkettenterprise.phonelocator.database.UpdateLogDatabase;
import com.birkettenterprise.phonelocator.database.UpdateLogDatabaseContentProvider;
import com.birkettenterprise.phonelocator.domain.BeaconList;
import com.birkettenterprise.phonelocator.model.request.MessageRequestDO;
import com.birkettenterprise.phonelocator.protocol.CorruptStreamException;
import com.birkettenterprise.phonelocator.protocol.Session;
import com.birkettenterprise.phonelocator.request.MessageRequest;
import com.birkettenterprise.phonelocator.utility.LocationMarshallingUtility;
import com.commonsware.cwac.locpoll.LocationPollerResult;
import com.commonsware.cwac.wakeful.WakefulIntentService;

public class UpdateService extends WakefulIntentService {

    static class VolleyExceptionWrapper {
        VolleyError volleyError;
    };

	private static final String LOG_TAG = PhonelocatorApplication.LOG_TAG + "_UPDATE_SERVICE";
	
	private UpdateLogDatabase database;
	
	public UpdateService() {
		super("PhonelocatorSerivce");
		//android.os.Debug.waitForDebugger();
		database = new UpdateLogDatabase(this);
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		handleUpdateLocation(intent);
		database.close();
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

		try {

            // store the location in case the connection fails so that it can be sent later
            storeLocationBestEffort(intent);

            List<Location> locations = LocationMarshallingUtility.retrieveLocations(this);

            if (locations.size() < 1) {
                // if the location was not stored, perhaps because of an out of
                // disk condition
                Location location = getLocationFromIntent(intent);
                locations = new ArrayList<Location>();
                locations.add(location);
            }

			sendLocations(locations);

			updateLog(locations);

            LocationMarshallingUtility.deleteLocations(this);

		} catch (LocationPollFailedException e) {
			updateLog(e);
		} catch (InterruptedException e) {
            updateLog(e);
        } catch (VolleyError e) {
            updateLog(e);
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

    private void sendLocations(List<Location> locations) throws InterruptedException, VolleyError {
        final CountDownLatch latch = new CountDownLatch(1);
        MessageRequestDO dataObject = createMessageRequestDOFromLocation(locations);
        final VolleyExceptionWrapper volleyExceptionWrapper = new VolleyExceptionWrapper();

        Response.Listener listener = new Response.Listener() {

            @Override
            public void onResponse(Object response) {
                latch.countDown();

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyExceptionWrapper.volleyError = error;
                latch.countDown();
            }
        };

        MessageRequest messageRequest = new MessageRequest(dataObject, listener, errorListener);

        PhonelocatorApplication.getInstance().getQueue().add(messageRequest);

        latch.await();
        if (volleyExceptionWrapper.volleyError != null) {
            throw volleyExceptionWrapper.volleyError;
        }
    }

    private static MessageRequestDO createMessageRequestDOFromLocation(List<Location> locations) {
        MessageRequestDO messageRequestDO = new MessageRequestDO();

        for (Location location : locations) {
            MessageRequestDO.Message message = new  MessageRequestDO.Message();
            message.location.latitude = location.getLatitude();
            message.location.longitude = location.getLongitude();
            message.location.speed = location.getSpeed();
            message.location.course = location.getBearing();
            message.location.accuracy = location.getAccuracy();
            message.location.timestamp = location.getTime();
            messageRequestDO.add(message);
        }
        return messageRequestDO;
    }

	private static  void sendUpdate(Session session, String error) throws IOException, CorruptStreamException {
		// send empty beacon list
		BeaconList beaconList = new BeaconList();
		session.sendPositionUpdate(beaconList);
	}
	
	private void updateLog(List<Location> locations) {
        for (Location location: locations) {
            database.updateLog(location);
        }
		notifyDataChanged();
	}
	
	private void updateLog(Exception e) {
		database.updateLog(e);
		notifyDataChanged();
	}
	
	private void notifyDataChanged() {
		getContentResolver().notifyChange(UpdateLogDatabaseContentProvider.URI, null);
	}
}
