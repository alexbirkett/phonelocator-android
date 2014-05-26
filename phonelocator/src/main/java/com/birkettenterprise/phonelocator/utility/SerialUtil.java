package com.birkettenterprise.phonelocator.utility;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.birkettenterprise.phonelocator.application.PhonelocatorApplication;
import com.birkettenterprise.phonelocator.settings.SettingsHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Created by alex on 25/04/14.
 */
public class SerialUtil {

    private static String serial;

    private static String generateSerial() {

        String serial = getTelephonyManager().getDeviceId();
        if (serial == null) {
            serial = Settings.Secure.getString(PhonelocatorApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        if (serial == null) {
            serial = UUID.randomUUID().toString();
        }
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        digest.update(serial.getBytes());
        return Hex.encodeHexString(digest.digest());
    }

    public static String getSerial() {
        if (serial == null) {
            serial = SettingsHelper.getSerial();
        }

        if (serial == null) {
            serial = generateSerial();
            SettingsHelper.setSerial(serial);
        }
        return serial;
    }

    public static TelephonyManager getTelephonyManager() {
        return  (TelephonyManager) PhonelocatorApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
    }
}
