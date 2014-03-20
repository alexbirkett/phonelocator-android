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

package com.birkettenterprise.phonelocator.domain;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.location.Location;

public class GpsBeacon implements Internalizable {
    
	public static final int GENERAL_ERROR = -1;
	public static final int SECONDS_IN_MILLISECOND = 1000;
	
	private int error;
	private List<SatelliteData> satelliteDataArray;
	
	private float speed;
	private float speedAccuracy;
	private float heading;
	private float headingAccuracy;
	private float course;
	private float courseAccuracy;
	private long time;
	private float horizontalAccuracy;
	private float verticalAccuracy;
	private double latitude;
	private double longitude;
	private float altitude;
	private int datum;
	private byte numSatellitesUsed;
	private long satelliteTime;
    // dop: dilution of precision
	private float horizontalDop;
	private float verticalDop;
	private float timeDop;
	private byte numSatellitesInView;
	private int moduleId;
	private int updateType;
	private int positionClassType;
	private int positionClassSize;
    
	public GpsBeacon() {
		satelliteDataArray = new ArrayList<SatelliteData>();
	}

	public GpsBeacon(Location location, String error) {
		this();
		if (error != null) {
			this.error = GENERAL_ERROR;
		} 
		
		if (location != null) {
			setSpeed(location.getSpeed());
			setHeading(location.getBearing());
			setTime(location.getTime() / SECONDS_IN_MILLISECOND);
			setLongitude(location.getLongitude());
			setLatitude(location.getLatitude());
			setAltitude((float)location.getAltitude());
			setVerticalAccuracy(location.getAccuracy());
			setHorizontalAccuracy(location.getAccuracy());	
		}
	}
	
	private static float returnFloat(float aFloat) {
		if (!Float.isNaN(aFloat)) {
			return aFloat;
		} else {
			return 0;
		}
	}
	
	private double returnDouble(double aDouble) {
		if (!Double.isNaN(aDouble)) {
			return aDouble;
		} else {
			return 0;
		}
	}
	
	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public List<SatelliteData> getSatelliteDataArray() {
		return satelliteDataArray;
	}
	
	public void setSatelliteDataArray(List<SatelliteData> satelliteDataArray) {
		this.satelliteDataArray = satelliteDataArray;
	}
	
