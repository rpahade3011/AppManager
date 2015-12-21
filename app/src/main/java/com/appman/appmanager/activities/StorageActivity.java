package com.appman.appmanager.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.appman.appmanager.R;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.pnikosis.materialishprogress.ProgressWheel;

/**
 * Created by rudhraksh.pahade on 21-12-2015.
 */
public class StorageActivity extends AppCompatActivity{
    Toolbar toolbar;
    ProgressWheel progressWheel;
    ProgressBarCircularIndeterminate progressBarCircularIndeterminate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }
}
