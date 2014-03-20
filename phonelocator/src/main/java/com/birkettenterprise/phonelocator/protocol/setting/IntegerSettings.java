/**
 * 
 *  Copyright 2011-2012 Birkett Enterprise Ltd
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

public class IntegerSettings {
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