	public float getSpeed() {
		return returnFloat(speed);
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public float getSpeedAccuracy() {
		return returnFloat(speedAccuracy);
	}
	
	public void setSpeedAccuracy(float speedAccuracy) {
		this.speedAccuracy = speedAccuracy;
	}
	
	public float getHeading() {
		return returnFloat(heading);
	}
	
	public void setHeading(float heading) {
		this.heading = heading;
	}
	
	public float getHeadingAccuracy() {
		return returnFloat(headingAccuracy);
	}
	
	public void setHeadingAccuracy(float headingAccuracy) {
		this.headingAccuracy = headingAccuracy;
	}
	
	public float getCourse() {
		return returnFloat(course);
	}
	
	public void setCourse(float course) {
		this.course = course;
	}
	
	public float getCourseAccuracy() {
		return returnFloat(courseAccuracy);
	}
	
	public void setCourseAccuracy(float courseAccuracy) {
		this.courseAccuracy = courseAccuracy;
	}
	
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public float getHorizontalAccuracy() {
		return returnFloat(horizontalAccuracy);
	}

	public void setHorizontalAccuracy(float horizontalAccuracy) {
		this.horizontalAccuracy = horizontalAccuracy;
	}

	public float getVerticalAccuracy() {
		return returnFloat(verticalAccuracy);
	}

	public void setVerticalAccuracy(float verticalAccuracy) {
		this.verticalAccuracy = verticalAccuracy;
	}

	public double getLatitude() {
		return returnDouble(latitude);
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return returnDouble(longitude);
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public float getAltitude() {
		return returnFloat(altitude);
	}

	public void setAltitude(float altitude) {
		this.altitude = altitude;
	}

	public int getDatum() {
		return datum;
	}

	public void setDatum(int datum) {
		this.datum = datum;
	}

	public byte getNumSatellitesUsed() {
		return numSatellitesUsed;
	}

	public void setNumSatellitesUsed(byte numSatellitesUsed) {
		this.numSatellitesUsed = numSatellitesUsed;
	}

	public long getSatelliteTime() {
		return satelliteTime;
	}

	public void setSatelliteTime(long satelliteTime) {
		this.satelliteTime = satelliteTime;
	}

	public float getHorizontalDop() {
		return returnFloat(horizontalDop);
	}

	public void setHorizontalDop(float horizontalDop) {
		this.horizontalDop = horizontalDop;
	}

	public float getVerticalDop() {
		return returnFloat(verticalDop);
	}

	public void setVerticalDop(float verticalDop) {
		this.verticalDop = verticalDop;
	}

	public float getTimeDop() {
		return returnFloat(timeDop);
	}

	public void setTimeDop(float timeDop) {
		this.timeDop = timeDop;
	}

	public byte getNumSatellitesInView() {
		return numSatellitesInView;
	}

	public void setNumSatellitesInView(byte numSatellitesInView) {
		this.numSatellitesInView = numSatellitesInView;
	}

	public int getModuleId() {
		return moduleId;
	}

	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}

	public int getUpdateType() {
		return updateType;
	}

	public void setUpdateType(int updateType) {
		this.updateType = updateType;
	}

	public int getPositionClassType() {
		return positionClassType;
	}

	public void setPositionClassType(int positionClassType) {
		this.positionClassType = positionClassType;
	}

	public int getPositionClassSize() {
		return positionClassSize;
	}

	public void setPositionClassSize(int positionClassSize) {
		this.positionClassSize = positionClassSize;
	}

	public void internalize(DataInputStream inputStream) throws IOException {
		setError(inputStream.readInt());
		
		byte satelliteDataCount = inputStream.readByte();
		for (byte i = 0; i < satelliteDataCount; i++) {
			SatelliteData satelliteData = new SatelliteData();
			satelliteData.internalize(inputStream);
			satelliteDataArray.add(satelliteData);
		}
		
		setSpeed(inputStream.readFloat());
		setSpeedAccuracy(inputStream.readFloat());
		setHeading(inputStream.readFloat());
		setHeadingAccuracy(inputStream.readFloat());
		setCourse(inputStream.readFloat());
		setCourseAccuracy(inputStream.readFloat());
		
		setTime(inputStream.readInt());
		setHorizontalAccuracy(inputStream.readFloat());
		setVerticalAccuracy(inputStream.readFloat());
		setLatitude(inputStream.readDouble());
		setLongitude(inputStream.readDouble());	
		setAltitude(inputStream.readFloat());
		setDatum(inputStream.readInt());

		setNumSatellitesUsed(inputStream.readByte());
		setSatelliteTime(inputStream.readInt());
		setHorizontalDop(inputStream.readFloat());
		setVerticalDop(inputStream.readFloat());
		setTimeDop(inputStream.readFloat());
		setNumSatellitesInView(inputStream.readByte());
		setModuleId(inputStream.readInt());
		setUpdateType(inputStream.readInt());
		setPositionClassType(inputStream.readInt());
		setPositionClassSize(inputStream.readInt());
		}

	public void externalize(DataOutputStream outputStream) throws IOException {
		outputStream.writeByte(BeaconTypes.GPS);
		
		outputStream.writeInt(getError());
		
		outputStream.writeByte(satelliteDataArray.size());
		Iterator<SatelliteData> iterator = satelliteDataArray.iterator();
		while (iterator.hasNext()) {
			SatelliteData satelliteData = iterator.next();
			satelliteData.externalize(outputStream);
		}
		
		outputStream.writeFloat(getSpeed());
		outputStream.writeFloat(getSpeedAccuracy());
		outputStream.writeFloat(getHeading());
		outputStream.writeFloat(getHeadingAccuracy());
		outputStream.writeFloat(getCourse());
		outputStream.writeFloat(getCourseAccuracy());
		
		outputStream.writeInt((int)getTime());
		outputStream.writeFloat(getHorizontalAccuracy());
		outputStream.writeFloat(getVerticalAccuracy());
		outputStream.writeDouble(getLatitude());
		outputStream.writeDouble(getLongitude());
		outputStream.writeFloat(getAltitude());
		outputStream.writeInt(getDatum());
		
		
		outputStream.writeByte(getNumSatellitesUsed());
		outputStream.writeInt((int)getSatelliteTime());
		outputStream.writeFloat(getHorizontalDop());
		outputStream.writeFloat(getVerticalDop());
		outputStream.writeFloat(getTimeDop());
		outputStream.writeByte(getNumSatellitesInView());
		outputStream.writeInt(getModuleId());
		outputStream.writeInt(getUpdateType());
		outputStream.writeInt(getPositionClassType());
		outputStream.writeInt(getPositionClassSize());		
	}
}
