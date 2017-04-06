package com.appman.appmanager.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.R;
import com.appman.appmanager.utils.AppPreferences;
import com.appman.appmanager.utils.InternetConnection;
import com.appman.appmanager.utils.UtilsApp;
import com.appman.appmanager.utils.UtilsUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class AboutActivity extends AppCompatActivity {
    // Load Settings
    AppPreferences appPreferences;

    // About variables
    private Context context;
    public static String facebook_id = "rudraksh.pahade";
    public static String twitter_id = "pahade_rudraksh";
    private boolean isInternetConnected = false;

    private InternetConnection internetConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        this.appPreferences = AppManagerApplication.getAppPreferences();
        this.context = this;
        internetConnection = new InternetConnection();

        setInitialConfiguration();
        setScreenElements();
        loadAdMob();

    }

    private void setInitialConfiguration() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.action_about);
        if (toolbar != null) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.bkg_card));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(UtilsUI.darker(getResources().getColor(R.color.bkg_card), 0.8));
            if (toolbar != null) {
                toolbar.setBackgroundColor(getResources().getColor(R.color.bkg_card));
            }
            if (!appPreferences.getNavigationBlackPref()) {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.bkg_card));
            }
        }

    }

    private void setScreenElements() {
        AppCompatImageView imageViewProfilePic = (AppCompatImageView) findViewById (R.id.imageViewProfilePic);
        TextView header = (TextView) findViewById(R.id.header);
        TextView appNameVersion = (TextView) findViewById(R.id.app_name);
        CardView about_1 = (CardView) findViewById(R.id.about_1);
        CardView about_googleplay = (CardView) findViewById(R.id.about_googleplay);
        CardView about_facebook = (CardView) findViewById (R.id.about_facebook);
        CardView about_twitter = (CardView) findViewById (R.id.about_twitter);

        try{
            if (imageViewProfilePic != null) {
                imageViewProfilePic.setImageResource(R.mipmap.about_rudraksh_pahade_profile_pic);
            }
        }catch (Exception e){
            imageViewProfilePic.setImageResource(R.mipmap.about_rudraksh_pahade_profile_pic);
            Log.e("Exception", e.getMessage());
        }

        if (header != null) {
            header.setBackgroundColor(getResources().getColor(R.color.bkg_card));
        }
        if (appNameVersion != null) {
            appNameVersion.setText(getResources().getString(R.string.app_name) + " " + UtilsApp.getAppVersionName(getApplicationContext()) + "(" + UtilsApp.getAppVersionCode(getApplicationContext()) + ")");
        }

        // If clicks on Google Play
        if (about_googleplay != null) {
            about_googleplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isInternetConnected = internetConnection.isInternetConnection(AboutActivity.this);
                    if (isInternetConnected){
                        try{
                            UtilsApp.goToGooglePlay(context, context.getPackageName());
                        }catch (Exception e){
                            Log.e("Exception", e.getMessage());
                        }
                    }else {
                        startActivity(new Intent(AboutActivity.this,ActivityNoInternetConnection.class));
                    }
                }
            });
        }
        // If clicks on Facebook
        if (about_facebook != null) {
            about_facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isInternetConnected = internetConnection.isInternetConnection(AboutActivity.this);
                    if (isInternetConnected){
                        try{
                            //UtilsApp.goToFacebook(context, facebook_id);
                            Intent facebookIntent = UtilsApp.getFacebookIntent(context, facebook_id);
                            context.startActivity(facebookIntent);
                        }catch (Exception e){
                            Log.e("Exception", e.getMessage());
                        }
                    }else {
                        startActivity(new Intent(AboutActivity.this,ActivityNoInternetConnection.class));
                    }

                }
            });
        }
        // If clicks on Twitter
        if (about_twitter != null) {
            about_twitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isInternetConnected = internetConnection.isInternetConnection(AboutActivity.this);
                    if (isInternetConnected){
                        try{
                            //UtilsApp.goToTwitter(context, twitter_id);
                            Intent twitterIntent = UtilsApp.getTwitterIntent(context, twitter_id);
                            context.startActivity(twitterIntent);
                        }catch (Exception e){
                            Log.e("Exception", e.getMessage());
                        }
                    }else {
                        startActivity(new Intent(AboutActivity.this,ActivityNoInternetConnection.class));
                    }

                }
            });
        }
    }

    private void loadAdMob(){
        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        if (adView != null) {
            adView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }

}
