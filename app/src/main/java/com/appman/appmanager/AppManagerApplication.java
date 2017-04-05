package com.appman.appmanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.appman.appmanager.activities.MainActivity;
import com.appman.appmanager.appupdater.AppUpdateAlert;
import com.appman.appmanager.appupdater.Config;
import com.appman.appmanager.service.NotificationAlarmService;
import com.appman.appmanager.utils.AppPreferences;
import com.crashlytics.android.Crashlytics;

import java.util.Calendar;
import java.util.Locale;

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

    private RequestQueue requestQueue = null;

    @Override
    public void onCreate() {
        sAppPreferences = new AppPreferences(this);
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mInstance = this;
        mInstance.registerActivityLifecycleCallbacks(this);
    }

    public static synchronized AppManagerApplication getInstance() {return mInstance;}

    /**
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return requestQueue;
    }

    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        getRequestQueue().add(req);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }

    private void createNotificationAlarm() {
        Log.e(TAG, "called createNotificationAlarm()");
        if (AppUpdateAlert.alarmManager == null) {
            AppUpdateAlert.alarmManager = (AlarmManager) AppUpdateAlert.alarmActivity.getSystemService(ALARM_SERVICE);
        }
        Intent intent = new Intent(AppUpdateAlert.alarmActivity, NotificationAlarmService.class);
        AppUpdateAlert.pendingIntent = PendingIntent.getService(AppUpdateAlert.alarmActivity, 0, intent, 0);
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 1);
        if (AppUpdateAlert.alarmManager != null) {
            AppUpdateAlert.alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AppUpdateAlert.pendingIntent);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        // Creating notification for update reminder, only if user has opted to perform later
        if (activity instanceof MainActivity) {
            if (activity != null) {
                if (Config.HAS_IGNORED_FOR_UPDATES) {
                    AppUpdateAlert.alarmActivity = (AppCompatActivity) activity;
                    createNotificationAlarm();
                }
            } else {
                Log.e(TAG, "MainActivity is not instantiated");
            }
        } else {
            Log.e(TAG, "MainActivity is not instantiated");
        }
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
