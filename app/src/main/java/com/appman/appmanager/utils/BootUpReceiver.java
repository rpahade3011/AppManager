package com.appman.appmanager.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.appman.appmanager.activities.MainActivity;


/**
 * Created by rudhraksh.pahade on 03-12-2015.
 */
public class BootUpReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
