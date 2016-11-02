package com.appman.appmanager.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.appman.appmanager.R;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;

/**
 * Created by rudhraksh.pahade on 11/2/2016.
 */

public class ActivityMain extends AppCompatActivity {

    private static final String LogTag = "ActivityMain";

    private Toolbar mainToolbar = null;
    private SpaceNavigationView spaceNavigationView = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        setupBottomNavigationBar(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        spaceNavigationView.onSaveInstanceState(outState);
    }

    private void setupToolbar() {
        mainToolbar = (Toolbar) findViewById (R.id.mainToolbar);
        if (mainToolbar != null) {
            setSupportActionBar(mainToolbar);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            mainToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    private void setupBottomNavigationBar(Bundle savedInstanceState) {
        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);

        spaceNavigationView.addSpaceItem(new SpaceItem("HOME", R.drawable.ic_home));
        spaceNavigationView.addSpaceItem(new SpaceItem("FAVORITES", R.drawable.ic_favorite));
        spaceNavigationView.addSpaceItem(new SpaceItem("SEARCH", R.drawable.ic_search));
        spaceNavigationView.addSpaceItem(new SpaceItem("ABOUT", R.drawable.ic_about_person));

        spaceNavigationView.setSpaceBackgroundColor(getResources().getColor(R.color.secondary_text));
        spaceNavigationView.changeCenterButtonIcon(R.drawable.ic_search);
        spaceNavigationView.setCentreButtonColor(getResources().getColor(R.color.colorPrimary));
        spaceNavigationView.setActiveSpaceItemColor(getResources().getColor(R.color.title_bg));
        spaceNavigationView.setCentreButtonIconColorFilterEnabled(true);
        spaceNavigationView.showIconOnly();

    }
}
