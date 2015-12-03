package com.appman.appmanager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.appman.appmanager.utils.AppData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rudhraksh.pahade on 28-11-2015.
 */
public class ApkInfo extends AppCompatActivity {

    private static final String TAG = ApkInfo.class.getSimpleName();

    TextView appLabel, packageName, version, features;
    TextView permissions, andVersion, installed, lastModify, path;
    PackageInfo packageInfo;
    Toolbar toolbar;
    private final int UNINSTALL_REQUEST_CODE = 1;
    private ProgressDialog progressDialog;
    private Typeface font;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_details);
        font = Typeface.createFromAsset(getAssets(), "fonts/Avenir-Book.ttf");

        findViewsById();

        AppData appData = (AppData) getApplicationContext();
        packageInfo = appData.getPackageInfo();

        setValues();

    }

    private void findViewsById() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_details));
        appLabel = (TextView) findViewById(R.id.applabel);
        packageName = (TextView) findViewById(R.id.package_name);
        version = (TextView) findViewById(R.id.version_name);
        features = (TextView) findViewById(R.id.req_feature);
        permissions = (TextView) findViewById(R.id.req_permission);
        andVersion = (TextView) findViewById(R.id.andversion);
        path = (TextView) findViewById(R.id.path);
        installed = (TextView) findViewById(R.id.insdate);
        lastModify = (TextView) findViewById(R.id.last_modify);
    }



    private void setValues() {

        // APP name
        appLabel.setText(getPackageManager().getApplicationLabel(packageInfo.applicationInfo));
        appLabel.setTypeface(font);

        // package name
        packageName.setText(packageInfo.packageName);
        packageName.setTypeface(font);

        // version name
        version.setText(packageInfo.versionName);
        version.setTypeface(font);

        // target version
        andVersion.setText(Integer.toString(packageInfo.applicationInfo.targetSdkVersion));
        andVersion.setTypeface(font);

        // path
        path.setText(packageInfo.applicationInfo.sourceDir);
        path.setTypeface(font);

        // first installation
        installed.setText(setDateFormat(packageInfo.firstInstallTime));
        installed.setTypeface(font);

        // last modified
        lastModify.setText(setDateFormat(packageInfo.lastUpdateTime));
        lastModify.setTypeface(font);

        // features
        if (packageInfo.reqFeatures != null){
            features.setText(getFeatures(packageInfo.reqFeatures));
            features.setTypeface(font);
        }
        else{
            features.setText("-");
        }


        // uses-permission
        if (packageInfo.requestedPermissions != null){
            permissions.setText(getPermissions(packageInfo.requestedPermissions));
            permissions.setTypeface(font);
        }
        else
            permissions.setText("-");
    }

    @SuppressLint("SimpleDateFormat")
    private String setDateFormat(long time) {
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String strDate = formatter.format(date);
        return strDate;
    }

    // Convert string array to comma separated string
    private String getPermissions(String[] requestedPermissions) {
        String permission = "";
        for (int i = 0; i < requestedPermissions.length; i++) {
            permission = permission + requestedPermissions[i] + ",\n";
        }
        return permission;
    }

    // Convert string array to comma separated string
    private String getFeatures(FeatureInfo[] reqFeatures) {
        String features = "";
        for (int i = 0; i < reqFeatures.length; i++) {
            features = features + reqFeatures[i] + ",\n";
        }
        return features;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popupmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // If backup if selected by the user
        if (id == R.id.menu_backup){
            final String pName = packageInfo.packageName;
            Log.d(TAG, pName);
            backupApplication(pName);
        }
        else if (id == R.id.menu_uninstall){
            // If user selected to uninstall the application
            final String packName = packageInfo.packageName;
            Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
            uninstallIntent.setData(Uri.parse("package:" + packName));
            uninstallIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
            startActivityForResult(uninstallIntent, UNINSTALL_REQUEST_CODE);
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * THIS METHOD WILL SHOW AN ALERT DIALOG MENTIONING TO BACKUP THE FILE.
     * IF YES THEN CALL TO ASYNC TASK TO PERFORM ACTUAL COPYING OF FILE TO STORAGE LOCATION
     * @param packageName
     */

    private void backupApplication(String packageName){
        new AlertDialog.Builder(ApkInfo.this)
                .setMessage("Are you sure you want to perform a backup of this application ?")
                .setPositiveButton("YES ! Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Backup().execute();
                    }
                }).setNegativeButton("Cancel & Exit", null)
                .show();

    }

    /**
     * PRIVATE INNER CLASS THAT WILL ACTUAL COPY THE FILE TO THE STORAGE LOCATION,
     * WITH THEIR APP NAME & LOGO WITH PROPER MESSAGE SO THAT USER WILL BE ENGAGED
     * WHILE IT PERFORMS THE OPERATION.
     */

    private class Backup extends AsyncTask<Void, Void, Void>{

        String fileName = getPackageManager().getApplicationLabel(packageInfo.applicationInfo).toString();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ApkInfo.this);
            progressDialog.setMessage("Please wait while we take a backup of "+fileName+"."+"This will only take a moment.");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            File f1 = new File(packageInfo.applicationInfo.sourceDir);
            try{
                Log.d(TAG, fileName);
                File f2 = new File(Environment.getExternalStorageDirectory().toString()+"/"+getString(R.string.app_name));
                f2.mkdirs();
                f2 = new File(f2.getPath()+"/"+fileName+".apk");
                f2.createNewFile();

                InputStream in = new FileInputStream(f1);
                OutputStream out = new FileOutputStream(f2);

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0){
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            }catch (FileNotFoundException fnfe){
                fnfe.getMessage().toString();
            }catch (Exception e){
                e.getMessage().toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"Backup finished. Stored in "+Environment.getExternalStorageDirectory().toString()+"/"+getString(R.string.app_name),Toast.LENGTH_LONG).show();
            finish();
            startActivity(new Intent(ApkInfo.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UNINSTALL_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                Toast.makeText(getApplicationContext(), "Uninstall finished", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(ApkInfo.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
            else if (resultCode == RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), "User aborted the operation", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RESULT_FIRST_USER){
                Toast.makeText(getApplicationContext(), "Failed to uninstall", Toast.LENGTH_SHORT).show();
            }
        }
    }
}