/**
 * 
 *  Copyright 2012 Birkett Enterprise Ltd
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

package com.birkettenterprise.phonelocator.settings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.birkettenterprise.phonelocator.application.PhonelocatorApplication;
import com.birkettenterprise.phonelocator.utility.StringUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import android.util.Log;


public class EnvironmentalSettingsSetter {
	
	public static final String LOG_TAG = PhonelocatorApplication.LOG_TAG + "_ENVIRONMENTAL_SETTINGS";

	public static Pattern mPattern = Pattern.compile("(\\d+)\\.(\\d+)");
	
	public static void updateEnvironmentalSettingsIfRequired(SharedPreferences sharedPreferences, Context context) {
	    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		setIMEIIfRequired(sharedPreferences, telephonyManager);
		setIMSIIfRequiredAndDetectSIMCardChanged(sharedPreferences, telephonyManager);
		
		try {
			EnvironmentalSettingsSetter.setVersionIfRequired(
					sharedPreferences,
					context.getPackageManager().getPackageInfo(
							context.getPackageName(), 0));
		} catch (NameNotFoundException e1) {
		}
		
	}
	
	public static void setIMEIIfRequired(SharedPreferences sharedPreferences, TelephonyManager telephonyManager) {
		String imei = telephonyManager.getDeviceId();
	
		Log.d(LOG_TAG, "imei " + imei);
		SettingsHelper.putStringIfRequired(sharedPreferences, Setting.Integer64Settings.IMEI, imei);
	}
	
	public static void setIMSIIfRequiredAndDetectSIMCardChanged(SharedPreferences sharedPreferences, TelephonyManager telephonyManager) {
		String imsi = telephonyManager.getSubscriberId();
		
		Log.d(LOG_TAG, "imsi " + imsi);
		if (!StringUtil.isNullOrWhiteSpace(imsi) && SettingsHelper.putStringIfRequired(sharedPreferences, Setting.Integer64Settings.IMSI, imsi)) {
			if (SettingsHelper.isBuddyMessageEnabled(sharedPreferences)) {
				SettingsHelper.setSendBuddyMessage(sharedPreferences, true);
			}
		}
	}
	
	public static void setVersionIfRequired(SharedPreferences sharedPreferences, PackageInfo packageInfo) {
		Version version = getVersion(packageInfo);
		SettingsHelper.putIntIfRequired(sharedPreferences, Setting.IntegerSettings.VERSION_MAJOR, version.mMajor);
		SettingsHelper.putIntIfRequired(sharedPreferences, Setting.IntegerSettings.VERSION_MINOR, version.mMinor);
		SettingsHelper.putIntIfRequired(sharedPreferences, Setting.IntegerSettings.VERSION_REVISION, version.mRevision);
	}
	
	static Version getVersion(PackageInfo packageInfo) {
		return getVersion(packageInfo.versionName, packageInfo.versionCode);	
	}
	
	static Version getVersion(String versionName, int versionCode) {
		Version version = new Version();
		version.mRevision = versionCode;
		Matcher matcher = mPattern.matcher(versionName);
		if (matcher.matches()) {
			version.mMajor = Integer.parseInt(matcher.group(1));
			version.mMinor =  Integer.parseInt(matcher.group(2));
		}
		return version;
	}
}
