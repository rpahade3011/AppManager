package com.appman.appmanager.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.File;

/**
 * Created by rudhraksh.pahade on 21-12-2015.
 */

/**
 * THIS CLASS IS USED TO SHOW AND DISPLAY THE INTERNAL STORAGE
 * DETAILS, SUCH AS TOTAL, AVAILABLE & USED SPACE.
 */
public class StorageViewer {
    // Show SD_CARD Total Space
    public static String showSDCardTotalSpace(Context ctx){
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long total = (long)stat.getBlockCount() * stat.getBlockSize();
        return Formatter.formatFileSize(ctx, total);
    }
    // Show SD_CARD Free Space
    public static String showSDCardFreeSpace(Context ctx){
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long free  = (long)stat.getAvailableBlocks() * stat.getBlockSize();
        return Formatter.formatFileSize(ctx, free);
    }
    // Show SD_CARD Used Space
    public static String showSDCardUsedSpace(Context ctx){
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long total = ((long)stat.getBlockCount() * stat.getBlockSize());
        long free  = ((long)stat.getAvailableBlocks() * stat.getBlockSize());
        long busy  = total - free;
        return Formatter.formatFileSize(ctx, busy);
    }
}
