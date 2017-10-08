package com.appman.appmanager.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.appman.appmanager.R;
import com.appman.appmanager.appupdater.AppUpdateAlert;
import com.appman.appmanager.appupdater.Config;

/**
 * Created by rudhraksh.pahade on 4/5/2017.
 */

public class NotificationAlarmService extends Service {

    private static final String LOG_TAG = "NotificationAlarmService";
    private static final int NOTIFICATION_ID = 10000;
    private static final String UPDATE_APP_ACTION = "UPDATE_ANYWAY";
    private static final String NO_UPDATE_ACTION = "CANCEL_UPDATE";
    private static final int BROADCAST_REQUEST = 12345;

    private Context mContext = null;
    private AppCompatActivity currentActivity = null;

    private NotificationManager notificationManager = null;
    private AlarmNotificationReceiver alarmNotificationReceiver = null;

    private IBinder notificationServiceBinder = new NotificationAlarmServiceBinder();

    @SuppressLint("LongLogTag")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(LOG_TAG, "called onCreate()");
        if (AppUpdateAlert.alarmActivity != null) {
            this.currentActivity = AppUpdateAlert.alarmActivity;
            this.mContext = currentActivity.getApplicationContext();
        }
        if (notificationManager == null) {
            try {
                notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Unable to create service: " + e.getMessage());
            }
        }

        try {
            registerNotificationReceiver();
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Unable to register receiver: " + ex.getMessage());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showAppUpdateReminderNotification();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return notificationServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onDestroy() {
        try {
            unRegisterNotificationReceiver();
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Unable to un-register receiver: " + ex.getMessage());
        }
        super.onDestroy();
    }

    @SuppressLint("LongLogTag")
    private void showAppUpdateReminderNotification() {
        Log.e(LOG_TAG, "called showAppUpdateReminderNotification()");
        if (notificationManager != null) {
            long when = System.currentTimeMillis();

            // Update Intent
            Intent updateIntent = new Intent();
            updateIntent.setAction(UPDATE_APP_ACTION);
            PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this, BROADCAST_REQUEST, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            android.support.v4.app.NotificationCompat.Action updateAction = new android.support.v4.app.NotificationCompat.Action(R.drawable.ic_file_download_black_24dp, "Update Now",updatePendingIntent);

            // Cancel Intent
            Intent cancelIntent = new Intent();
            cancelIntent.setAction(NO_UPDATE_ACTION);
            PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(mContext, BROADCAST_REQUEST, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            android.support.v4.app.NotificationCompat.Action cancelAction = new android.support.v4.app.NotificationCompat.Action(R.drawable.ic_close_black_24dp, "Not Now", cancelPendingIntent);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            android.support.v4.app.NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(mContext)
                    .setContentTitle(currentActivity.getResources().getString(R.string.app_name) + " update reminder")
                    .setContentText("An update is available to download")
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setAutoCancel(false)
                    .setSound(defaultSoundUri)
                    .setWhen(when)
                    .addAction(updateAction)
                    .addAction(cancelAction)
                    .setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText("An update is available to download").setSummaryText("By: Rudraksh Pahade"));

            notificationManager.notify(NOTIFICATION_ID, notiBuilder.build());
        } else {
            Log.e(LOG_TAG, "Notification service didn't created");
        }
    }

    private void registerNotificationReceiver() throws Exception {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("UPDATE_ANYWAY");
        intentFilter.addAction("CANCEL_UPDATE");
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        alarmNotificationReceiver = new AlarmNotificationReceiver();
        this.registerReceiver(alarmNotificationReceiver, intentFilter);
    }

    private void unRegisterNotificationReceiver() throws Exception {
        if (alarmNotificationReceiver != null) {
            this.unregisterReceiver(alarmNotificationReceiver);
        }
    }

    private void goToMarket() {
        currentActivity.startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(Config.ROOT_PLAY_STORE_DEVICE + currentActivity.getPackageName())));
        currentActivity.finish();
    }

    public class AlarmNotificationReceiver extends BroadcastReceiver {

        @SuppressLint("LongLogTag")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UPDATE_APP_ACTION.equals(action)) {
                Log.e(LOG_TAG + ".UpdateAppNotificationReceiver", "Detected update application action");
                if (AppUpdateAlert.alarmManager != null) {
                    AppUpdateAlert.alarmManager.cancel(AppUpdateAlert.pendingIntent);
                }

                if (notificationManager != null) {
                    notificationManager.cancel(NOTIFICATION_ID);
                }
                onDestroy();
                goToMarket();
            } else if (NO_UPDATE_ACTION.equals(action)) {
                Log.e(LOG_TAG + ".AlarmNotificationReceiver", "Detected cancel notification action");
                if (AppUpdateAlert.alarmManager != null) {
                    AppUpdateAlert.alarmManager.cancel(AppUpdateAlert.pendingIntent);
                }

                if (notificationManager != null) {
                    notificationManager.cancel(NOTIFICATION_ID);
                }
                onDestroy();
            }
        }
    }

    public class NotificationAlarmServiceBinder extends Binder {
        public NotificationAlarmService getBinder() {
            return NotificationAlarmService.this;
        }
    }
}
