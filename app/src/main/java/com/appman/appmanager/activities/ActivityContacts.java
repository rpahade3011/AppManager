package com.appman.appmanager.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.R;
import com.appman.appmanager.async.BackupContactsInBackground;
import com.appman.appmanager.async.LoadContactsInBackground;
import com.appman.appmanager.models.ContactsInfo;
import com.appman.appmanager.utils.AppPreferences;
import com.appman.appmanager.utils.UtilsUI;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
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
    public static RelativeLayout relativeLayoutContactsCount;
    public static ProgressWheel progressWheel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        this.appPreferences = AppManagerApplication.getAppPreferences();

        setUpToolbar();
        initViews();
        loadContactsInBackground();
        showInterstitialAd();
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

    /**
     * REFERENCING THE VIEWS FROM {@link R.layout.activity_contacts}
     */
    private void initViews(){
        listViewContacts = (ListView) findViewById(R.id.listViewContacts);
        progressWheel = (ProgressWheel) findViewById (R.id.progress);
        txtContactsCount = (TextView) findViewById (R.id.txtViewContactsCount);
        relativeLayoutContactsCount = (RelativeLayout) findViewById (R.id.linearLayoutContactsCount);
    }

    /**
     * METHOD TO LOAD ALL CONTACTS WHICH ARE STORED IN THE DEVICE.
     * THIS METHOD CALLS TO {@link LoadContactsInBackground} ASYNC TASK AND PERFORM
     * THE LOADING PROCEDURE AND SHOWS IT IN LISTVIEW.
     */

    private void loadContactsInBackground(){
        try{
            new LoadContactsInBackground(ActivityContacts.this).execute();
        }catch (Exception e){
            e.getMessage().toString();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contacts_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_backup_contacts:
                try{
                    new BackupContactsInBackground(ActivityContacts.this).execute();
                }catch (Exception e){
                    e.getMessage().toString();
                }
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * THIS METHOD IS TO SHOW THE INTERSTITIAL ADS ON THE APP.
     */
    private void showInterstitialAd(){
        final InterstitialAd interstitialAd = new InterstitialAd(ActivityContacts.this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.ad_mob_interstitial_id));
        AdView adView = (AdView) findViewById (R.id.adView);
        adView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();

        adView.loadAd(adRequest);
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (interstitialAd.isLoaded()){
                    interstitialAd.show();
                }
            }
        });
    }
}
