package com.appman.appmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.appman.appmanager.R;


/**
 * Created by Rudraksh on 22-Jan-16.
 */
public class ActivityAfterMemoryCleaned extends AppCompatActivity{

    private ImageView circleImageView;
    private TextView txtMemoryCleaned;
    private TextView txtMemorySuffix;

    private String memoryKilled;
    private String memorySuffix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.after_memory_clean_activity);
        circleImageView = (ImageView) findViewById (R.id.circleImageView);
        txtMemoryCleaned = (TextView) findViewById (R.id.txtMemoryCleaned);
        txtMemorySuffix = (TextView) findViewById (R.id.txtMemoryCleanedSuffix);

        memoryKilled = getIntent().getStringExtra("memoryKilled");
        memorySuffix = getIntent().getStringExtra("memorySuffix");
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.proc_clean_rotate_anim);
        circleImageView.setAnimation(animation);


        txtMemoryCleaned.setText(memoryKilled);
        txtMemorySuffix.setText(memorySuffix);

        // To close the activity in 3 Secs
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent mainIntent = new Intent(ActivityAfterMemoryCleaned.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityAfterMemoryCleaned.this.startActivity(mainIntent);
                ActivityAfterMemoryCleaned.this.finish();
            }
        }, 3000);

    }
}
