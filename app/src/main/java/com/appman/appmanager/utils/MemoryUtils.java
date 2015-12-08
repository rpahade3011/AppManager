package com.appman.appmanager.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    //----------> The following several ways to kill the process.
    public static long totalCleanMem;
    public static long cleanMemory(final Context context) {

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                long beforeCleanMemory=getAvailMemory(context);
                System.out.println("---> Clean up before the available memory size:"+beforeCleanMemory+"M");
                ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                RunningAppProcessInfo runningAppProcessInfo = null;
                List<RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
                for (int i = 0; i <runningAppProcessInfoList.size(); ++i) {
                    runningAppProcessInfo = runningAppProcessInfoList.get(i);
                    String processName = runningAppProcessInfo.processName;
                    if (processName.equals("com.appman.appmanager")){
                        System.out.println("---> OWN PACKAGE:"+processName);

                    }else {
                        //Method invocation kill process
                        System.out.println("---> Start cleaning:"+processName);
                        killProcessByRestartPackage(context, processName);
                    }
                }
                long afterCleanMemory=getAvailMemory(context);
                System.out.println("---> The available memory size after cleaning:" + afterCleanMemory + "M");
                System.out.println("---> Save the memory size:" + (afterCleanMemory - beforeCleanMemory) + "M");
                totalCleanMem = afterCleanMemory-beforeCleanMemory;

            }
        };
        thread.start();
        return totalCleanMem;
    }



    //Using activityManager.restartPackage () method of kill process
    //The actual method calls activityManager.killBackgroundProcesses () method
    public static void killProcessByRestartPackage(Context context,String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.restartPackage(packageName);
        System.gc();
    }


    //Using Process.killProcess (PID) kill process
    //Matters needing attention:
    //The 1 methods can be Dutch act, that is to kill the process
    //2 it may kill other common application process
    //3 the mode can not kill the application or system/app application
    public static void killProcessBykillProcess(int pid){
        Process.killProcess(pid);
    }


    //Using the ADB shell command to kill process
    public static void killProcessByAdbShell(int pid) {
        String cmd = "adb shell kill -9 " + pid;
        System.out.println("-------> cmd=" + cmd);
        try {
            java.lang.Process process = Runtime.getRuntime().exec(cmd);
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println("----> exec shell:" + line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Using the Su process in order to kill the process
    //1 to get the Su process (super process)
    //  Runtime.getRuntime().exec("su");
    //2 using Su process in the implementation of the command
    //  process.getOutputStream().write(cmd.getBytes());
    public static void killProcessBySu(int pid) {
        try {
            java.lang.Process process = Runtime.getRuntime().exec("su");
            String cmd = "kill -9 " + pid;
            System.out.println("-------> cmd = " + cmd);
            process.getOutputStream().write(cmd.getBytes());
            if ((process.waitFor() != 0)) {
                System.out.println("-------> su.waitFor()!= 0");
            } else {
                System.out.println("------->  su.waitFor()==0 ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //----------> Above several ways to kill the process.





    //Gets the current process
    public static String getCurrentProcessName(Context context) {
        int pid = Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningAppProcessInfo runningAppProcessInfo : activityManager.getRunningAppProcesses()) {
            if (runningAppProcessInfo.pid == pid) {
                String processName=runningAppProcessInfo.processName;
                return processName;
            }
        }
        return null;
    }


    //Access to the top of the stack Activity name
    public static String getTopActivityName(Context context) {
        String topActivityName = null;
        ActivityManager activityManager = (ActivityManager) (context.getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            String topActivityClassName = f.getClassName();
            String temp[] = topActivityClassName.split("\\.");
            topActivityName = temp[temp.length - 1];
        }
        return topActivityName;
    }



    //Gets the name of the top of the stack in Activity process
    public static String getTopActivityProcessName(Context context) {
        String processName = null;
        ActivityManager activityManager = (ActivityManager) (context.getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName componentName = runningTaskInfos.get(0).topActivity;
            String topActivityClassName = componentName.getClassName();
            int index = topActivityClassName.lastIndexOf(".");
            processName = topActivityClassName.substring(0, index);
        }
        return processName;
    }



    //Gets the total size of memory
    public static long getTotalMemory() {
        // Memory file system
        String filePath = "/proc/meminfo";
        String lineString;
        String[] stringArray;
        long totalMemory = 0;
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader,1024 * 8);
            // Read meminfo first line, access to the system total memory size
            lineString = bufferedReader.readLine();
            // According to the space resolution
            stringArray = lineString.split("\\s+");
            // System total memory, unit KB
            totalMemory = Integer.valueOf(stringArray[1]).intValue();
            bufferedReader.close();
        } catch (IOException e) {
        }
        return totalMemory / 1024;
    }



    //Access to the available memory size
    public static long getAvailMemory(Context context) {
        long availMemory = -1L;
        ActivityManager activityManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        availMemory = memoryInfo.availMem / (1024 * 1024);
        return availMemory;
    }

    public static String getMemorySizeStrings() {

        DecimalFormat dformat = new DecimalFormat("#.##");
        String mem = null;
        long freeSize = 0L;
        long totalSize = 0L;
        long usedSize = 0L;
        double totCal = 0;
        String lastValue = "";
        String load = null;
        try {
            Runtime info = Runtime.getRuntime();
            freeSize = info.freeMemory();
            totalSize = info.totalMemory();
            usedSize = totalSize - freeSize;

            // totRam = totRam / 1024;

            double mb = usedSize / 1024.0;
            double gb = usedSize / 1048576.0;
            double tb = usedSize / 1073741824.0;

            if (tb > 1) {
                lastValue = dformat.format(tb).concat(" TB");
            } else if (gb > 1) {
                lastValue = dformat.format(gb).concat(" GB");
            } else if (mb > 1) {
                lastValue = dformat.format(mb).concat(" MB");
            } else {
                lastValue = dformat.format(totCal).concat(" KB");
            }

            mem = "free=" + dformat.format(freeSize) + " byte / " + "total="
                    + dformat.format(totalSize) + " byte / " + "used="
                    + dformat.format(usedSize) + " byte";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lastValue;
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

    public static long calculateRam(Context context){
        long total = 0;
        long used = 0;
        long free = 0;

        total = getTotalMemory();
//        used = getAvailMemory(context);
        used = getFreeMemorySize();

        free = total - used;
        return free;
    }

    public static long getFreeMemorySize() {

        long size = -1L;
        try {
            Runtime info = Runtime.getRuntime();
            size = info.freeMemory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
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
