package com.birkettenterprise.phonelocator.utility;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.birkettenterprise.phonelocator.application.PhonelocatorApplication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by alex on 25/04/14.
 */
public class SerialUtil {

    private static String serial;

    private static String generateSerial() {

        String deviceId = getTelephonyManager().getDeviceId();
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        digest.update(deviceId.getBytes());
        return Hex.encodeHexString(digest.digest());
    }

    public static String getSerial() {
        if (serial == null) {
            serial = generateSerial();
        }
        return serial;
    }

    public static TelephonyManager getTelephonyManager() {
        return  (TelephonyManager) PhonelocatorApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
    }
}
