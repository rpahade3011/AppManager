package com.appman.appmanager.utils;

import android.content.Context;
import android.content.Intent;

import com.appman.appmanager.R;

import java.util.Calendar;

/**
 * Created by rudhraksh.pahade on 11/2/2016.
 */

public class Utility {

    public static int getDayOrNight() {
        int actualHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (actualHour >= 8 && actualHour < 19) {
            return 1;
        } else {
            return 0;
        }
    }

    private static void openShareIntent(Context ctx){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, ctx.getResources().getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey, look out this awesome application monitor and backup tool for Android - " + "https://play.google.com/store/apps/details?id=com.appman.appmanager&hl=en");
        ctx.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

}
