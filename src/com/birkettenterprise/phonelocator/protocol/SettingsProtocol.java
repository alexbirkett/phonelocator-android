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

package com.birkettenterprise.phonelocator.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Vector;

import android.util.Log;

import com.birkettenterprise.phonelocator.application.PhonelocatorApplication;
import com.birkettenterprise.phonelocator.util.Setting;

public class SettingsProtocol {

	public static final String LOG_TAG = PhonelocatorApplication.LOG_TAG + "_SETTINGS_PROTOCOL";
	
	public static final short BOOLEAN_OFFSET = 0;
	public static final short INTEGER_OFFSET = 64;
	public static final short INT64_OFFSET = 128;
	public static final short STRING_OFFSET = 160;
	public static final short END_OF_SETTINGS_MARKER = 255;
	
	public static final int DEFAULT_BOOLEAN_COUNT = 30;
	public static final int DEFAULT_INTEGER_COUNT = 21;
	public static final int DEFAULT_STRING_COUNT = 14;
	public static final int DEFAULT_INT64_COUNT = 2;
	
	private static final byte PROTOCOL_VERSION_2 = 2;

	private class BooleanSettings {
		public static final int PERIODIC_UPDATE_ENABLED = 5;
		public static final int REGISTERED = 6;
	}
	
	private class IntegerSettings {
		public static final int UPDATE_FREQUENCY = 3;
	}

	public static void writeSettings(Vector<Setting> settings, DataOutputStream outputStream) throws IOException {
		outputStream.writeByte(PROTOCOL_VERSION_2);
		
		if (settings == null) {
			createDefaultSettings(outputStream);
		} else {
			for (Setting setting : settings) {
				try {
					byte settingId = getSettingIdForSettingName(setting
							.getName());
					writeSetting(settingId, setting.getTimestamp(),
							setting.getValue(), outputStream);
				} catch (UnknowSettingException e) {
					// ignore unknown settings
				}
			}
			
			
		}
		outputStream.writeByte(END_OF_SETTINGS_MARKER);
	}
	
	public static Vector<Setting> readingSettings(DataInputStream dataInputStream) throws IOException {
		
		/*int streamLength =*/ dataInputStream.readUnsignedShort();
		
		short settingId = 0;
		Vector<Setting> settings = new Vector<Setting>();
		while ((settingId = (short)dataInputStream.readUnsignedByte()) != END_OF_SETTINGS_MARKER) {
			try {
				settings.add(readSetting(dataInputStream, settingId));
			} catch (UnknowSettingException e) {
				// ignore unknown setting
			}
		}
		return settings;
	}

	private static byte getSettingIdForSettingName(String settingName) throws UnknowSettingException {
		if (settingName.equals(Setting.BooleanSettings.PERIODIC_UPDATES_ENABLED)) {
			return ((byte)(BooleanSettings.PERIODIC_UPDATE_ENABLED + BOOLEAN_OFFSET));
		} else if (settingName.equals(Setting.BooleanSettings.REGISTERED)) {
			return BooleanSettings.REGISTERED + BOOLEAN_OFFSET;
		} else if (settingName.equals(Setting.StringSettings.UPDATE_FREQUENCY)) {
			return IntegerSettings.UPDATE_FREQUENCY + INTEGER_OFFSET;
		}
		throw new UnknowSettingException();
	}
	
