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

public class SettingNameResolver {

	public static String getSettingsNameForId(int settingsId) throws UnknowSettingException {
	
		try {
			 return getBooleanSettingNameForSettingId(settingsId);
		} catch (UnknowSettingException e) {
			try {
				return getIntSettingIdForSettingName(settingsId);
			} catch (UnknowSettingException e1) {
				return getStringSettingNameForSettingId(settingsId);
			}
		}
	}
	
	private static String getBooleanSettingNameForSettingId(int settingId) throws UnknowSettingException {

		settingId -= SettingsOffsets.BOOLEAN_OFFSET;
		
		if (settingId == BooleanSettings.PERIODIC_UPDATE_ENABLED) {
			return Setting.BooleanSettings.PERIODIC_UPDATES_ENABLED;
		} else if (settingId == BooleanSettings.REGISTERED) {
			return Setting.BooleanSettings.REGISTERED;
		} else if (settingId == BooleanSettings.PINCODE_REQUIRED_ON_STARTUP) {
			return Setting.BooleanSettings.PINCODE_REQUIRED_ON_STARTUP;
		} else if (settingId == BooleanSettings.SENDBUDDY_MESSAGE) {
			return Setting.BooleanSettings.SEND_BUDDY_MESSAGE;
		} else if (settingId == BooleanSettings.BUDDY_MESSAGE_ENABLED) {
			return Setting.BooleanSettings.BUDDY_MESSAGE_ENABLED;
		} else if (settingId == BooleanSettings.GPS_ENABLED) {
			return Setting.BooleanSettings.GPS_ENABLED;
		} else {
			throw new UnknowSettingException(settingId);
		}
	}
	
	private static String getIntSettingIdForSettingName(int settingId) throws UnknowSettingException {
		settingId -= SettingsOffsets.INTEGER_OFFSET; 
		if (settingId == IntegerSettings.UPADATE_FREQUENCY) {
			return Setting.StringSettings.UPDATE_FREQUENCY;
		} else {
			throw new UnknowSettingException(settingId);
		}
	}
	
	private static String getStringSettingNameForSettingId(int settingId) throws UnknowSettingException {

		settingId -= SettingsOffsets.STRING_OFFSET; 
		if (settingId == StringSettings.AUTHENTICATION_TOKEN) {
			return Setting.StringSettings.AUTHENTICATION_TOKEN;
		} else if (settingId == StringSettings.PINCODE) {
			return Setting.StringSettings.PASSCODE;
		} else if (settingId == StringSettings.BUDDY_TELEPHONE_NUMBER) {
			return Setting.StringSettings.BUDDY_TELEPHONE_NUMBER;
		} else if (settingId == StringSettings.BUDDY_MESSAGE) {
			return Setting.StringSettings.BUDDY_MESSAGE;
		} else {
			throw new UnknowSettingException(settingId); 
		}
	}
	
}
