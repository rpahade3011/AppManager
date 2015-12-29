package com.appman.appmanager.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Process;
import android.text.format.Formatter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by rudhraksh.pahade on 02-12-2015.
 */

/**
 * Document description:
 * Memory tools
 *
 * Including content:
 * 1 memory cleanup that kill the process in several ways
 * 2 access to the memory size and the size of the total available
 * 3 determine the stack top Activity name and its process
 *
 * Note that permissions:
 * <uses-permission android:name="android.permission.GET_TASKS"/>
 * <uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />
 *
 * Document date:
 * In May 30, 2014 10:01:55
 *
 */
public class MemoryUtils {


    // Total Ram
    public static String getTotalMemory(Context ctx){
        ActivityManager actManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long totalMemory = memInfo.totalMem;
        return Formatter.formatFileSize(ctx, totalMemory);
    }

    // Available Ram
    public static String getAvailableRam(Context ctx){
        ActivityManager actManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long availableMem = memInfo.availMem;
        return Formatter.formatFileSize(ctx, availableMem);
    }
    // Used Memory
    public static String getUsedRam(Context ctx){
        ActivityManager actManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long totalMemory = memInfo.totalMem;
        long availableMem = memInfo.availMem;
        long usedMemory = totalMemory - availableMem;
        return Formatter.formatFileSize(ctx, usedMemory);
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }


        return dir.delete();
    }

    public static void clearCache(Context context){
        ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).clearApplicationUserData();
    }

    public void clearApplicationData(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                }
            }
        }
    }


}