	private static String getSettingsNameForId(int settingsId) throws UnknowSettingException {
		if (settingsId == BooleanSettings.PERIODIC_UPDATE_ENABLED + BOOLEAN_OFFSET) {
			return Setting.BooleanSettings.PERIODIC_UPDATES_ENABLED;
		} else if (settingsId == BooleanSettings.REGISTERED + BOOLEAN_OFFSET) {
			return Setting.BooleanSettings.REGISTERED;
		} else if (settingsId ==  IntegerSettings.UPDATE_FREQUENCY + INTEGER_OFFSET) {
			return Setting.StringSettings.UPDATE_FREQUENCY;
		}
		throw new UnknowSettingException();
	}

	
	private static Setting readSetting(DataInputStream dataInputStream, int settingId) throws IOException, UnknowSettingException {
		Setting setting = new Setting();
		int timeStampInSeconds = dataInputStream.readInt();
		long timeStampInMilliseconds = timeStampInSeconds * 1000L;
		setting.setTimestamp(timeStampInMilliseconds);
		
		if (BOOLEAN_OFFSET <= settingId && settingId <  INTEGER_OFFSET) {
			setting.setValue(dataInputStream.readByte() > 0);
		} else if (INTEGER_OFFSET <= settingId && settingId < INT64_OFFSET) {
			setting.setValue(dataInputStream.readInt() + "");
		} else if (INT64_OFFSET <= settingId && settingId < STRING_OFFSET) {
			setting.setValue(readBigInteger(dataInputStream));
		} else if (STRING_OFFSET <= settingId && settingId < END_OF_SETTINGS_MARKER) {
			setting.setValue(dataInputStream.readUTF());			
		}
		
		try {
			setting.setName(getSettingsNameForId(settingId));
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
		writeSettingValue(settingId,value, outputStream);
		Log.d(LOG_TAG, "written setting id: " + settingId + " value: " + value.toString() + " timestamp: " + timeStamp);

	}
	
	private static void writeTimestamp(long timestamp, DataOutputStream outputStream) throws IOException {
		long timeStampInSeconds = timestamp/1000;
		outputStream.writeInt((int) timeStampInSeconds);
	}
	
	private static void writeSettingValue(int settingId, Object setting, DataOutputStream outputStream) throws IOException {
		if (BOOLEAN_OFFSET <= settingId && settingId <  INTEGER_OFFSET) {
			byte byteToWrite = (Boolean)setting ? (byte)1 : (byte)0;
			outputStream.writeByte(byteToWrite);
		} else if (INTEGER_OFFSET <= settingId && settingId < INT64_OFFSET) {
			int intToWrite = 0;
			
			if (setting instanceof String) {
				intToWrite = Integer.parseInt((String)setting);
			} else if (setting instanceof Integer) {
				intToWrite = (Integer)setting;
			}
	
			outputStream.writeInt(intToWrite);	
		} else if (INT64_OFFSET <= settingId && settingId < STRING_OFFSET) {
			writeBigInteger(outputStream, (BigInteger)setting);
		} else if (STRING_OFFSET <= settingId && settingId < END_OF_SETTINGS_MARKER) {
			String stringToWrite = (String)setting;
			outputStream.writeUTF(stringToWrite);
		}
	}
	
	private static void createDefaultSettings(DataOutputStream outputStream) throws IOException {
		for (int i = 0; i < DEFAULT_BOOLEAN_COUNT; i++) {
			writeSetting(i+BOOLEAN_OFFSET, 0, true, outputStream);
		}
		for (int i = 0; i < DEFAULT_INTEGER_COUNT; i++) {
			writeSetting(i+INTEGER_OFFSET, 0, 0, outputStream);
		}
		for (int i = 0; i < DEFAULT_STRING_COUNT; i++) {
			writeSetting(i+STRING_OFFSET, 0, "", outputStream);
		}
		for (int i = 0; i < DEFAULT_INT64_COUNT; i++) {
			writeSetting(i+INT64_OFFSET, 0, BigInteger.ZERO, outputStream);
		}
	}
	
	private static BigInteger readBigInteger(DataInputStream stream) throws IOException {
        byte[] byteArray = new byte[8];
        stream.read(byteArray);
        return new BigInteger(1, byteArray); 
	}
	
	private static void writeBigInteger(DataOutputStream stream, BigInteger value) throws IOException {
		byte[] bigIntegerAsByteArray = ((BigInteger)value).toByteArray();
        byte[] byteArray = new byte[8];
        System.arraycopy(bigIntegerAsByteArray, 0, byteArray, 0, bigIntegerAsByteArray.length);
		stream.write(byteArray);
	}
}
