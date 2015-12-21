package com.appman.appmanager.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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
    TextView txtTotalRam;
    List<ActivityManager.RunningAppProcessInfo> runningProcesses;
    ButtonFlat btnCleanRam;
    // Load Settings
    private AppPreferences appPreferences;
    private TextView txtRunningProcessesCount;
    private ProgressWheel progressWheel;
    private ListView listProcess;
    private Integer totalRunningApps;
    private String totalRam;
    private long ramCleaned;
    private ProgressBarDeterminate progressBarDeterminate;
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
        progressBarDeterminate = (ProgressBarDeterminate) findViewById(R.id.progressBar);

        progressWheel.setBarColor(appPreferences.getPrimaryColorPref());
        progressWheel.setVisibility(View.VISIBLE);
        btnCleanRam = (ButtonFlat) findViewById(R.id.buttonCleanRam);
        mVibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);


        getAvailableRam();

        new LoadProcessesInBackground().execute();

    }

    /**
     * THIS WILL LOAD THE TOTAL AND USED RAM FROM {@link MemoryUtils}
     * AND UPDATE THE TEXT ACCORDINGLY
     */

    private void getAvailableRam(){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    totalRam = MemoryUtils.getTotalMemory(mContext);
                    String usedRam = MemoryUtils.getUsedRam(mContext);
                    txtTotalRam.setText(usedRam + " / " + totalRam);
                    showRamProgressBar(mContext);
                }
                catch (RuntimeException re){
                    re.getMessage().toString();
                }catch (Exception e){
                    e.getMessage().toString();
                }

            }
        });
        t.start();

        /**
         * WHEN USER CLICKS ON THE CLEAR MEMORY BUTTON
         */
        btnCleanRam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfimationDialog();

            }
        });
    }

    private void showConfimationDialog(){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(MemoryInfoActivity.this);
        builder.title("WARNING");
        builder.content(getString(R.string.ram_clear_message));
        builder.negativeText("Cancel");
        builder.positiveText("OK");
        builder.negativeColor(getResources().getColor(R.color.colorPrimary));
        builder.positiveColor(getResources().getColor(R.color.card_blue));
        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                mVibrator.vibrate(100);
                clearMemory();
            }
        });
        builder.show();
    }

    /**
     * THIS METHOD WILL CLEAR THE BACKGROUND RUNNING PROCESSES
     * BY CALLING {@link MemoryUtils}
     */

    private void clearMemory(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ramCleaned = MemoryUtils.cleanMemory(mContext);
                    showUpdatedAvailableRam();
                    Toast.makeText(mContext, "A total clean-up: " + ramCleaned + "M", Toast.LENGTH_SHORT).show();
                } catch (RuntimeException re) {
                    re.getMessage().toString();
                } catch (Exception e) {
                    e.getMessage().toString();
                }

            }
        });
    }

    /**
     * THIS METHOD WILL UPDATE THE PROGRESS BAR ACCCORDING THE USAGE OF RAM.
     * @param ctx
     */

    private void showRamProgressBar(final Context ctx){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    ActivityManager actManager = (ActivityManager) ctx.getSystemService(ACTIVITY_SERVICE);
                    ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
                    actManager.getMemoryInfo(memInfo);
                    long totalMemory = memInfo.totalMem;
                    long availableMem = memInfo.availMem;
                    int result = (int)totalMemory - (int) availableMem;

                    if (null != progressBarDeterminate){
                        progressBarDeterminate.setMax((int) totalMemory);
                        progressBarDeterminate.setProgress(result);
                    }

                }catch (RuntimeException re){
                    re.getMessage().toString();
                }catch (Exception e){
                    e.getMessage().toString();
                }

            }
        });
        thread.start();
    }

    /**
     * AFTER DELETING THE BACKGROUND APPS, THIS METHOD IS CALLED TO
     * AGAIN UPDATE THE CONTENTS AND DISPLAY THE PROGRESS BAR
     */

    private void showUpdatedAvailableRam(){
        new LoadProcessesInBackground().execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }

    /**
     * PRIVATE INNER CLASS THAT WILL PERFORM THE OPERATION TO SEARCH FOR RUNNING
     * BACKGROUND TAKS OF RAM
     */

    public class LoadProcessesInBackground extends AsyncTask<Void, String, Void> {

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
                getAvailableRam();

            } else {
                // In case there are no processes running (not a chance :))
                Toast.makeText(getApplicationContext(), "No application are running", Toast.LENGTH_LONG).show();
            }
        }
    }
}
