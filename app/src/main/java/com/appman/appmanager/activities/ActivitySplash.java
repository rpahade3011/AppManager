package com.appman.appmanager.activities;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appman.appmanager.R;

/**
 * Created by rudhraksh.pahade on 4/5/2017.
 */

public class ActivitySplash extends AppCompatActivity {
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
    private static final String LOG_TAG = "ActivitySplash";

    private long SPLASH_SCREEN_MILLIS = 3000;
    private long SPLASH_SCREEN_MILLIS_INTERVAL = 1000;

    private Animation mainAnimation;
    private Animation appImageAnimation;

    private TextView tvSplashAppName;
    private ImageView imgVwAppIcon;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initViews();
        showSplash();
    }

    private void initViews() {
        relativeLayout = (RelativeLayout) findViewById(R.id.rel_lay);
        imgVwAppIcon = (ImageView) findViewById(R.id.imgVwAppIcon);
        tvSplashAppName = (TextView) findViewById(R.id.tvSplashAppName);
        mainAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_main_animation_alpha);

        mainAnimation.reset();
        relativeLayout.clearAnimation();
        relativeLayout.startAnimation(mainAnimation);

        appImageAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_app_icon_translate);
        appImageAnimation.reset();
        imgVwAppIcon.clearAnimation();
        imgVwAppIcon.setAnimation(appImageAnimation);

    }

    private void showSplash() {
        CountDownTimer splashScreenTimer = new CountDownTimer(SPLASH_SCREEN_MILLIS,
                SPLASH_SCREEN_MILLIS_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Do nothing
            }

            @Override
            public void onFinish() {
                if (tvSplashAppName.getVisibility() == View.GONE) {
                    tvSplashAppName.setVisibility(View.VISIBLE);

                    mainAnimation = AnimationUtils.loadAnimation(ActivitySplash.this, R.anim.slide_in_right);
                    mainAnimation.reset();
                    tvSplashAppName.clearAnimation();
                    tvSplashAppName.setAnimation(mainAnimation);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(LOG_TAG, "Starting Main activity");
                        goToMainActivity();
                    }
                }, 500);
            }
        };
        splashScreenTimer.start();
    }

    private void goToMainActivity() {
        Intent mainIntent = new Intent(ActivitySplash.this, MainActivity.class);
        startActivity(mainIntent);

        ActivitySplash.this.finish();
    }
}
