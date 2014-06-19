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

package com.birkettenterprise.phonelocator.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.birkettenterprise.phonelocator.application.PhonelocatorApplication;
import com.birkettenterprise.phonelocator.broadcastreceiver.SendWorkToUpdateServiceBroadcastReceiver;
import com.birkettenterprise.phonelocator.database.UpdateLogDatabase;
import com.birkettenterprise.phonelocator.database.UpdateLogDatabaseContentProvider;
import com.birkettenterprise.phonelocator.model.request.MessageRequestDO;
import com.birkettenterprise.phonelocator.request.MessageRequest;
import com.birkettenterprise.phonelocator.utility.LocationMarshallingUtility;
import com.commonsware.cwac.locpoll.LocationPollerResult;

public class UpdateService extends Service {

	private static final String LOG_TAG = PhonelocatorApplication.LOG_TAG + "_UPDATE_SERVICE";
	
	private UpdateLogDatabase database;

    private static interface SendLocationCallback {
        public void onComplete(VolleyError error);
    }

	public UpdateService() {
		//android.os.Debug.waitForDebugger();
	}

    @Override
    public void onCreate() {
       super.onCreate();
       database = new UpdateLogDatabase(this);
    }

    @Override
    public void onDestroy() {
        database.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand()");
        handleUpdateLocation(intent);
        return START_STICKY;
    }
	
	private void storeLocationBestEffort(Intent intent) {
		try {
			Location location = getLocationFromIntent(intent);
			LocationMarshallingUtility.storeLocation(this, location);
		} catch (LocationPollFailedException e) {
		} catch (IOException e) {
		}
	}

    private List<Location> getLocations(Intent intent) throws LocationPollFailedException {
        List<Location> locations = LocationMarshallingUtility.retrieveLocations(this);

        if (locations.size() < 1) {
            // if the location was not stored, perhaps because of an out of
            // disk condition
            Location location = getLocationFromIntent(intent);
            locations = new ArrayList<Location>();
            locations.add(location);
        }
        return locations;
    }

	private void handleUpdateLocation(final Intent intent) {
		Log.v(LOG_TAG, "handleUpdateLocation");
		sendBroadcast(new Intent(
				"com.birkettenterprise.phonelocator.SENDING_UPDATE"));

		try {

            // store the location in case the connection fails so that it can be sent later
            storeLocationBestEffort(intent);

            final List<Location> locations = getLocations(intent);


			sendLocations(locations, new SendLocationCallback() {

                @Override
                public void onComplete(VolleyError error) {
                    if (error == null) {
                        updateLog(locations);
                    } else {
                        updateLog(error);
                    }

                    LocationMarshallingUtility.deleteLocations(UpdateService.this);
                    handleUpdateComplete(intent);
                }
            });

		} catch (LocationPollFailedException e) {
			updateLog(e);
            handleUpdateComplete(intent);
		}
        sendBroadcast(new Intent(
				"com.birkettenterprise.phonelocator.UPDATE_COMPLETE"));
	}

    private void handleUpdateComplete(Intent intent) {
        stopSelf();
        SendWorkToUpdateServiceBroadcastReceiver.completeWakefulIntent(intent);
    }

	private static Location getLocationFromIntent(Intent intent) throws LocationPollFailedException {
		LocationPollerResult locationPollerResult = new LocationPollerResult(intent.getExtras());
		Location location = locationPollerResult.getBestAvailableLocation();
		if (location == null) {
			throw new LocationPollFailedException(locationPollerResult.getError());
		}
		return location;
	}

    private void sendLocations(List<Location> locations, final SendLocationCallback callback)  {

        MessageRequestDO dataObject = createMessageRequestDOFromLocation(locations);

        Response.Listener listener = new Response.Listener() {

            @Override
            public void onResponse(Object response) {
                callback.onComplete(null);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onComplete(error);
            }
        };

        MessageRequest messageRequest = new MessageRequest(dataObject, listener, errorListener);

        PhonelocatorApplication.getInstance().getQueue().add(messageRequest);
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
            message.location.provider = location.getProvider();
            messageRequestDO.add(message);
        }
        return messageRequestDO;
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
