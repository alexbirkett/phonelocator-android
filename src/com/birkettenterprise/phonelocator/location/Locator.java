package com.birkettenterprise.phonelocator.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class Locator {

	private Context mContext;
	private Location mLocation;
	private Object lock = new Object();
	
	public Locator(Context context) {
		this.mContext = context;
	}
	
	public Location getLocation() throws Exception {
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		    	mLocation = location;
		    	lock.notify();
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {
		    	int i = 0;
		    	i++;
		    }

		    public void onProviderEnabled(String provider) {
		    	int i = 0;
		    	i++;
		    }

		    public void onProviderDisabled(String provider) {
		    	int i = 0;
		    	i++;
		    }
		  };

		// Register the listener with the Location Manager to receive location updates
		// Callback on main thread
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener, null);
		
		synchronized(lock) {
		lock.wait();
		}
		return mLocation;
		
	}
}
