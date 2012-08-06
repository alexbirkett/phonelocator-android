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


/**
 * Some settings are a legacy of the Symbian OS client and are included here for completeness
 * @author alex
 *
 */
public class BooleanSettings 	{
	
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
	public static final int GPS_ENABLED = 30; /*  30 added first to Android Phonelocator */

}
