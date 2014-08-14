package com.birkettenterprise.phonelocator.settings;

/**
 * Set default settings to be synchronized with server during registration
 */
public class DefaultSettingsSetter {

	
	public static void setDefaultSettings() {

        if (SettingsHelper.getGpsTimeOut() == 0) {
            SettingsHelper.setGpsTimeOut(SettingsHelper.DEFAULT_GPS_TIMEOUT);
        }

        if (SettingsHelper.getUpdateFrequencyInSeconds() == 0) {
            SettingsHelper.setUpdateFrequencyInSeconds(SettingsHelper.DEFAULT_UPDATE_FREQUENCY);
        }

        if (!SettingsHelper.isGpsEnabledSettingSet()) {
            SettingsHelper.setGpsEnabled(true);
        }
	}
}
