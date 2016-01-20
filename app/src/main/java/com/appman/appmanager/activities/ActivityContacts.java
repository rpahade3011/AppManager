package com.appman.appmanager.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.R;
import com.appman.appmanager.async.LoadContactsInBackground;
import com.appman.appmanager.models.ContactsInfo;
import com.appman.appmanager.utils.AppPreferences;
import com.appman.appmanager.utils.UtilsUI;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;

/**
 * Created by Rudraksh on 19-Jan-16.
 */
public class ActivityContacts extends AppCompatActivity {

    private AppPreferences appPreferences;
    public static ListView listViewContacts;
    private Toolbar toolbar;
    public static ArrayList<ContactsInfo> arrayList;

    public static TextView txtContactsCount;
    public static ProgressWheel progressWheel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        this.appPreferences = AppManagerApplication.getAppPreferences();

        setUpToolbar();
        initViews();
        loadContactsInBackground();
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
            getSupportActionBar().setTitle(getResources().getString(R.string.action_contacts));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setBackgroundColor(getResources().getColor(R.color.md_purple_500));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            // Setting navigation bar color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(UtilsUI.darker(getResources().getColor(R.color.md_purple_700), 0.8));


                toolbar.setBackgroundColor(getResources().getColor(R.color.md_purple_500));
                if (!appPreferences.getNavigationBlackPref()) {
                    getWindow().setNavigationBarColor(getResources().getColor(R.color.md_purple_500));
                }
            }
        }
    }

    private void initViews(){
        listViewContacts = (ListView) findViewById(R.id.listViewContacts);
        progressWheel = (ProgressWheel) findViewById (R.id.progress);
        txtContactsCount = (TextView) findViewById (R.id.txtViewContactsCount);
    }

    private void loadContactsInBackground(){
        try{
            new LoadContactsInBackground(ActivityContacts.this).execute();
        }catch (Exception e){
            e.getMessage().toString();
        }
    }
}
