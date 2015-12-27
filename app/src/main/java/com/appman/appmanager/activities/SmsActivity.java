package com.appman.appmanager.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.appman.appmanager.R;
import com.appman.appmanager.adapter.SmsAdapter;
import com.appman.appmanager.async.LoadSmsInBackground;
import com.pnikosis.materialishprogress.ProgressWheel;

/**
 * Created by Rudraksh on 23-Dec-15.
 */
public class SmsActivity extends AppCompatActivity{

    public static final String TAG = SmsActivity.class.getSimpleName();

    public static ListView listViewSms;
    private Toolbar toolbar;
    public static SmsAdapter smsAdapter;
    public static ProgressWheel progressWheel;
    public static TextView txtSmsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        setUpToolbar();
        initViews();
        loadSmsInBackground();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }

    private void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(getResources().getString(R.string.action_sms));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }
    private void initViews(){
        listViewSms = (ListView)findViewById(R.id.listViewSms);
        progressWheel = (ProgressWheel)findViewById(R.id.progress);
        txtSmsCount = (TextView)findViewById(R.id.txtViewSmsCount);
    }
    private void loadSmsInBackground(){
        new LoadSmsInBackground(SmsActivity.this).execute();
    }
}
