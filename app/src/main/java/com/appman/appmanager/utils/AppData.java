package com.appman.appmanager.utils;

import android.app.Application;
import android.content.pm.PackageInfo;

/**
 * Created by rudhraksh.pahade on 28-11-2015.
 */
public class AppData extends Application {

    PackageInfo packageInfo;

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }
}
