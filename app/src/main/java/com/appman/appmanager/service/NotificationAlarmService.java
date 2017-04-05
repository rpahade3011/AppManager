package com.appman.appmanager.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
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
    private static final long ALARM_TIME_WHEN = 60000;
    private static final String NO_UPDATE_ACTION = "CANCEL_UPDATE";
    private static final int BROADCAST_REQUEST_CODE = 12345;

    private Context mContext = null;
    private AppCompatActivity currentActivity = null;

    private NotificationManager notificationManager = null;

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
            notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
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
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("LongLogTag")
    private void showAppUpdateReminderNotification() {
        Log.e(LOG_TAG, "called showAppUpdateReminderNotification()");
        if (ALARM_TIME_WHEN == System.currentTimeMillis()) {
            long when = System.currentTimeMillis();

            Intent updateIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(Config.ROOT_PLAY_STORE_DEVICE + currentActivity.getPackageName()));
            PendingIntent updatePendingIntent = PendingIntent.getActivity(mContext, 0, updateIntent, 0);

            Intent cancelIntent = new Intent();
            cancelIntent.setAction(NO_UPDATE_ACTION);
            PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(mContext, BROADCAST_REQUEST_CODE, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            android.support.v4.app.NotificationCompat.Action cancelAction = new android.support.v4.app.NotificationCompat.Action(R.drawable.ic_close_black_24dp, "Not Now", cancelPendingIntent);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            android.support.v4.app.NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(mContext)
                    .setContentTitle(currentActivity.getResources().getString(R.string.app_name) + " update reminder")
                    .setContentText("An update is available to download")
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setAutoCancel(false)
                    .setSound(defaultSoundUri)
                    .setWhen(when)
                    .addAction(R.drawable.ic_file_download_black_24dp, "Update Now", updatePendingIntent)
                    .addAction(cancelAction);

            notificationManager.notify(NOTIFICATION_ID, notiBuilder.build());
        }
    }

    public class CancelAlarmNotificationReceiver extends BroadcastReceiver {

        @SuppressLint("LongLogTag")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (NO_UPDATE_ACTION.equals(action)) {
                Log.e(LOG_TAG + ".CancelAlarmNotificationReceiver", "Detected cancel notification action");
                if (AppUpdateAlert.alarmManager != null) {
                    AppUpdateAlert.alarmManager.cancel(AppUpdateAlert.pendingIntent);
                }

                if (notificationManager != null) {
                    notificationManager.cancel(NOTIFICATION_ID);
                }
            }
        }
    }
}
