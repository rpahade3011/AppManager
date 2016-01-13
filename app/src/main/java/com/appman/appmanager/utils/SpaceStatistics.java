package com.appman.appmanager.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.File;
import java.io.IOException;

/**
 * Created by Rudraksh on 01-Jan-16.
 */
public class SpaceStatistics {

    // Global Definitions


    public static String getImagesStatistics(Context ctx){

        try{
            StatFs fs = new StatFs(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
            long total = ((long)fs.getBlockCount() * fs.getBlockSize());
            long free  = ((long)fs.getAvailableBlocks() * fs.getBlockSize());
            long busy  = total - free;
            return Formatter.formatFileSize(ctx, busy);

        }catch (Exception e){
            e.getMessage().toString();
        }
        return "";

    }
}
