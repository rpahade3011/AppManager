package com.appman.appmanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appman.appmanager.adapter.ApkAdapter;
import com.appman.appmanager.utils.AppData;
import com.appman.appmanager.utils.AvailableSpaceHandler;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private Context mContext;
    PackageManager packageManager;
    ListView apkList;
    Toolbar toolbar;
    ProgressDialog progressDialog;
    List<PackageInfo> packageList1;
    private static long back_pressed;
    private long space;
    private long totalSpace;

    TextView txtSpace, txtTotalSpace;
    private Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById (R.id.toolbar);
        mContext = getApplicationContext();
        txtSpace = (TextView) findViewById(R.id.textViewSpace);
        txtTotalSpace = (TextView) findViewById (R.id.textViewTotalSpace);

        if (toolbar != null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setIcon(R.mipmap.hdd);
        }
        font = Typeface.createFromAsset(getAssets(), "fonts/Avenir-Book.ttf");

        new LoadApplications().execute();

        packageManager = getPackageManager();

        getAvailableSpace();
    }

    private void getAvailableSpace(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                space = AvailableSpaceHandler.getExternalAvailableSpaceInGB();
                totalSpace = AvailableSpaceHandler.getTotalSpace();

                Log.i(TAG, "Available Space :- " + space);
                Log.i(TAG, "Total Space :- " + totalSpace);

                txtSpace.setText(getString(R.string.available_space) + " " + String.valueOf(space) + " " + "GB left.");
                txtSpace.setTypeface(font);

                txtTotalSpace.setText(getString(R.string.total_space) + " " + String.valueOf(totalSpace) + " " + "GB.");
                txtTotalSpace.setTypeface(font);

            }
        });

    }

    /**
     * Return whether the given PackgeInfo represents a system package or not.
     * User-installed packages (Market or otherwise) should not be denoted as
     * system packages.
     *
     * @param pkgInfo
     * @return boolean
     */
    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true : false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;
        switch (item.getItemId()) {
            case R.id.menu_ram_info: {
                //displayAboutDialog();
                startActivity(new Intent(MainActivity.this, RamInfo.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            }
            case R.id.menu_device_info:
                startActivity(new Intent(MainActivity.this, DeviceInfo.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            default: {
                result = super.onOptionsItemSelected(item);

                break;
            }
        }

        return result;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PackageInfo packageInfo = (PackageInfo) parent.getItemAtPosition(position);
        AppData appData = (AppData) getApplicationContext();
        appData.setPackageInfo(packageInfo);

        Intent appInfo = new Intent(getApplicationContext(), ApkInfo.class);
        startActivity(appInfo);
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setIndeterminate(false);
            progressDialog.setMessage("Loading all applications");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                List<PackageInfo> packageList = packageManager
                        .getInstalledPackages(PackageManager.GET_PERMISSIONS);

                packageList1 = new ArrayList<PackageInfo>();

            /*To filter out System apps*/
                for(PackageInfo pi : packageList) {
                    boolean b = isSystemPackage(pi);
                    if(!b) {
                        packageList1.add(pi);
                    }
                }
            }catch (Exception e){
                e.getMessage().toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            progressDialog = null;
            apkList = (ListView) findViewById(R.id.applist);
            ApkAdapter adapter = new ApkAdapter(MainActivity.this, packageList1, packageManager);
            apkList.setAdapter(adapter);
            adapter.notifyDatasetChanged();

            apkList.setOnItemClickListener(MainActivity.this);
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    // Double back press to exit the application
    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            Toast.makeText(getApplicationContext(), "Press back again to exit !", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();

    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }
}
