package com.appman.appmanager.activities;

import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.R;
import com.appman.appmanager.adapter.ListAdapter;
import com.appman.appmanager.utils.AppPreferences;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.List;

/**
 * Created by rudhraksh.pahade on 03-12-2015.
 */
public class ProcessActivity extends ListActivity{

    Context mContext;
    // Load Settings
    private AppPreferences appPreferences;
    private ProgressWheel progressWheel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        this.appPreferences = AppManagerApplication.getAppPreferences();

        progressWheel = (ProgressWheel) findViewById(R.id.progress);

        progressWheel.setBarColor(appPreferences.getPrimaryColorPref());
        progressWheel.setVisibility(View.VISIBLE);

        new MemoryInfoInBackground().execute();

    }

    public class MemoryInfoInBackground extends AsyncTask<Void, String, Void> {

        private Integer totalRunningApps;

        @Override
        protected Void doInBackground(Void... params) {
            // Get running processes
            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();
            totalRunningApps = runningProcesses.size();
            if (runningProcesses != null && runningProcesses.size() > 0) {
                // Set data to the list adapter
                setListAdapter(new ListAdapter(mContext, runningProcesses));
            } else {
                // In case there are no processes running (not a chance :))
                Toast.makeText(getApplicationContext(), "There are no applications running", Toast.LENGTH_LONG).show();
            }
            return null;
        }
    }
}
