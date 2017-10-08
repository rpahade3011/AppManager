package com.appman.appmanager.utils;

/**
 * Created by rudhraksh.pahade on 08-12-2015.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;

import com.appman.appmanager.R;
import com.gc.materialdesign.views.ButtonFlat;


/**
 * AppRater prompts engaged users to rate your app in the Android market. It requires a certain number
 * of launches of the app and days since the installation before the rating dialog appears.<br/>
 * Found at: http://www.androidsnippets.com/prompt-engaged-users-to-rate-your-app-in-the-android-market-appirater
 * @author Sissi @ http://www.androidsnippets.com/
 */
public class AppRater {

    private final static String APP_TITLE = "AppManager";
    private final static String APP_PNAME = "com.appman.appmanager";
    private final static int DAYS_UNTIL_PROMPT = 3;
    private final static int LAUNCHES_UNTIL_PROMPT = 7;


    /**
     * METHOD TO INITIALIZE AND SETUP THE SHARED PREFS VALUES,
     * GET THE SYSTEM DATE AND INSTALLED DATE OF APPLICATION, WAIT FOR N DAYS
     * THE CALL THE {@link showRateDialog}
     * @param mContext
     */

    public static void app_launched(Context mContext) {

        SharedPreferences prefs = mContext.getSharedPreferences("appmanager_apprater", 0);

        if (prefs.getBoolean("dontshowagain", false)) { return ; }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }

        editor.apply();
    }

    /**
     * MEHTOD TO DISPLAY THE CUSTOM DIALOG TO ENGAGED USERS TO RATE THE APPLICATION
     * @param mContext
     * @param editor
     */
    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle("Rate " + APP_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.apprater_activity);

        final ButtonFlat btnRateMe = (ButtonFlat) dialog.findViewById(R.id.buttonRateApp);
        final ButtonFlat btnRemindLater = (ButtonFlat) dialog.findViewById(R.id.buttonRemindLater);
        final ButtonFlat btnNoThanks = (ButtonFlat)dialog.findViewById(R.id.buttonNoThanks);

        // Button Rate Clicked
        btnRateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                }catch (Exception e){
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://market.android.com/details?id="+ APP_PNAME)));
                }

                dialog.dismiss();
            }
        });
        // Button Remind Later Clicked
        btnRemindLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // Button No Thanks Clicked
        btnNoThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
