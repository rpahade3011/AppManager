package com.appman.appmanager.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.R;
import com.appman.appmanager.utils.AppPreferences;
import com.appman.appmanager.utils.UtilsUI;


/**
 * Created by Rudraksh on 22-Jan-16.
 */
public class ActivityAfterMemoryCleaned extends AppCompatActivity{

    private ImageView circleImageView;
    private TextView txtMemoryCleaned;
    private TextView txtMemorySuffix;

    private String memoryKilled;
    private String memorySuffix;
    AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.after_memory_clean_activity);
        this.appPreferences = AppManagerApplication.getAppPreferences();
        setInitialConfiguration();
        circleImageView = (ImageView) findViewById (R.id.circleImageView);
        txtMemoryCleaned = (TextView) findViewById (R.id.txtMemoryCleaned);
        txtMemorySuffix = (TextView) findViewById (R.id.txtMemoryCleanedSuffix);

        memoryKilled = getIntent().getStringExtra("memoryKilled");
        memorySuffix = getIntent().getStringExtra("memorySuffix");

        txtMemoryCleaned.setText(memoryKilled);
        txtMemorySuffix.setText(memorySuffix);

        // To close the activity in 3 Secs and move the intent again to {@link MemoryCleanActivity}
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent mainIntent = new Intent(ActivityAfterMemoryCleaned.this, MemoryCleanActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityAfterMemoryCleaned.this.startActivity(mainIntent);
                ActivityAfterMemoryCleaned.this.finish();
            }
        }, 3000);

    }
    private void setInitialConfiguration() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        if (toolbar != null) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.title_bg));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(UtilsUI.darker(getResources().getColor(R.color.title_bg), 0.8));
            if (toolbar != null) {
                toolbar.setBackgroundColor(getResources().getColor(R.color.title_bg));
            }
            if (!appPreferences.getNavigationBlackPref()) {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.title_bg));
            }
        }

    }
}
