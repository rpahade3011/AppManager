package com.appman.appmanager;

import android.app.Application;

import com.appman.appmanager.utils.AppPreferences;


/**
 * Created by rudhraksh.pahade on 03-12-2015.
 */
public class AppManagerApplication extends Application{
    private static AppPreferences sAppPreferences;

    @Override
    public void onCreate() {
        sAppPreferences = new AppPreferences(this);
        super.onCreate();
    }

    public static AppPreferences getAppPreferences() {
        return sAppPreferences;
    }

    /**
     * Retrieve ML Manager Pro
     * @return true for ML Manager Pro, false otherwise

    public static Boolean isPro() {
        return false;
    }

    public static String getProPackage() {
        return "com.appman.appmanager";
    }*/
}
