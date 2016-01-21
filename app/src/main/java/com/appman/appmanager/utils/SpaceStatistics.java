package com.appman.appmanager.utils;

import android.content.Context;
import android.os.Environment;
import android.text.format.Formatter;

import java.io.File;

/**
 * Created by Rudraksh on 01-Jan-16.
 */
public class SpaceStatistics {

    // Global Definitions
    private static String DCIM =  Environment.DIRECTORY_DCIM;
    private static String MUSIC = Environment.DIRECTORY_MUSIC;
    private static String MOVIES = Environment.DIRECTORY_MOVIES;
    private static String PICTURES = Environment.DIRECTORY_PICTURES;
    private static String RINGTONES = Environment.DIRECTORY_RINGTONES;

    public static String getImagesStatistics(Context ctx){
        long size = 0;

        try{
            File path = new File(DCIM);
            if (path.isDirectory()){
                for (File file : path.listFiles()){
                    size += file.length();
                }
            }else {
                size = path.length();
            }
        }catch (Exception e){
            e.getMessage().toString();
        }
        return Formatter.formatFileSize(ctx, size);
    }

    public static String getMusicStatistics(Context ctx){
        long size = 0;

        try{
            File path = new File(MUSIC);
            if (path.isDirectory()){
                for (File file : path.listFiles()){
                    size += file.length();
                }
            }else {
                size = path.length();
            }
        }catch (Exception e){
            e.getMessage().toString();
        }
        return Formatter.formatFileSize(ctx, size);
    }
}
