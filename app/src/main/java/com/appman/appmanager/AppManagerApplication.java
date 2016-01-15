package com.appman.appmanager;

import android.app.Application;

import com.appman.appmanager.utils.AppPreferences;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;


/**
 * Created by rudhraksh.pahade on 03-12-2015.
 */
public class AppManagerApplication extends Application{
    private static AppPreferences sAppPreferences;

    public static AppPreferences getAppPreferences() {
        return sAppPreferences;
    }

    @Override
    public void onCreate() {
        sAppPreferences = new AppPreferences(this);
        super.onCreate();
        Fabric.with(this, new Crashlytics());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
