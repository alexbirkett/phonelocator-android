/**
 *
 *  Copyright 2011-2014 Birkett Enterprise Ltd
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

import com.birkettenterprise.phonelocator.application.PhonelocatorApplication;
import com.birkettenterprise.phonelocator.broadcastreceiver.PollLocationAndSendUpdateBroadcastReceiver;
import com.birkettenterprise.phonelocator.settings.Setting.BooleanSettings;
import com.birkettenterprise.phonelocator.settings.Setting.Integer64Settings;
import com.birkettenterprise.phonelocator.settings.Setting.LongSettings;
import com.birkettenterprise.phonelocator.settings.Setting.StringSettings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

public class SettingsHelper {

    private static final String TAG = "Phonelocator";

    public static final long DEFAULT_UPDATE_FREQUENCY = 60 * 10;
    public static final long DEFAULT_GPS_TIMEOUT = 30000;

    private static final int MILLISECONDS_IN_SECOND = 1000;

    public static void scheduleUpdates(Context context) {
        AlarmManager alamManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(PollLocationAndSendUpdateBroadcastReceiver.ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (isPeriodicUpdatesEnabled()) {
            long intervalInMicroseconds = getUpdateFrequencyInMilliSeconds();
            alamManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    intervalInMicroseconds, pendingIntent);
            Log.d(TAG, "updates scheduled every " + intervalInMicroseconds + " microseconds");
        } else {
            alamManager.cancel(pendingIntent);
            Log.d(TAG, "canceled updates");

        }
    }

    public static long getUpdateFrequencyInMilliSeconds() {
        return getUpdateFrequencyInSeconds() * MILLISECONDS_IN_SECOND;
    }

    public static long getUpdateFrequencyInSeconds() {
        return getSettingAsLong(Setting.StringSettings.UPDATE_FREQUENCY, DEFAULT_UPDATE_FREQUENCY);
    }

    public static void setUpdateFrequencyInSeconds(long value) {
        putStringIfRequired(Setting.StringSettings.UPDATE_FREQUENCY, Long.toString(value));
    }

    public static long getGpsTimeOut() {
        return getSettingAsLong(Setting.StringSettings.GPS_UPDATE_TIMEOUT, DEFAULT_GPS_TIMEOUT);
    }

    public static void setGpsTimeOut(long value) {
        putStringIfRequired(Setting.StringSettings.GPS_UPDATE_TIMEOUT, Long.toString(value));
    }

    public static long getSettingAsLong(String key, long defaultValue) {
        try {
            return getSharedPreferences().getLong(
                    key,
                    defaultValue);
        } catch (ClassCastException e) {
            String value = getSharedPreferences().getString(
                    key,
                    defaultValue + "");
            return Long.parseLong(value);
        }
    }

    public static void storeResponse(String authenticationToken, String registrationUrl) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(Setting.StringSettings.AUTHENTICATION_TOKEN,
                authenticationToken);
        editor.putString(Setting.StringSettings.REGISTRATION_URL,
                registrationUrl);
        editor.commit();
    }

    public static void setAuthenticationToken(String authenticationToken) {
        putStringIfRequired(StringSettings.AUTHENTICATION_TOKEN, authenticationToken);
    }


    public static String getAuthenticationToken() {
        return getSharedPreferences().getString(StringSettings.AUTHENTICATION_TOKEN, null);
    }

    public static boolean hasAuthenticationToken() {
        return getAuthenticationToken() != null;
    }

    public static String getRegistrationUrl() {
        return getSharedPreferences().getString(StringSettings.REGISTRATION_URL, null);
    }

    public static void setPasscode(String passcode) {
        putStringIfRequired(StringSettings.PINCODE, passcode);
    }

    public static String getPasscode() {
        return getSharedPreferences().getString(StringSettings.PINCODE, null);
    }

    public static void setBuddyTelephoneNumber(String buddyTelephoneNumber) {
        putStringIfRequired(StringSettings.BUDDY_TELEPHONE_NUMBER, buddyTelephoneNumber);
    }

    public static String getBuddyTelephoneNumber() {
        return getSharedPreferences().getString(StringSettings.BUDDY_TELEPHONE_NUMBER, null);
    }

    public static String getImsi() {
        return getSharedPreferences().getString(Integer64Settings.IMSI, null);
    }

    public static void setImsi(String imsi) {
        putStringIfRequired(Integer64Settings.IMSI, imsi);
    }

    public static void setBuddyMessage(String buddyMessage) {
        putStringIfRequired(StringSettings.BUDDY_MESSAGE, buddyMessage);
    }

    public static String getBuddyMessage() {
        return getSharedPreferences().getString(StringSettings.BUDDY_MESSAGE, null);
    }

    public static boolean isPeriodicUpdatesEnabled() {
        return getSharedPreferences().getBoolean(BooleanSettings.PERIODIC_UPDATES_ENABLED, false);
    }

    public static void setPeriodicUpdatesEnabled(boolean enabled) {
        storeBoolean(BooleanSettings.PERIODIC_UPDATES_ENABLED, enabled);
    }

    public static boolean isRegistered() {
        return getSharedPreferences().getBoolean(BooleanSettings.REGISTERED, false);
    }

    public static void setHideTriggerMessage(boolean hideTriggerMessages) {
        storeBoolean(BooleanSettings.HIDE_SMS_TRIGGER, hideTriggerMessages);
    }

    public static boolean isHideTriggerMessage() {
        return getSharedPreferences().getBoolean(BooleanSettings.HIDE_SMS_TRIGGER, false);
    }

    public static boolean isPasscodeEnabled() {
        return getSharedPreferences().getBoolean(BooleanSettings.PINCODE_REQUIRED_ON_STARTUP, false);
    }

    public static void setSendBuddyMessage(boolean sendBuddyMessage) {
        storeBoolean(BooleanSettings.SEND_BUDDY_MESSAGE, sendBuddyMessage);
    }

    public static boolean isSendBuddyMessage() {
        return getSharedPreferences().getBoolean(BooleanSettings.SEND_BUDDY_MESSAGE, false);
    }

    public static void setBuddyMessageEnabled(boolean enabled) {
        storeBoolean(BooleanSettings.BUDDY_MESSAGE_ENABLED, enabled);
    }

    public static boolean isBuddyMessageEnabled() {
        return getSharedPreferences().getBoolean(BooleanSettings.BUDDY_MESSAGE_ENABLED, true);
    }

    public static boolean isGpsEnabled() {
        return getSharedPreferences().getBoolean(BooleanSettings.GPS_ENABLED, true);
    }

    public static long getLastUpdateTimeStamp() {
        return getSharedPreferences().getLong(LongSettings.LAST_UPDATE_TIME_STAMP, 0L);
    }

    public static void setLastUpdateTimeStamp(long value) {
        storeLong(LongSettings.LAST_UPDATE_TIME_STAMP, value);
    }

    public static void setTrackerName(String name) {
        putStringIfRequired(StringSettings.TRACKER_NAME, name);
    }

    public static String getTrackerName() {
        return getSharedPreferences().getString(StringSettings.TRACKER_NAME, null);
    }

    public static void storeLong(String key, long value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void storeBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean putBooleanIfRequired(SharedPreferences.Editor editor, String name, boolean value) {
        boolean changed = false;
        if (!getSharedPreferences().contains(name)
                || getSharedPreferences().getBoolean(name, false) != value) {
            editor.putBoolean(name, value);
            changed = true;
        }
        return changed;
    }

    public static boolean putIntIfRequired(SharedPreferences.Editor editor, String name, int value) {
        boolean changed = false;
        if (!getSharedPreferences().contains(name)
                || getSharedPreferences().getInt(name, 0) != value) {
            editor.putInt(name, value);
            changed = true;
        }
        return changed;
    }

    public static boolean putStringIfRequired(SharedPreferences.Editor editor, String name, String value) {
        boolean changed = false;
        if (!getSharedPreferences().contains(name)
                || !getSharedPreferences().getString(name, "").equals(value)) {
            editor.putString(name, value);
            changed = true;
        }
        return changed;
    }

    public static boolean putStringIfRequired(String key, String value) {
        boolean changed = false;
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        if (putStringIfRequired(editor, key, value)) {
            editor.commit();
            changed = true;
        }
        return changed;
    }

    public static boolean putIntIfRequired(String key, int value) {
        boolean changed = false;
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        if (putIntIfRequired(editor, key, value)) {
            editor.commit();
            changed = true;
        }
        return changed;
    }

    private static SharedPreferences getSharedPreferences() {
        return PhonelocatorApplication.getInstance().getSharedPreferences();
    }

}
