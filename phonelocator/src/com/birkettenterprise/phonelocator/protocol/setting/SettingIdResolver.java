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

import com.birkettenterprise.phonelocator.settings.Setting;

public class SettingIdResolver {
	
	public static int getSettingsIdForName(String settingName) throws UnknowSettingException {
		try {
			return getInt64SettingIdForSettingName(settingName);
		} catch (UnknowSettingException e) {
			try {
				return getBooleanSettingIdForSettingName(settingName);
			} catch (UnknowSettingException e1) {
				try {
					return getIntSettingIdForSettingName(settingName);
				} catch (UnknowSettingException e2) {
					return getStringSettingIdForSettingName(settingName);
				}
			}
		}
	}
	
	private static int getInt64SettingIdForSettingName(String settingName) throws UnknowSettingException {
		int settingId = 0;		
		if (settingName.equals(Setting.Integer64Settings.IMEI)) {
			settingId = Integer64Settings.IMEI;
		} else if (settingName.equals(Setting.Integer64Settings.IMSI)) {
			settingId = Integer64Settings.IMSI;
		} else {
			throw new UnknowSettingException(settingName);			
		}
		return settingId + SettingsOffsets.INT64_OFFSET;
	}
    
	private static int getBooleanSettingIdForSettingName(String settingName) throws UnknowSettingException {

		int settingId = 0;
		if (Setting.BooleanSettings.PERIODIC_UPDATES_ENABLED.equals(settingName)) {
			settingId = BooleanSettings.PERIODIC_UPDATE_ENABLED;
		} else if (Setting.BooleanSettings.REGISTERED.equals(settingName)) {
			settingId = BooleanSettings.REGISTERED;
		} else if (Setting.BooleanSettings.PINCODE_REQUIRED_ON_STARTUP.equals(settingName)) {
			settingId = BooleanSettings.PINCODE_REQUIRED_ON_STARTUP;
		} else if (Setting.BooleanSettings.SEND_BUDDY_MESSAGE.equals(settingName)) {
			settingId = BooleanSettings.SENDBUDDY_MESSAGE;
		} else if (Setting.BooleanSettings.BUDDY_MESSAGE_ENABLED.equals(settingName)) {
			settingId = BooleanSettings.BUDDY_MESSAGE_ENABLED;
		} else if (Setting.BooleanSettings.GPS_ENABLED.equals(settingName)) {
			settingId = BooleanSettings.GPS_ENABLED;
		} else {
			throw new UnknowSettingException(settingName);
		}
		return settingId + SettingsOffsets.BOOLEAN_OFFSET;
		
	}
	private static int getIntSettingIdForSettingName(String settingName) throws UnknowSettingException {
		int settingId = 0;
		// update frequency is stored locally as a string and remotely as an int
		if (Setting.StringSettings.UPDATE_FREQUENCY.equals(settingName)) {
			settingId = IntegerSettings.UPADATE_FREQUENCY;
		} else if (Setting.StringSettings.GPS_UPDATE_TIMEOUT.equals(settingName)) {
			settingId = IntegerSettings.GPS_UPDATE_TIMEOUT;
		} else if (Setting.IntegerSettings.VERSION_MAJOR.equals(settingName)) {
			settingId =  IntegerSettings.VERSION_MAJOR;
		} else if (Setting.IntegerSettings.VERSION_MINOR.equals(settingName)) {
			settingId = IntegerSettings.VERSION_MINOR;
		} else if (Setting.IntegerSettings.VERSION_REVISION.equals(settingName)) {
			settingId = IntegerSettings.VERSION_REVISION;
		} else {
			throw new UnknowSettingException(settingName);
		}
		return settingId + SettingsOffsets.INTEGER_OFFSET;
	}
	
	private static int getStringSettingIdForSettingName(String settingName) throws UnknowSettingException {

		int settingId = 0;
		
		if (Setting.StringSettings.AUTHENTICATION_TOKEN.equals(settingName)) {
			settingId = StringSettings.AUTHENTICATION_TOKEN;
		} else if (Setting.StringSettings.PINCODE.equals(settingName)) {
			settingId = StringSettings.PINCODE;
		} else if (Setting.StringSettings.BUDDY_TELEPHONE_NUMBER.equals(settingName)) {
			settingId = StringSettings.BUDDY_TELEPHONE_NUMBER;
		} else if (Setting.StringSettings.BUDDY_MESSAGE.equals(settingName)) {
			settingId = StringSettings.BUDDY_MESSAGE;
		} else {
			throw new UnknowSettingException(settingName); 
		}
		return settingId + SettingsOffsets.STRING_OFFSET;
	}

}
