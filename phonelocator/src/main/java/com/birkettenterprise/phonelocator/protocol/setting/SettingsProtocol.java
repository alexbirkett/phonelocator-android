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

package com.birkettenterprise.phonelocator.protocol.setting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Vector;

import android.util.Log;

import com.birkettenterprise.phonelocator.application.PhonelocatorApplication;
import com.birkettenterprise.phonelocator.settings.Setting;

public class SettingsProtocol {

	public static final String LOG_TAG = PhonelocatorApplication.LOG_TAG + "_SETTINGS_PROTOCOL";
	
	public static final int DEFAULT_BOOLEAN_COUNT = 30;
	public static final int DEFAULT_INTEGER_COUNT = 21;
	public static final int DEFAULT_STRING_COUNT = 14;
	public static final int DEFAULT_INT64_COUNT = 2;
	
	private static final byte PROTOCOL_VERSION_2 = 2;
	
	public static void writeSettings(Vector<Setting> settings,
			DataOutputStream outputStream) throws IOException {
		outputStream.writeByte(PROTOCOL_VERSION_2);

		for (Setting setting : settings) {
			try {
				int settingId = SettingIdResolver.getSettingsIdForName(setting.getName());
				writeSetting(settingId, setting.getTimestamp(),
						setting.getValue(), outputStream);
			} catch (UnknowSettingException e) {
				// ignore unknown settings
			}
		}

		outputStream.writeByte(SettingsOffsets.END_OF_SETTINGS_MARKER);
	}
	
	public static Vector<Setting> readingSettings(DataInputStream dataInputStream) throws IOException {
		
		/*int streamLength =*/ dataInputStream.readUnsignedShort();
		
		short settingId = 0;
		Vector<Setting> settings = new Vector<Setting>();
		while ((settingId = (short)dataInputStream.readUnsignedByte()) != SettingsOffsets.END_OF_SETTINGS_MARKER) {
			try {
				settings.add(readSetting(dataInputStream, settingId));
			} catch (UnknowSettingException e) {
				// ignore unknown setting
			}
		}
		return settings;
	}
	
	private static Setting readSetting(DataInputStream dataInputStream, int settingId) throws IOException, UnknowSettingException {
		Setting setting = new Setting();
		int timeStampInSeconds = dataInputStream.readInt();
		long timeStampInMilliseconds = timeStampInSeconds * 1000L;
		setting.setTimestamp(timeStampInMilliseconds);
		
		if (SettingsOffsets.BOOLEAN_OFFSET <= settingId && settingId <  SettingsOffsets.INTEGER_OFFSET) {
			setting.setValue(dataInputStream.readByte() > 0);
		} else if (SettingsOffsets.INTEGER_OFFSET <= settingId && settingId < SettingsOffsets.INT64_OFFSET) {
			setting.setValue(dataInputStream.readInt() + "");
		} else if (SettingsOffsets.INT64_OFFSET <= settingId && settingId < SettingsOffsets.STRING_OFFSET) {
			setting.setValue(readBigInteger(dataInputStream));
		} else if (SettingsOffsets.STRING_OFFSET <= settingId && settingId < SettingsOffsets.END_OF_SETTINGS_MARKER) {
			setting.setValue(dataInputStream.readUTF());			
		}
		
		try {
			setting.setName(SettingNameResolver.getSettingsNameForId(settingId));
			Log.d(LOG_TAG, "read setting id: " + settingId + " name: " + setting.getName() + " value: " + setting.getValue().toString() + " timestamp: " + setting.getTimestamp());
		} catch (UnknowSettingException e) {
			Log.d(LOG_TAG, "ignored setting id: " + settingId + " value: " + setting.getValue().toString() + " timestamp: " + setting.getTimestamp());
			throw e;		
		}
		
		return setting;
	}
	
	private static void writeSetting(int settingId, long timeStamp, Object value,  DataOutputStream outputStream) throws IOException {
		outputStream.writeByte(settingId);
		writeTimestamp(timeStamp, outputStream);
		try {
			writeSettingValue(settingId,value, outputStream);			
		} catch (ClassCastException e) {
			Log.d(LOG_TAG, "ClassCastException error writing setting " + settingId);
			throw e;
		}
		Log.d(LOG_TAG, "written setting id: " + settingId + " value: " + value + " timestamp: " + timeStamp);

	}
	
	private static void writeTimestamp(long timestamp, DataOutputStream outputStream) throws IOException {
		long timeStampInSeconds = timestamp/1000;
		outputStream.writeInt((int) timeStampInSeconds);
	}
	
	private static void writeSettingValue(int settingId, Object setting, DataOutputStream outputStream) throws IOException {
		if (SettingsOffsets.BOOLEAN_OFFSET <= settingId && settingId <  SettingsOffsets.INTEGER_OFFSET) {
			byte byteToWrite = (Boolean)setting ? (byte)1 : (byte)0;
			outputStream.writeByte(byteToWrite);
		} else if (SettingsOffsets.INTEGER_OFFSET <= settingId && settingId < SettingsOffsets.INT64_OFFSET) {
			int intToWrite = 0;
			
			if (setting instanceof String) {
				intToWrite = Integer.parseInt((String)setting);
			} else if (setting instanceof Integer) {
				intToWrite = (Integer)setting;
			}
	
			outputStream.writeInt(intToWrite);	
		} else if (SettingsOffsets.INT64_OFFSET <= settingId && settingId < SettingsOffsets.STRING_OFFSET) {
			
			BigInteger bigIntegerToWrite = BigInteger.ZERO;
			if (setting instanceof String) {
				bigIntegerToWrite = new BigInteger((String)setting);
			} else if (setting instanceof BigInteger){
				bigIntegerToWrite = (BigInteger)setting;
			}
			writeBigInteger(outputStream, bigIntegerToWrite);
			
		} else if (SettingsOffsets.STRING_OFFSET <= settingId && settingId < SettingsOffsets.END_OF_SETTINGS_MARKER) {
			String stringToWrite = (String)setting;
			outputStream.writeUTF(stringToWrite);
		}
	}
	
	private static BigInteger readBigInteger(DataInputStream stream) throws IOException {
        byte[] byteArray = new byte[8];
        stream.read(byteArray);
        return new BigInteger(1, byteArray); 
	}
	
	private static void writeBigInteger(DataOutputStream stream, BigInteger value) throws IOException {
		byte[] bigIntegerAsByteArray = ((BigInteger)value).toByteArray();
		int desPos = 8 - bigIntegerAsByteArray.length;
        byte[] byteArray = new byte[8];
        System.arraycopy(bigIntegerAsByteArray, 0, byteArray, desPos > 0 ? desPos: 0, bigIntegerAsByteArray.length > 8 ? 8 : bigIntegerAsByteArray.length);
		stream.write(byteArray);
	}
}
