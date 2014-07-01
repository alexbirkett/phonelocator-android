package com.birkettenterprise.phonelocator.utility;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.birkettenterprise.phonelocator.application.PhonelocatorApplication;

/**
 * Created by alex on 01/07/14.
 */
public class VersionUtil {

    static String version;

    public static String getVersion() {
        if (version == null) {
            Context context = PhonelocatorApplication.getInstance();
            PackageInfo pInfo = null;
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                version = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return version;
    }
}
