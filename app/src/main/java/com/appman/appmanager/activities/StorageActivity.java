package com.appman.appmanager.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.appman.appmanager.R;
import com.appman.appmanager.async.LoadStorageInBackground;
import com.appman.appmanager.async.StoragePercentageInBackground;
import com.appman.appmanager.progressbars.CircularProgressBar;
import com.pnikosis.materialishprogress.ProgressWheel;

/**
 * Created by rudhraksh.pahade on 21-12-2015.
 */
public class StorageActivity extends AppCompatActivity{
    public static TextView txtStoragePercentage, txtTotalStorage, txtUsedStorage, txtFreeStorage;
    Toolbar toolbar;
    public static ProgressWheel progressWheel;
    public static CircularProgressBar progressBarCircularIndeterminate;
    public static String sd_card_total_space;
    public static String sd_card_used_space;
    public static String sd_card_free_space;
    public static float sd_card_total_per;
    public static float sd_card_used_per;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storage_activity);
        toolbar = (Toolbar)findViewById(R.id.toolbar);

        if (toolbar != null ) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Storage Details");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setBackgroundColor(getResources().getColor(R.color.card_green));
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        initializeViews();
        loadStorageInBackground();
        calculateStoragePercentage();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }

    private void initializeViews(){
        txtStoragePercentage = (TextView) findViewById(R.id.txtStoragePercentage);
        txtTotalStorage = (TextView) findViewById(R.id.textViewTotalStorage);
        txtUsedStorage = (TextView) findViewById(R.id.textViewUsedStorage);
        txtFreeStorage = (TextView) findViewById(R.id.textViewFreeStorage);
        progressWheel = (ProgressWheel)findViewById(R.id.progress);
        progressBarCircularIndeterminate = (CircularProgressBar)findViewById(R.id.progressCirculer);

    }
    private void loadStorageInBackground(){
        new LoadStorageInBackground(StorageActivity.this).execute();
    }
    private void calculateStoragePercentage(){
        new StoragePercentageInBackground(StorageActivity.this).execute();
    }
}
