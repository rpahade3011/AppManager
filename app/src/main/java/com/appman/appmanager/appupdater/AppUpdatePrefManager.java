package com.appman.appmanager.appupdater;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by rudhraksh.pahade on 3/8/2017.
 */

public class AppUpdatePrefManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public AppUpdatePrefManager(Activity mAct) {
        int PRIVATE_MODE = 0;
        pref = mAct.getSharedPreferences(mAct.getPackageName(), PRIVATE_MODE);
    }

    public void setCount() {
        editor = pref.edit();
        editor.putInt(Config.KEY_COUNT, getCount() + 1);
        editor.apply();
    }

    public int getCount() {
        if (pref.getInt(Config.KEY_COUNT, 0) >= getPref()) {
            editor = pref.edit();
            editor.putInt(Config.KEY_COUNT, 0);
            editor.apply();
            return 0;
        } else {
            return pref.getInt(Config.KEY_COUNT, 0);
        }
    }

    public void setPref(int count) {
        editor = pref.edit();
        editor.putInt(Config.KEY_PREF, count);
        editor.apply();
    }

    private int getPref() {
        return pref.getInt(Config.KEY_PREF, 5);
    }
}
