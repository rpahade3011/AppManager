package com.appman.appmanager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.appman.appmanager.utils.TypefaceUtil;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;


/**
 * Created by rudhraksh.pahade on 03-12-2015.
 */
public class AppManagerApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private static AppManagerApplication mInstance;
    private Activity mCurrentActivity;


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        TypefaceUtil.overrideFont(this, "SERIF", "fonts/Avenir-Book.ttf");
        TypefaceUtil.overrideFont(this, "MONOSPACE", "fonts/Avenir-Book.ttf");
        TypefaceUtil.overrideFont(this, "DEFAULT", "fonts/Avenir-Book.ttf");
        mInstance = this;
        mInstance.registerActivityLifecycleCallbacks(this);
    }
    /**
     * To avoid crashing of crashlytics below version Lollipop
     *
     * @param base Context - context for application-specific operations
     */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * Gets instance of current application class.
     *
     * @return the instance
     */
    public static synchronized AppManagerApplication getInstance() {
        return mInstance;
    }

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
        setCurrentActivity(activity);
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
    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    private void clearReferences(){
        Activity currActivity = getCurrentActivity();
        if (this.equals(currActivity))
            setCurrentActivity(null);
    }
}
