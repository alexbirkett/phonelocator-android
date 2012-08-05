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
import com.birkettenterprise.phonelocator.settings.Setting;

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

	
	/**
	 * Some settings are a legacy of the Symbian OS client and are included here for completeness
	 * @author alex
	 *
	 */
	private class BooleanSettings {
		public static final int AUTOSTART = 0; /* 00 - start the phonelocator service when the phone starts */
		public static final int LOCATION_PUBLIC = 1; /* 01 - location public if true */
		public static final int UNUSED = 2; /* 02 */
		public static final int PINCODE_REQUIRED_ON_STARTUP = 3; /* 03 - pin code required on application start and un-install if true */
		public static final int SENDBUDDY_MESSAGE = 4; /* 04 - set true when SIM card changed. This should not be set remotely */
		public static final int PERIODIC_UPDATE_ENABLED = 5; /* 05 - updates sent periodically if true */
		public static final int REGISTERED = 6; /* 06 - set true after registration process is complete */
		public static final int TRY_ALL_ACCESS_POINTS = 7; /* 07 - network connection is attempted using all access points if true */
		public static final int SHOW_URL_1 = 8; /* 08 - URL 1 is shown on options menu if true */
		public static final int SHOW_URL_2 = 9; /* 09 - URL 2 is shown on options menu if true */
		public static final int PINCODE_REQUIRED_IN_SMS_MESSAGE = 10; /* 10 - 'update message must contain a the pin code if true e.g. "update 4321" if pin code (EPinCode) is 4321 */
		public static final int PINCODE_SETTING_ENABLED = 11; /* 11 - pin code setting is shown on settings pane if true */
		public static final int SMS_UPDATE_TRIGGER_ENABLED = 12; /* 12 - application responds to 'update message' (an SMS message that starts with the word 'update')  by sending an update (and synchronizing settings) with the server if true*/
		public static final int BUDDY_MESSAGE_ENABLED = 13; /* 13 - application sends contents of  EBuddyMessage as an SMS to EBuddyTelephoneNumber when SIM card is changed if true */
		public static final int CLIENT_DISABLED = 14; /* 14 - application UI won't start, instead EClientDisabledMessage is displayed and user offered the option to open  EClientDisabledUrl if true */
		public static final int TRY_ALL_ACCESS_POINTS_SETTING_ENABLED = 15; /* 15 - try all access setting is shown on settings pane if true */
		public static final int BUDDY_MESSAGE_SETTING_ENABLED = 16; /* 16 - buddy message is shown on settings pane if true */
		public static final int SYNCHRONIZATION_SETTING_ENABLED = 17; /* 17 - unused */
		public static final int WARN_NOT_AUTOSTARTING = 18; /* 18 warning message displayed when EAutoStart is set to false if true*/
		public static final int WARN_AUTO_STARTING = 19; /* 19 warning message displayed when EAutoStart is set to true if true */
		public static final int HOST_AND_PORT_SETTING_ENABLED = 20; /* 20 host and port setting is shown on settings pane if true */
		public static final int CONFIRM_LOCATION_MESSAGE = 21; /* 21 unused */
		public static final int LOCAITON_MESSAGE_ENABLED = 22; /* 22 unused */
		public static final int USE_ALTERNATIVE_UPDATE_FREQUENCY_WHEN_POWER_CONNECTED = 23; /* 23 EUpdateFrequencyWhenPowerConnected is used instead of EUpdateFrequency if true */
		public static final int SET_STATUS_MENU_OPTION_ENABLED = 24; /* 24 unused */
		public static final int MESSAGE_VIEW_ENABLED = 25; /* 25 unused */
		public static final int WLAN_SCANNER_ENABLED = 26;/* 26 wlan beacons are sent with update if enabled */
		public static final int CELL_SCANNER_ENABLED = 27; /* 27 cell beacons are sent with update if enabled */
		public static final int USE_SIMPLE_BEACONS = 28; /* 28 simple GPS, wlan and cell data format is used if true */
		public static final int ALERT_ACTIVE = 29; /* 29 When set to true, a popup will be displayed showing EMessage and the alert sound will be played  EAlertSoundRepeats times. When the popup is dismissed, EAlertActive will be set to false*/
	}
	
	private class IntegerSettings {
		public static final int VERSION_MAJOR = 0; /* 0 client version major (read only) */
		public static final int VERSION_MINOR = 1; /* 1 client version minor (read only) */
		public static final int VERSION_REVISION = 2; /* 2 client version minor(read only) */
		public static final int UPADATE_FREQUENCY = 3;  /* 3 update frequency (how often updates are sent to the server) measured in seconds */
		public static final int IAP_ID = 4; /* 4 the id of the access point used to connect to the server - this setting will differ on a phone by phone basis, it's therefore unwise to attempt to set it from the server*/
		public static final int RESERVED = 5;
		public static final int CONNECTION_TIMEOUT = 6; /* 6 length of time the access point is held open after all data has been sent measured in seconds */
		public static final int GPS_SESSION_TIMEOUT = 7; /* 7 length of time the GPS session is held open after becoming idle measured in seconds */
		public static final int GPS_UPDATE_TIMEOUT = 8; /* 8 length of time to wait for a GPS lock before giving up measured in micro seconds (see EGpsLowSignalThreshold) */
		public static final int GPS_UPDATE_LOWSIGNAL_TIMEOUT = 9; /* 9 length of time to wait for a GPS lock before giving up when GPS signal is low (see EGpsLowSignalThreshold) */
		public static final int GPS_POLL_INTERVAL = 10; /* 10 GPS poll interval measured in microseconds. (How often to poll the GPS hardware when a GPS session is active) It's advisable not to alter this setting*/
		public static final int GPS_LOW_SIGNAL_THRESHOLD = 11; /* 11 when strongest EGpsMinGoodSatelliteCount number of satellites are below EGpsLowSignalThreshold EGpsUpdateLowSignalTimeout is used instead of EGpsUpdateTimeout */
		public static final int GPS_MINGOODSATELLITECOUNT = 12; /* 12 see EGpsLowSignalThreshold */
		public static final int TRANSACTION_CONTROLLER_TIMEOUT = 13; /* 13 length of time to wait for a TCP method to complete measured in seconds */
		public static final int LANGUAGE = 14; /* unused */
		public static final int VARIANT = 15; /* unused */
		public static final int PORT_NUMBER = 16;  /* 16 server port number */
		public static final int POSITIONING_MODULE_UID = 17; /* 17 Symbian OS positioning module id It's advisable not to alter this setting unless you know the id you're setting is valid for any particular handset */
		public static final int SMS_GPS_REST_TIME = 18; /* 18 unsued */
		public static final int SMS_SENDING_FAILED_RETRY_TIMEOUT = 19; /* 19 length of time to wait before attempting to retry sending an SMS (e.g. buddy message SMS) after sending failure measured in seconds */
		public static final int GPS_LOCK_WAIT_TIME = 20; /* 20 length of time to wait after acquiring GPS lock before sending update measured in microseconds  */
		public static final int UPDATE_FREQUENCY_WHEN_POWER_CONNECTED = 21; /* 21 update frequency, measured in seconds, when power is connected and EUseAlternativeUpdateFrequencyWhenPowerConnected is set true*/
		public static final int STATUS = 22; /* unused */
		public static final int ALERT_SOUND_REPEATS = 22; /* 23 number of times to repeat alert sound (see EAlertActive) */
	}
		
	private class Integer64Settings {
		public static final int IMEI = 0;
		public static final int IMSI = 1;
	}
	
	 
	private class StringSettings {
		public static final int AUTHENTICATION_TOKEN = 0; /* 0 authentication token*/
		public static final int FIRMWARE_VERSON = 1; /* 1 phone firmware version (read only) */
		public static final int MODEL_NAME = 2; /* 2 phone model name read only */
		public static final int BUDDY_TELEPHONE_NUMBER = 3; /* 3 buddy telephone number */
		public static final int BUDDY_MESSAGE = 4; /* 4 buddy message */
		public static final int URL1 = 5; /* 5 Url 1 see EShowUrl1 */
		public static final int URL2 = 6; /* 6 Url 1 see EShowUrl2 */
		public static final int URL1_NAME = 7; /* 7 Url 1 name (text displayed on menu) see EShowUrl1 */
		public static final int URL2_NAME = 8; /* 8 Url 2 name (text displayed on menu) see EShowUrl2 */
		public static final int CLIENT_DISABLED_MESSAGE = 9; /* 9 message displayed when client is disabled */
		public static final int CLIENT_DISABLED_URL = 10; /* 10 url to offer to user when client is disabled */
		public static final int HOSTNAME = 11;  /* 11 server host name */
		public static final int PINCODE = 12; /* 12 pin code (should be numeric) */
		public static final int MESSAGE = 13; /* 13 message to display EAlertActive */
	};
	
	public static void writeSettings(Vector<Setting> settings,
			DataOutputStream outputStream) throws IOException {
		outputStream.writeByte(PROTOCOL_VERSION_2);

		for (Setting setting : settings) {
			try {
				int settingId = getSettingIdForSettingName(setting.getName());
				writeSetting(settingId, setting.getTimestamp(),
						setting.getValue(), outputStream);
			} catch (UnknowSettingException e) {
				// ignore unknown settings
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

	private static int getSettingIdForSettingName(String settingName) throws UnknowSettingException {
		if (settingName.equals(Setting.BooleanSettings.PERIODIC_UPDATES_ENABLED)) {
			return BooleanSettings.PERIODIC_UPDATE_ENABLED + BOOLEAN_OFFSET;
		} else if (settingName.equals(Setting.BooleanSettings.REGISTERED)) {
			return BooleanSettings.REGISTERED + BOOLEAN_OFFSET;
		} else if (settingName.equals(Setting.StringSettings.UPDATE_FREQUENCY)) {
			return IntegerSettings.UPADATE_FREQUENCY + INTEGER_OFFSET;
		} else if (settingName.equals(Setting.Integer64Settings.IMEI)) {
			return Integer64Settings.IMEI + INT64_OFFSET;
		} else if (settingName.equals(Setting.Integer64Settings.IMSI)) {
			return Integer64Settings.IMSI + INT64_OFFSET;
		} else if (settingName.equals(Setting.IntegerSettings.VERSION_MAJOR)) {
			return IntegerSettings.VERSION_MAJOR + INTEGER_OFFSET;
		} else if (settingName.equals(Setting.IntegerSettings.VERSION_MINOR)) {
			return IntegerSettings.VERSION_MINOR + INTEGER_OFFSET;
		} else if (settingName.equals(Setting.IntegerSettings.VERSION_REVISION)) {
			return IntegerSettings.VERSION_REVISION + INTEGER_OFFSET;
		}
		throw new UnknowSettingException();
	}
	
	private static String getSettingsNameForId(int settingsId) throws UnknowSettingException {
		if (settingsId == BooleanSettings.PERIODIC_UPDATE_ENABLED + BOOLEAN_OFFSET) {
			return Setting.BooleanSettings.PERIODIC_UPDATES_ENABLED;
		} else if (settingsId == BooleanSettings.REGISTERED + BOOLEAN_OFFSET) {
			return Setting.BooleanSettings.REGISTERED;
		} else if (settingsId ==  IntegerSettings.UPADATE_FREQUENCY + INTEGER_OFFSET) {
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
		Log.d(LOG_TAG, "written setting id: " + settingId + " value: " + value + " timestamp: " + timeStamp);

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
			
			BigInteger bigIntegerToWrite = BigInteger.ZERO;
			if (setting instanceof String) {
				bigIntegerToWrite = new BigInteger((String)setting);
			} else if (setting instanceof BigInteger){
				bigIntegerToWrite = (BigInteger)setting;
			}
			writeBigInteger(outputStream, bigIntegerToWrite);
			
		} else if (STRING_OFFSET <= settingId && settingId < END_OF_SETTINGS_MARKER) {
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
