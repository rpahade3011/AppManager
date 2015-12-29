package com.appman.appmanager.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.R;
import com.appman.appmanager.async.LoadStorageInBackground;
import com.appman.appmanager.async.StoragePercentageInBackground;
import com.appman.appmanager.utils.AppPreferences;
import com.appman.appmanager.utils.UtilsUI;
import com.appman.appmanager.views.StorageSimpleView;
import com.appman.appmanager.views.TextView_Regular;
import com.gc.materialdesign.views.ProgressBarDeterminate;
import com.pnikosis.materialishprogress.ProgressWheel;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by rudhraksh.pahade on 29-12-2015.
 */
public class FragmentStorage extends AppCompatActivity{

    private AppPreferences appPreferences;

    Toolbar toolbar;
    //public static StorageSimpleView storageSimpleViewInternal;
    //public static MaterialProgressBar storageSimpleViewInternal;
    public static ProgressBarDeterminate storageSimpleViewInternal;

    public static ProgressWheel progressWheel;

    public static TextView_Regular txtInternal;
    //public static TextView txtInternal;
    public static TextView_Regular txtInternalPercent;
    //public static TextView txtInternalPercent;

    public static String sd_card_total_space;
    public static String sd_card_used_space;
    public static String sd_card_free_space;
    public static float sd_card_total_per;
    public static float sd_card_used_per;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_storage);
        this.appPreferences = AppManagerApplication.getAppPreferences();
        setInitialConfiguration();
        initializeViews();
        loadStorageInBackground();
        calculateStoragePercentage();
    }



    /**
     * SETTING UP THE TOOLBAR WITH TRANSLUCENT STATUS BAR
     */

    private void setInitialConfiguration(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null ) {
            getSupportActionBar().setTitle("Storage Details");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setBackgroundColor(getResources().getColor(R.color.list_txt_info_3));
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(UtilsUI.darker(getResources().getColor(R.color.list_txt_info_3), 0.8));


            toolbar.setBackgroundColor(getResources().getColor(R.color.list_txt_info_3));
            if (!appPreferences.getNavigationBlackPref()) {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.list_txt_info_3));
            }
        }
    }

    private void initializeViews(){
        progressWheel = (ProgressWheel) findViewById(R.id.progressWheel);
        txtInternal = (TextView_Regular) findViewById(R.id.txtInternal);
        txtInternalPercent = (TextView_Regular) findViewById (R.id.txtPercent1);
        //storageSimpleViewInternal = (StorageSimpleView) findViewById (R.id.memoryInternal);
        //storageSimpleViewInternal = (MaterialProgressBar) findViewById (R.id.memoryInternal);
        storageSimpleViewInternal = (ProgressBarDeterminate) findViewById (R.id.memoryInternal);
    }

    private void loadStorageInBackground() {
        new LoadStorageInBackground(FragmentStorage.this).execute();
    }

    private void calculateStoragePercentage() {
        new StoragePercentageInBackground(FragmentStorage.this).execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }
}
