package com.appman.appmanager.appupdater;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.appman.appmanager.activities.ActivitySplash;
import com.appman.appmanager.activities.MainActivity;
import com.appman.appmanager.service.NotificationAlarmService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;
import static com.appman.appmanager.appupdater.Config.PLAY_STORE_HTML_TAGS_TO_DIV_WHATS_NEW_END;
import static com.appman.appmanager.appupdater.Config.PLAY_STORE_HTML_TAGS_TO_DIV_WHATS_NEW_START;
import static com.appman.appmanager.appupdater.Config.PLAY_STORE_WHATS_NEW;


/**
 * Created by rudhraksh.pahade on 3/8/2017.
 */

public class AppUpdateAlert {

    private AppCompatActivity activity;
    public static AlarmManager alarmManager = null;
    public static PendingIntent pendingIntent = null;
    public static AppCompatActivity alarmActivity;
    private boolean isAlarmSet = false;

    public AppUpdateAlert(AppCompatActivity mAct) {
        this.activity = mAct;
    }

    public void showDialog(String response, boolean info, String version) {
        String whatsNew = whatsNew(response);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Update Found");
        alertDialogBuilder.setCancelable(false);
        try {
            String appName = activity.getPackageManager().getApplicationLabel(
                    activity.getPackageManager().getApplicationInfo(
                            activity.getPackageName(), 0)).toString();
            String message = "New Version (" + version + ") Available for " + appName;
            if (info) {
                message = message + "\n" + whatsNew;
            }
            alertDialogBuilder.setMessage(message);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        alertDialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goToMarket();
                Config.HAS_IGNORED_FOR_UPDATES = false;
            }
        });
        alertDialogBuilder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Config.HAS_IGNORED_FOR_UPDATES = true;
                setAlarmForNotification();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        try {
            alertDialog.show();
        } catch (Exception e) {
            Log.v(AppUpdateHandler.TAG, e.toString());
        }
    }

    public String whatsNew(String response) {
        if (!response.toUpperCase().contains(PLAY_STORE_WHATS_NEW)) {
            return "";
        }
        Document doc = Jsoup.parse(response);
        String whatsNew = doc.select("div[class=recent-change]").toString() + "\n";
        whatsNew = whatsNew.replace(PLAY_STORE_HTML_TAGS_TO_DIV_WHATS_NEW_START, "");
        whatsNew = whatsNew.replace(PLAY_STORE_HTML_TAGS_TO_DIV_WHATS_NEW_END, "");
        whatsNew = whatsNew.replaceAll("[\r\n]+", "\n-");
        whatsNew = whatsNew.substring(0, whatsNew.length() - 2);
        return whatsNew;
    }

    private void goToMarket() {
        activity.startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(Config.ROOT_PLAY_STORE_DEVICE + activity.getPackageName())));
        activity.finish();
    }

    private void setAlarmForNotification() {
        if (activity instanceof MainActivity) {
            if (Config.HAS_IGNORED_FOR_UPDATES) {
                if (!isAlarmSet) {
                    AppUpdateAlert.alarmActivity = activity;
                    createNotificationAlarm();
                    isAlarmSet = true;
                } else {
                    Log.e("AppUpdateAlert", "Alarm has already set");
                }
            }
        } else {
            Log.e("AppUpdateAlert", "MainActivity is not instantiated");
        }
    }

    private void createNotificationAlarm() {
        Log.e("AppUpdateAlert", "called createNotificationAlarm()");
        if (AppUpdateAlert.alarmManager == null) {
            AppUpdateAlert.alarmManager = (AlarmManager) AppUpdateAlert.alarmActivity.getSystemService(ALARM_SERVICE);

        }
        Intent intent = new Intent(AppUpdateAlert.alarmActivity, NotificationAlarmService.class);
        AppUpdateAlert.pendingIntent = PendingIntent.getService(AppUpdateAlert.alarmActivity, 0, intent, 0);
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 30);
        if (AppUpdateAlert.alarmManager != null) {
            AppUpdateAlert.alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AppUpdateAlert.pendingIntent);
        }
    }
}
