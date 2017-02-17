package com.appman.appmanager.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.provider.Settings;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by rudhraksh.pahade on 2/17/2017.
 */

public class AdModDeviceIdUtils {

    private static final String TAG = "AdModDeviceIdUtils";

    public static String getAdMobDeviceId(Activity activity) {
        String androidId = getDeviceId(activity);
        return getMessageDigest(androidId).toUpperCase();
    }

    private static String getDeviceId(Activity activity) {
        @SuppressLint("HardwareIds")
        String deviceId = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return deviceId;
    }

    private static String getMessageDigest(final String android_id) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance("MD5");
            digest.update(android_id.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Exception - " + e.getMessage());
        }
        return "";
    }
}
