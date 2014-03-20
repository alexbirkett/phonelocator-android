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

public class SatelliteData implements Internalizable {
	
	public int getSatelliteId() {
		return satelliteId;
	}

	public void setSatelliteId(int satelliteId) {
		this.satelliteId = satelliteId;
	}

	public float getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(float azimuth) {
		this.azimuth = azimuth;
	}

	public float getElevation() {
		return elevation;
	}

	public void setElevation(float elevation) {
		this.elevation = elevation;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public int getSignalStrength() {
		return signalStrength;
	}

	public void setSignalStrength(int signalStrength) {
		this.signalStrength = signalStrength;
	}

	int satelliteId;
	float azimuth;
	float elevation;
	boolean used;
	int signalStrength;
	
	public void internalize(DataInputStream inputStream) throws IOException {
		setSatelliteId(inputStream.readInt());
		setAzimuth(inputStream.readFloat());
		setElevation(inputStream.readFloat());
		setUsed(inputStream.readByte() != 0);
		setSignalStrength(inputStream.readInt());
	}

	public void externalize(DataOutputStream outputStream) throws IOException {
		outputStream.writeInt(getSatelliteId());
		outputStream.writeFloat(getAzimuth());
		outputStream.writeFloat(getElevation());
		outputStream.writeByte(isUsed() ? 1 : 0);
		outputStream.writeInt(getSignalStrength());
		
	}
}
