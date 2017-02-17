package com.appman.appmanager.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;

import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.R;
import com.appman.appmanager.models.AppFavInfo;
import com.appman.appmanager.models.AppInfo;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkButtonBuilder;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

/**
 * Created by rudhraksh.pahade on 11/2/2016.
 */

public class Utility {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_READ = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static SparkButton sparkButton;
    public static List<AppFavInfo> appFavInfoList;
    /**
     * Default folder where APKs will be saved
     * @return File with the path
     */
    public static File getDefaultAppFolder() {
        return new File(Environment.getExternalStorageDirectory() + "/AppManager/APK/");
    }

    /**
     * Default folder where all SMS will be saved
     * @return File with the path
     */
    public static File getDefaultSmsFolder(){
        return new File(Environment.getExternalStorageDirectory() + "/AppManager/SMS/");
    }

    /**
     * Default folder where all contacts will be saved
     * @return File with the path
     */
    public static File getDefaultContactsFolder(){
        return new File(Environment.getExternalStorageDirectory() + "/AppManager/Contacts/");
    }

    public static int getDayOrNight() {
        int actualHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (actualHour >= 8 && actualHour < 19) {
            return 1;
        } else {
            return 0;
        }
    }

    public static Boolean copyFile(AppInfo appInfo) {
        Boolean res = false;

        File initialFile = new File(appInfo.getSource());
        File finalFile = getOutputFilename(appInfo);

        try {
            FileUtils.copyFile(initialFile, finalFile);
            res = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    public static Boolean copyFavFile(AppFavInfo appInfo) {
        Boolean res = false;

        File initialFile = new File(appInfo.getSource());
        File finalFile = getFavOutputFilename(appInfo);

        try {
            FileUtils.copyFile(initialFile, finalFile);
            res = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    /**
     * Retrieve the name of the extracted APK
     * @param appInfo AppInfo
     * @return String with the output name
     */
    public static String getAPKFilename(AppInfo appInfo) {
        AppPreferences appPreferences = AppManagerApplication.getAppPreferences();
        String res;

        switch (appPreferences.getCustomFilename()) {
            case "1":
                res = appInfo.getAPK() + "_" + appInfo.getVersion();
                break;
            case "2":
                res = appInfo.getName() + "_" + appInfo.getVersion();
                break;
            case "4":
                res = appInfo.getName();
                break;
            default:
                res = appInfo.getAPK();
                break;
        }

        return res;
    }

    public static String getFavAPKFilename(AppFavInfo appInfo) {
        AppPreferences appPreferences = AppManagerApplication.getAppPreferences();
        String res;

        switch (appPreferences.getCustomFilename()) {
            case "1":
                res = appInfo.getAPK() + "_" + appInfo.getVersion();
                break;
            case "2":
                res = appInfo.getName() + "_" + appInfo.getVersion();
                break;
            case "4":
                res = appInfo.getName();
                break;
            default:
                res = appInfo.getAPK();
                break;
        }

        return res;
    }

    /**
     * Retrieve the name of the extracted APK with the path
     * @param appInfo AppInfo
     * @return File with the path and output name
     */
    public static File getOutputFilename(AppInfo appInfo) {
        return new File(getAppFolder().getPath() + "/" + getAPKFilename(appInfo) + ".apk");
    }

    public static File getFavOutputFilename(AppFavInfo appInfo) {
        return new File(getAppFolder().getPath() + "/" + getFavAPKFilename(appInfo) + ".apk");
    }

    /**
     * Delete all the extracted APKs
     * @return true if all files have been deleted, false otherwise
     */
    public static Boolean deleteAppFiles(Context context) {
        //UtilsDialog.showTitleContent(context, "Confirm Delete", "Are you sure you want to delete all the files stored in the extracted folder ?");
        Boolean res = false;
        File f = getAppFolder();
        if (f.exists() && f.isDirectory()) {
            File[] files = f.listFiles();
            for (File file : files) {
                file.delete();
            }
            if (f.listFiles().length == 0) {
                res = true;
            }
        }
        return res;

    }

    /**
     * Custom folder where APKs will be saved
     * @return File with the path
     */
    public static File getAppFolder() {
        AppPreferences appPreferences = AppManagerApplication.getAppPreferences();
        return new File(appPreferences.getCustomPath());
    }
    private static void openShareIntent(Context ctx){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, ctx.getResources().getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey, look out this awesome application monitor and backup tool for Android - " + "https://play.google.com/store/apps/details?id=com.appman.appmanager&hl=en");
        ctx.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    /**
     * Retrieve your own app version
     * @param context Context
     * @return String with the app version
     */
    public static String getAppVersionName(Context context) {
        String res = "0.0.0.0";
        try {
            res = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    /**
     * Retrieve your own app version code
     * @param context Context
     * @return int with the app version code
     */
    public static int getAppVersionCode(Context context) {
        int res = 0;
        try {
            res = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    public static Intent getShareIntent(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.setType("application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }

    /**
     * Retrieve if an app has been marked as favorite
     * @param apk App to check
     * @param appFavorites Set with apps
     * @return true if the app is marked as favorite, false otherwise
     */
    public static Boolean isAppFavorite(String apk, Set<String> appFavorites) {
        Boolean res = false;
        if (appFavorites.contains(apk)) {
            res = true;
        }
        return res;
    }

    /**
     * Save the app as favorite
     * @param context Context
     * @param menuItem Item of the ActionBar
     * @param isFavorite true if the app is favorite, false otherwise

    public static void setAppFavorite(Context context, MenuItem menuItem, Boolean isFavorite) {
        if (isFavorite) {
            menuItem.setIcon(context.getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
        } else {
            menuItem.setIcon(context.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
        }
    }*/

    public static void setAppFavorite(Context context, MenuItem menuItem, Boolean isFavorite) {
        if (isFavorite) {
            sparkButton = new SparkButtonBuilder(context).setActiveImage(R.drawable.ic_favorite_black_24dp).build();
        } else {
            sparkButton = new SparkButtonBuilder(context).setInactiveImage(R.drawable.ic_favorite_border_black_24dp).build();
        }
    }

    /**
     * Retrieve if an app is hidden
     * @param appInfo App to check
     * @param appHidden Set with apps
     * @return true if the app is hidden, false otherwise
     */
    public static Boolean isAppHidden(AppInfo appInfo, Set<String> appHidden) {
        Boolean res = false;
        if (appHidden.contains(appInfo.toString())) {
            res = true;
        }

        return res;
    }

    /**
     * Convert Dp to Pixel
     */
    public static int dpToPx(float dp, Resources resources){
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    public static int getRelativeTop(View myView) {
//	    if (myView.getParent() == myView.getRootView())
        if(myView.getId() == android.R.id.content)
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }

    public static int getRelativeLeft(View myView) {
//	    if (myView.getParent() == myView.getRootView())
        if(myView.getId() == android.R.id.content)
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    /**
     * METHOD TO CHECK PERMISSIONS FOR WRITE EXTERNAL STORAGE,
     * REQUIRED FOR ANDROID 6.0 RUN TIME PERMISSIONS.
     * @param activity
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static Boolean checkPermissions(Activity activity) {
        Boolean res = false;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_READ);
        } else {
            res = true;
        }

        return res;
    }

    /**
     * Delelete an app icon from cache folder
     * @param context Context
     * @param appInfo App to remove icon
     * @return true if the icon has been removed, false otherwise
     */
    public static Boolean removeIconFromCache(Context context, AppInfo appInfo) {
        File file = new File(context.getCacheDir(), appInfo.getAPK());
        return file.delete();
    }

    /**
     * Get an app icon from cache folder
     * @param context Context
     * @param appInfo App to get icon
     * @return Drawable with the app icon
     */
    public static Drawable getIconFromCache(Context context, AppInfo appInfo) {
        Drawable res;

        try {
            File fileUri = new File(context.getCacheDir(), appInfo.getAPK());
            Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath());
            res = new BitmapDrawable(context.getResources(), bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            res = context.getResources().getDrawable(R.drawable.ic_android);
        }

        return res;
    }

    /**
     * Opens Google Play if installed, if not opens browser
     * @param context Context
     * @param id PackageName on Google Play
     */
    public static void goToGooglePlay(Context context, String id) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + id))
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + id))
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    /**
     * Opens Google Plus
     * @param context Context
     * @param id Name on Google Play
     */
    public static void goToGooglePlus(Context context, String id) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/u/0/" + id)));
    }

    /**
     * OPENS FACBOOK ACCOUNT
     * @param context
     * @param id
     */
    /*public static void goToFacebook(Context context, String id){
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + id)));
    }*/
    public static Intent getFacebookIntent(Context context, String id){
        try{
            context.getPackageManager().getPackageInfo("com.facebook.katana",0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + id));
        }catch (PackageManager.NameNotFoundException nnfe){
            nnfe.getMessage().toString();
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + id));
        }
    }

    /**
     * OPENS TWITTER ACCOUNT
     * @param context
     * @param id
     */
    /*public static void goToTwitter(Context context, String id){
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitter.com/" + id)));
    }*/

    public static Intent getTwitterIntent(Context context, String id){
        try{
            context.getPackageManager().getPackageInfo("com.twitter.android", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id="+id));
        }catch (PackageManager.NameNotFoundException nnfe){
            nnfe.getMessage().toString();
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitter.com/" + id));
        }
    }
}
