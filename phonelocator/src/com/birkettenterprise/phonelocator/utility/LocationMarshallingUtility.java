package com.birkettenterprise.phonelocator.utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.location.Location;
import android.os.Parcel;

public class LocationMarshallingUtility {

	private static String LOCATION_FILE_NAME = "location";
	
	public static void storeLocation(Context context, Location locaiton) throws IOException {
		String fileName = "location" + UUID.randomUUID().toString();
		writeLocationToFile(context, locaiton, fileName);
	}
	
	private interface LocationOperation {
		void performOperationOnLocationFile(String fileName);
	}
	
	
	public static void performOperationOnLocationFiles(Context context, LocationOperation operation) {
		String[] fileList = context.fileList();
		for (String fileName : fileList) {
			if (fileName.startsWith(LOCATION_FILE_NAME)) {
				operation.performOperationOnLocationFile(fileName);
			}
		}
	}
	
	public static List<Location> retrieveLocations(final Context context) {
		final Vector<Location> locations = new Vector<Location>();
		
		LocationOperation addLocationOperation = new LocationOperation() {

			@Override
			public void performOperationOnLocationFile(String fileName) {
				try {
					locations.add(readLocationFromFile(context, fileName));
				} catch (IOException e) {
					// ignore corrupt files and files written by older OS versions
				}
			}
			
		};
		performOperationOnLocationFiles(context,addLocationOperation);
		return locations;
	}
	
	
	public static void deleteLocations(final Context context) {
		LocationOperation deleteLocationOperation =  new LocationOperation() {

			@Override
			public void performOperationOnLocationFile(String fileName) {
				context.deleteFile(fileName);
			}
			
		};
		performOperationOnLocationFiles(context,deleteLocationOperation);
	}
	
	public static void writeLocationToFile(Context context, Location locaiton, String fileName) throws IOException {
		
		FileOutputStream fileOutputStream  = null;
		try {
			fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			Parcel parcel = Parcel.obtain();
			locaiton.writeToParcel(parcel, 0);
			byte[] data = parcel.marshall();
			fileOutputStream.write(data);
			fileOutputStream.flush();
			parcel.recycle();
		} catch (IOException e) {
			closeInputStreamBestEffort(fileOutputStream);
			throw e;
		}
		
		closeInputStreamBestEffort(fileOutputStream);
	}
	
	
	public static void closeInputStreamBestEffort(OutputStream stream) {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (IOException e) {
			// ignore
		}
	}
	
	public static Location readLocationFromFile(Context context, String fileName)
			throws IOException {
		FileInputStream fileInputStream = context.openFileInput(fileName);

		Parcel parcel = Parcel.obtain();
		byte[] data = IOUtils.toByteArray(fileInputStream);
		parcel.unmarshall(data, 0, data.length);
		parcel.setDataPosition(0);
		Location location = Location.CREATOR.createFromParcel(parcel);
		parcel.recycle();
		return location;

	}
}
