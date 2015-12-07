package com.appman.appmanager.activities;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.appman.appmanager.R;
import com.appman.appmanager.utils.MemoryUtils;
import com.gc.materialdesign.views.ButtonFlat;

/**
 * Created by rudhraksh.pahade on 02-12-2015.
 */
public class RamInfo extends AppCompatActivity {
    private static final String TAG = RamInfo.class.getSimpleName();

    Context mContext;
    Toolbar toolbar;

    TextView txtTotalRam, txtUsedRam, txtRamMsg;
    //Button btnCleanRam;
    ButtonFlat btnCleanRam;


    private Typeface font;
    private String totalRam;
    private long usedRam;
    private long ramCleaned;
    private long calculationOfRam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ram_info);
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
        txtTotalRam = (TextView) findViewById(R.id.textViewTotalRam);
        txtUsedRam = (TextView) findViewById(R.id.textViewUsedRam);
        txtRamMsg = (TextView) findViewById(R.id.txtViewRamMsg);
        btnCleanRam = (ButtonFlat) findViewById(R.id.buttonCleanRam);

        //font = Typeface.createFromAsset(getAssets(), "fonts/Avenir-Book.ttf");
        getAvailableRam();

    }

    private void getAvailableRam(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                totalRam = MemoryUtils.getTotalRAM();
                usedRam = MemoryUtils.getAvailMemory(mContext);
                calculationOfRam = MemoryUtils.calculateRam(mContext);

                Log.i(TAG, "TOTAL RAM :- " + totalRam);
                Log.i(TAG, "Available :- " + usedRam);
                Log.i(TAG,  "FREE RAM :- "+ calculationOfRam);

                txtTotalRam.setText(getString(R.string.total_ram) + " " + totalRam);
                txtTotalRam.setTypeface(font);

                txtUsedRam.setText(getString(R.string.available_ram) + " " + usedRam + " " + "M");
                txtUsedRam.setTypeface(font);

                txtRamMsg.setTypeface(font);
            }
        });
        //btnCleanRam.setTypeface(font);
        btnCleanRam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void showUpdatedAvailableRam(){
        usedRam = MemoryUtils.getAvailMemory(getApplicationContext());
        txtUsedRam.setText(getString(R.string.available_ram) + " " + usedRam + " " + "M");
        txtUsedRam.setTypeface(font);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }

}
