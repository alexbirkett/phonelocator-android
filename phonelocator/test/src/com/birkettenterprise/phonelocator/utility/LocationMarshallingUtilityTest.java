package com.birkettenterprise.phonelocator.utility;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.test.AndroidTestCase;

public class LocationMarshallingUtilityTest extends AndroidTestCase {

	public static final String FILE_NAME = "location";
	public static final String GARBAGE_FILE_NAME = "garbage_filename";
	private static final byte[] GARBAGE = { -1, -2, -3, 4,5,6, -7,-8-9};

	public void testUnmarshalGarbage() {
		
		try {
			writeToFile(getContext(), GARBAGE, GARBAGE_FILE_NAME);
			LocationMarshallingUtility.readLocationFromFile(getContext(), GARBAGE_FILE_NAME);
			// Unfortunately it seems there is no way to make
			// Location.CREATOR.createFromParcel fail if you pass it garbage, it
			// just returns an empty Location
			// it also appears that Location has not implemented the Object
			// equals() method so there is not way to compare the returned
			// Location against a known empty location
	} catch (IOException e) {
			assertTrue(false);
		}
	}

	public void testReadWriteLocationToFile() {
		Location location = new Location("");
		location.setLatitude(-2);
		location.setLongitude(53);
		
		try {
			LocationMarshallingUtility.writeLocationToFile(getContext(), location, FILE_NAME);
			
			Location recreatedLocation = LocationMarshallingUtility.readLocationFromFile(getContext(), FILE_NAME);
			
			assertEquals(-2, recreatedLocation.getLatitude(), 0.01);
			assertEquals(53, recreatedLocation.getLongitude(), 0.01);
			
		} catch (IOException e) {
			assertTrue(false);
		}
		LocationMarshallingUtility.deleteLocations(getContext());
	}
	
	public void testStoreRetrieveLocations() {
		LocationMarshallingUtility.deleteLocations(getContext());
		
		Location location1 = new Location("1");
		location1.setLatitude(-2);
		location1.setLongitude(53);
		
		Location location2 = new Location("2");
		location2.setLatitude(1);
		location2.setLongitude(52);
		
		try {
			LocationMarshallingUtility.storeLocation(getContext(), location1);
			LocationMarshallingUtility.storeLocation(getContext(), location2);
			
			List<Location> locations = LocationMarshallingUtility.retrieveLocations(getContext());
			for (Location location : locations) {
				if ("1".equals(location.getProvider())) {
					assertEquals(location.getLatitude(), -2, 0.01);
					assertEquals(location.getLongitude(), 53, 0.01);
				} else if ("2".equals(location.getProvider())) {
					assertEquals(location.getLatitude(), 1, 0.01);
					assertEquals(location.getLongitude(), 52, 0.01);
				} else {
					assertTrue(false);
				}
			}
		} catch (IOException e) {
			assertTrue(false);
		}
		
		LocationMarshallingUtility.deleteLocations(getContext());
		
		List<Location> locations = LocationMarshallingUtility.retrieveLocations(getContext());
		assertEquals(0, locations.size());
	}
	
	public static void writeToFile(Context context, byte[] data, String fileName) throws IOException {
		
		FileOutputStream fileOutputStream  = null;
		try {
			fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			fileOutputStream.write(data);
			fileOutputStream.flush();
		
		} catch (IOException e) {
			LocationMarshallingUtility.closeInputStreamBestEffort(fileOutputStream);
			throw e;
		}
		LocationMarshallingUtility.closeInputStreamBestEffort(fileOutputStream);
	}
}
