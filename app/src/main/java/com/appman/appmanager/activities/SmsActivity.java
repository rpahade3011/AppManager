package com.appman.appmanager.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.R;
import com.appman.appmanager.async.BackupSmsInBackground;
import com.appman.appmanager.async.LoadSmsInBackground;
import com.appman.appmanager.models.SmsInfo;
import com.appman.appmanager.utils.AppPreferences;
import com.appman.appmanager.utils.UtilsUI;
import com.gc.materialdesign.views.ButtonRectangle;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;

/**
 * Created by Rudraksh on 23-Dec-15.
 */
public class SmsActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = SmsActivity.class.getSimpleName();

    private AppPreferences appPreferences;

    public static ListView listViewSms;
    private Toolbar toolbar;
    public static ArrayList<SmsInfo> arrayList;

    public static ProgressWheel progressWheel;
    public static TextView txtSmsCount;
    public static RelativeLayout relativeLayoutSmsCount;
    private ButtonRectangle buttonSMSBackup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        this.appPreferences = AppManagerApplication.getAppPreferences();
        setUpToolbar();
        initViews();
        loadSmsInBackground();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }

    /**
     * THIS WILL SET UP THE TOOL BAR AND NAVIGATION BAR OF THE DEVICE,
     * WHICH LAYS IN THE BOTTOM OF SOME DEVICES LIKE NEXUS, HTC, ETC.
     */
    private void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(getResources().getString(R.string.action_sms));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setBackgroundColor(getResources().getColor(R.color.card_orange));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            // Setting navigation bar color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(UtilsUI.darker(getResources().getColor(R.color.card_orange), 0.8));


                toolbar.setBackgroundColor(getResources().getColor(R.color.card_orange));
                if (!appPreferences.getNavigationBlackPref()) {
                    getWindow().setNavigationBarColor(getResources().getColor(R.color.card_orange));
                }
            }
        }
    }

    /**
     * INITIALIZING THE VIEWS
     */
    private void initViews(){
        listViewSms = (ListView)findViewById(R.id.listViewSms);
        relativeLayoutSmsCount = (RelativeLayout) findViewById (R.id.linearLayoutSmsCount);
        progressWheel = (ProgressWheel)findViewById(R.id.progress);
        txtSmsCount = (TextView)findViewById(R.id.txtViewSmsCount);
        buttonSMSBackup = (ButtonRectangle) findViewById (R.id.buttonSMSBackup);
        buttonSMSBackup.setOnClickListener(this);

    }

    /**
     * THIS METHOD WILL CALL THE {@link LoadSmsInBackground} CLASS
     * TO LOAD ALL THE SMSs IN A DIFFERENT ASYNC TASK
     */
    private void loadSmsInBackground(){

        try{
            new LoadSmsInBackground(SmsActivity.this).execute();
        }catch (Exception e){
            e.getMessage().toString();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonSMSBackup:
                try{
                    // To back up the sms
                    new BackupSmsInBackground(SmsActivity.this).execute();
                }catch (Exception e){
                    e.getMessage().toString();
                }
                break;
        }
    }
}
