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
 * THIS CLASS IS USED TO SHOW AND DISPLAY THE INTERNAL STORAGE & EXTERNAL STORAGE
 * DETAILS, SUCH AS TOTAL, AVAILABLE & USED SPACE.
 */
public class StorageViewer {

    public static long total, aval, used;


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

    // Show External SD_CARD Total Space
    public static String showExternalSDCardTotalSpace(Context ctx){

        if (externalMemoryAvailable()){
            File path = Environment.getExternalStorageDirectory();
            StatFs fs = new StatFs(path.getPath());
            total = (long)fs.getBlockCount() * fs.getBlockSize();

        }else{
            File path = Environment.getExternalStorageDirectory();
            StatFs fs = new StatFs(path.getPath());
            total = (long)fs.getBlockCount() * fs.getBlockSize();
            //return Formatter.formatFileSize(ctx, total);
        }
        return Formatter.formatFileSize(ctx, total);

    }

    /**
     * THIS METHOD IS USED TO GET THE TOTAL SPACE OF EXTERNAL MEDIA
     * @param ctx
     * @return total memory with formatter.
     */
    public static String showExternalSDCardFreeSpace(Context ctx){
        File path = Environment.getExternalStorageDirectory();
        StatFs fs = new StatFs(path.getPath());
        aval = (long)fs.getAvailableBlocks() * fs.getBlockSize();
        return Formatter.formatFileSize(ctx, aval);
    }

    /**
     * THIS METHOD IS USED TO GET THE USED SPACE OF EXTERNAL MEDIA
     * @param ctx
     * @return used memory with formatter.
     */
    public static String showExternalSDCardUsedSpace(Context ctx){
        if (externalMemoryAvailable()){
            File path = Environment.getExternalStorageDirectory();
            StatFs fs = new StatFs(path.getPath());
            long total = (long)fs.getBlockCount() * fs.getBlockSize();
            long aval = (long)fs.getAvailableBlocks() * fs.getBlockSize();
            used = total - aval;

        }else {
            File path = Environment.getExternalStorageDirectory();
            StatFs fs = new StatFs(path.getPath());
            long total = (long)fs.getBlockCount() * fs.getBlockSize();
            long aval = (long)fs.getAvailableBlocks() * fs.getBlockSize();
            used = total - aval;
            //return Formatter.formatFileSize(ctx, used);
        }
        return Formatter.formatFileSize(ctx, used);

    }

    /**
     * THIS METHOD IS TO CHECK WHETHER EXTERNAL MEDIA IS MOUNTED OR NOT
     * ON THE DEVICE.
     * @return status OF EXTERNAL MEDIA.
     */
    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }
}
