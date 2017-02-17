package com.appman.appmanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.appman.appmanager.utils.AppPreferences;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;


/**
 * Created by rudhraksh.pahade on 03-12-2015.
 */
public class AppManagerApplication extends Application implements
        Application.ActivityLifecycleCallbacks {

    private static final String TAG = "AppManagerApplication";
    @SuppressLint("StaticFieldLeak")
    private static AppPreferences sAppPreferences;
    @SuppressLint("StaticFieldLeak")
    private static AppManagerApplication mInstance;
    private Activity mCurrentActivity;
    public static AppPreferences getAppPreferences() {
        return sAppPreferences;
    }

    @Override
    public void onCreate() {
        sAppPreferences = new AppPreferences(this);
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mInstance = this;
        mInstance.registerActivityLifecycleCallbacks(this);
    }

    public static synchronized AppManagerApplication getInstance() {return mInstance;}

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        setmCurrentActivity(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        clearReferences();
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        // Close all the instances of Activity
        clearReferences();
    }

    /**
     * GETTER SETTER METHODS FOR GET AND SET CURRENT ACTIVITY INSTANCE
     * @return activity instance
     */
    public Activity getmCurrentActivity() {
        return mCurrentActivity;
    }

    public void setmCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    private void clearReferences(){
        Activity currActivity = getmCurrentActivity();
        if (this.equals(currActivity))
            setmCurrentActivity(null);
    }
}
