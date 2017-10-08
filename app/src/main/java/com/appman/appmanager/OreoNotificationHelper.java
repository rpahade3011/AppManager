package com.appman.appmanager;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;

@TargetApi(26)
public class OreoNotificationHelper extends ContextWrapper {
    private final String LOG_TAG = "OreoNotificationHelper";
    public static final String NOTIFICATION_CHANNEL_ID = "com.appman.appmanager.APPMANAGER";
    private static final String NOTIFICATION_CHANNEL_NAME = "AppManager Channel";

    private static OreoNotificationHelper helper = null;
    private NotificationManager notificationManager = null;

    public static synchronized OreoNotificationHelper getInstance(Context context) {
        if (helper == null) {
            helper = new OreoNotificationHelper(context);
        }
        return helper;
    }
    public OreoNotificationHelper(Context base) {
        super(base);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.setShowBadge(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getNotificationManager().createNotificationChannel(channel);
    }

    private NotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public void notify(int id, Notification.Builder notification) {
        getNotificationManager().notify(id, notification.build());
    }
}