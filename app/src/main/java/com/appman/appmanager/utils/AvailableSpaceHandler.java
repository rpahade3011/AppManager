package com.appman.appmanager.utils;

/**
 * Created by rudhraksh.pahade on 01-12-2015.
 */

import android.os.Environment;
import android.os.StatFs;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is designed to get available space in external storage of android.
 * It contains methods which provide you the available space in different units e.g
 * bytes, KB, MB, GB. OR you can get the number of available blocks on external storage.
 * Also it is used to extract the Total Ram installed in the device.
 */
public class AvailableSpaceHandler {
    /**
     * Number of bytes in one KB = 2<sup>10</sup>
     */
    public final static long SIZE_KB = 1024L;
    /**
     * Number of bytes in one MB = 2<sup>20</sup>
     */
    public final static long SIZE_MB = SIZE_KB * SIZE_KB;
    /**
     * Number of bytes in one GB = 2<sup>30</sup>
     */
    public final static long SIZE_GB = SIZE_KB * SIZE_KB * SIZE_KB;


    /**
     * @return Number of bytes available on external storage
     */

    public static long getExternalAvailableSpaceInBytes() {
        long availableSpace = -1L;
        try {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            availableSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return availableSpace;
    }

    /**
     * @return Number of kilo bytes available on external storage
     */
    public static long getExternalAvailableSpaceInKB(){
        return getExternalAvailableSpaceInBytes()/SIZE_KB;
    }
    /**
     * @return Number of Mega bytes available on external storage
     */
    public static long getExternalAvailableSpaceInMB(){
        return getExternalAvailableSpaceInBytes()/SIZE_MB;
    }
    /**
     * @return gega bytes of bytes available on external storage
     */
    public static long getExternalAvailableSpaceInGB(){
        return getExternalAvailableSpaceInBytes()/SIZE_GB;
    }
    /**
     * @return Total number of available blocks on external storage
     */
    public static long getExternalStorageAvailableBlocks() {
        long availableBlocks = -1L;
        try {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            availableBlocks = stat.getAvailableBlocks();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return availableBlocks;
    }

    /**
     * METHOD TO EXTRACT TOTAL SPACE OF THE DEVICE
     * @return
     */

    public static long getTotalSpace(){
        long totalSpace = -1L;
        totalSpace = (long)Environment.getExternalStorageDirectory().getTotalSpace()/SIZE_GB;
        return totalSpace;
    }

    /**
     * METHOD THAT WILL EXTRACT THE TOTAL RAM SIZE OF THE DEVICE
     * @return
     */
    public static String getTotalRAM() {

        RandomAccessFile reader = null;
        String load = null;
        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
        double totRam = 0;
        String lastValue = "";
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            load = reader.readLine();

            // Get the Number value from the string
            Pattern p = Pattern.compile("(\\d+)");
            Matcher m = p.matcher(load);
            String value = "";
            while (m.find()) {
                value = m.group(1);
                // System.out.println("Ram : " + value);
            }
            reader.close();

            totRam = Double.parseDouble(value);
            // totRam = totRam / 1024;

            double mb = totRam / 1024.0;
            double gb = totRam / 1048576.0;
            double tb = totRam / 1073741824.0;

            if (tb > 1) {
                lastValue = twoDecimalForm.format(tb).concat(" TB");
            } else if (gb > 1) {
                lastValue = twoDecimalForm.format(gb).concat(" GB");
            } else if (mb > 1) {
                lastValue = twoDecimalForm.format(mb).concat(" MB");
            } else {
                lastValue = twoDecimalForm.format(totRam).concat(" KB");
            }



        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Streams.close(reader);
        }

        return lastValue;
    }

    public static long getUsedMemorySize(){
        long freeSize = 0L;
        long totalSize = 0L;
        long usedSize = -1L;
        try{
            Runtime info = Runtime.getRuntime();
            freeSize = info.freeMemory();
            totalSize = info.totalMemory();
            usedSize = totalSize - freeSize;
        }catch (Exception e){
            e.getMessage().toString();
        }
        return usedSize;
    }


    /**
     * METHOD TO FREE UP THE RAM OF THE DEVICE
     */
    public static void cleanMemory(){
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }




}
