package com.appman.appmanager.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.R;
import com.appman.appmanager.adapter.ListAdapter;
import com.appman.appmanager.utils.AppPreferences;
import com.appman.appmanager.utils.MemoryUtils;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ProgressBarDeterminate;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.List;

/**
 * Created by rudhraksh.pahade on 04-12-2015.
 */
public class MemoryInfoActivity extends AppCompatActivity {

    private static final String TAG = MemoryInfoActivity.class.getSimpleName();
    Toolbar toolbar;
    Context mContext;
    TextView txtTotalRam, txtUsedRam;
    List<ActivityManager.RunningAppProcessInfo> runningProcesses;
    ButtonFlat btnCleanRam;
    // Load Settings
    private AppPreferences appPreferences;
    private TextView txtRunningProcessesCount;
    private ProgressWheel progressWheel;
    private ListView listProcess;
    private Integer totalRunningApps;
    private String totalRam;
    private long usedRam;
    private long ramCleaned;
    private long calculationOfRam;
    private ProgressBarDeterminate progressBarDeterminate;

    private String mem;
    private Vibrator mVibrator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_activity);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.action_ram));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mContext = getApplicationContext();
        this.appPreferences = AppManagerApplication.getAppPreferences();

        txtRunningProcessesCount = (TextView) findViewById(R.id.txtViewRunningProcessCount);

        progressWheel = (ProgressWheel) findViewById(R.id.progress);
        listProcess = (ListView) findViewById (R.id.listProcess);
        txtTotalRam = (TextView) findViewById(R.id.textViewTotalRam);
        txtUsedRam = (TextView) findViewById(R.id.textViewUsedRam);
        progressBarDeterminate = (ProgressBarDeterminate) findViewById(R.id.progressBar);

        progressWheel.setBarColor(appPreferences.getPrimaryColorPref());
        progressWheel.setVisibility(View.VISIBLE);
        btnCleanRam = (ButtonFlat) findViewById(R.id.buttonCleanRam);
        mVibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);


        getAvailableRam();
        showProgressbar();

        new MemoryInfoInBackground().execute();

    }

    private void getAvailableRam(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                totalRam = MemoryUtils.getTotalRAM();
                usedRam = MemoryUtils.getAvailMemory(mContext);
                calculationOfRam = MemoryUtils.calculateRam(mContext);
                mem = MemoryUtils.getMemorySizeStrings();

                Log.i(TAG, "TOTAL RAM :- " + totalRam);
                Log.i(TAG, "Available :- " + usedRam);
                Log.i(TAG, "FREE RAM :- " + calculationOfRam);
                Log.i(TAG, "MEM STRING :- "+mem);

                txtTotalRam.setText(getString(R.string.total_ram) + " " + totalRam);


                txtUsedRam.setText(getString(R.string.available_ram) + " " + usedRam + " " + "M");

            }
        });

        btnCleanRam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVibrator.vibrate(100);
                clearMemory();
            }
        });
    }

    private void clearMemory(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ramCleaned = MemoryUtils.cleanMemory(mContext);
                showUpdatedAvailableRam();
                Toast.makeText(mContext, "A total clean-up: " + ramCleaned + "M", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgressbar(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                progressBarDeterminate.setMax(100);
                progressBarDeterminate.setProgress(1);
            }
        };
        thread.start();
    }

    private void showUpdatedAvailableRam(){
        usedRam = MemoryUtils.getAvailMemory(getApplicationContext());
        txtUsedRam.setText(getString(R.string.available_ram) + " " + usedRam + " " + "M");
        new MemoryInfoInBackground().execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }

    public class MemoryInfoInBackground extends AsyncTask<Void, String, Void> {

        private Integer actualApps = 0;


        @Override
        protected Void doInBackground(Void... params) {
            // Get running processes
            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            runningProcesses = manager.getRunningAppProcesses();
            totalRunningApps = runningProcesses.size();

            actualApps++;
            publishProgress(Double.toString((actualApps * 100) / totalRunningApps));

            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            super.onProgressUpdate(progress);
            progressWheel.setProgress(Float.parseFloat(progress[0]));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (runningProcesses != null && runningProcesses.size() > 0) {
                // Set data to the list adapter
                txtRunningProcessesCount.setText(String.valueOf(totalRunningApps));
                listProcess.setVisibility(View.VISIBLE);
                listProcess.setAdapter(new ListAdapter(mContext, runningProcesses));
                progressWheel.setVisibility(View.GONE);

            } else {
                // In case there are no processes running (not a chance :))
                Toast.makeText(getApplicationContext(), "No application are running", Toast.LENGTH_LONG).show();
            }
        }
    }
}
