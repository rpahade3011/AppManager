package com.appman.appmanager.activities;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.R;
import com.appman.appmanager.utils.AppPreferences;
import com.appman.appmanager.utils.UtilsApp;
import com.appman.appmanager.utils.UtilsUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class AboutActivity extends AppCompatActivity {
    // Load Settings
    AppPreferences appPreferences;

    // About variables
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        this.appPreferences = AppManagerApplication.getAppPreferences();
        this.context = this;

        setInitialConfiguration();
        setScreenElements();
        loadAdMob();

    }

    private void setInitialConfiguration() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.action_about);
        toolbar.setBackgroundColor(getResources().getColor(R.color.bkg_card));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(UtilsUI.darker(getResources().getColor(R.color.bkg_card), 0.8));
            toolbar.setBackgroundColor(getResources().getColor(R.color.bkg_card));
            if (!appPreferences.getNavigationBlackPref()) {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.bkg_card));
            }
        }

    }

    private void setScreenElements() {
        TextView header = (TextView) findViewById(R.id.header);
        TextView appNameVersion = (TextView) findViewById(R.id.app_name);
        CardView about_1 = (CardView) findViewById(R.id.about_1);
        //CardView about_2 = (CardView) findViewById(R.id.about_2);
        CardView about_googleplay = (CardView) findViewById(R.id.about_googleplay);
        //CardView about_googleplus = (CardView) findViewById(R.id.about_googleplus);

        //header.setBackgroundColor(appPreferences.getPrimaryColorPref());
        header.setBackgroundColor(getResources().getColor(R.color.bkg_card));
        appNameVersion.setText(getResources().getString(R.string.app_name) + " " + UtilsApp.getAppVersionName(getApplicationContext()) + "(" + UtilsApp.getAppVersionCode(getApplicationContext()) + ")");
//        about_1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                UtilsApp.goToGooglePlus(context, "109312616470328191163");
//            }
//        });
//        about_2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                UtilsApp.goToGooglePlus(context, "+javitoro95");
//            }
//        });
        about_googleplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilsApp.goToGooglePlay(context, context.getPackageName());
            }
        });
//        about_googleplus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                UtilsApp.goToGooglePlus(context, "109312616470328191163");
//            }
//        });
    }

    private void loadAdMob(){
        Toast.makeText(context, "Inside AdMob", Toast.LENGTH_SHORT).show();
        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }

}
