/**
 * 
 *  Copyright 2012 Birkett Enterprise Ltd
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

package com.commonsware.cwac.locpoll;

import android.location.Location;
import android.os.Bundle;

public class LocationPollerResult {

	private static final String KEY = "com.commonsware.cwac.locpoll.";
	static final String LOCATION_KEY = KEY + "EXTRA_LOCATION";
	static final String LASTKNOWN_LOCATION_KEY = KEY + "EXTRA_LASTKNOWN";
	static final String ERROR_KEY = KEY + "EXTRA_ERROR";

	private Bundle bundle;
	
	public LocationPollerResult(Bundle bundle) {
		this.bundle = bundle;
	}
	
	public Location getLocation() {
		return (Location)bundle.get(LOCATION_KEY);
	}
	
	public void setLocation(Location location) {
		bundle.putParcelable(LOCATION_KEY, location);
	}

	public Location getLastKnownLocation() {
		return (Location)bundle.get(LASTKNOWN_LOCATION_KEY);
	}

	public void setLastKnownLocation(Location location) {
		bundle.putParcelable(LASTKNOWN_LOCATION_KEY, location);
	}
	
	public String getError() {
		return bundle.getString(ERROR_KEY);
	}
	
	public Location getBestAvailableLocation() {
		Location location = getLocation();
		if (location == null) {
			location = getLastKnownLocation();
		}
		return location;
	}

}
