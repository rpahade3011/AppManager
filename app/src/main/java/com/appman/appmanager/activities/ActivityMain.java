package com.appman.appmanager.activities;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.appman.appmanager.R;
import com.appman.appmanager.fragments.FragmentAbout;
import com.appman.appmanager.fragments.FragmentFavorites;
import com.appman.appmanager.fragments.FragmentHome;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

/**
 * Created by rudhraksh.pahade on 11/2/2016.
 */

public class ActivityMain extends AppCompatActivity {

    private static final String LogTag = "ActivityMain";

    public static Toolbar mainToolbar = null;
    private FragmentManager mFragmentManager = null;
    private FragmentTransaction mFragmentTransaction = null;
    public static BottomBar bottomBar = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        setupBottomNavigationBar();
    }

    private void setupToolbar() {
        mainToolbar = (Toolbar) findViewById (R.id.mainToolbar);
        if (mainToolbar != null) {
            setSupportActionBar(mainToolbar);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            mainToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    private void setupBottomNavigationBar() {
        bottomBar = (BottomBar) findViewById(R.id.bottom_navigation);
        bottomBar.setDefaultTab(R.id.tab_home);

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    // Home
                    case R.id.tab_home:
                        changeFragment(new FragmentHome());
                        break;
                    // Favorites
                    case R.id.tab_favorites:
                        if (FragmentHome.bb != null) {
                            FragmentHome.bb.removeBadge();
                        }
                        FragmentFavorites fragmentFavorites = FragmentFavorites.newInstance(FragmentHome.appFavoriteAdapter);
                        changeFragment(fragmentFavorites);
                        break;
                    // Search
                    case R.id.tab_clean:
                        break;
                    // About
                    case R.id.tab_about:
                        changeFragment(new FragmentAbout());
                        break;
                    // More
                    case R.id.tab_more:
                        break;
                    default:
                        break;
                }
            }
        });
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                switch (tabId) {
                    // Home
                    case R.id.tab_home:
                        changeFragment(new FragmentHome());
                        break;
                    // Favorites
                    case R.id.tab_favorites:
                        FragmentFavorites fragmentFavorites = FragmentFavorites.newInstance(FragmentHome.appFavoriteAdapter);
                        changeFragment(fragmentFavorites);
                        break;
                    // Search
                    case R.id.tab_clean:
                        break;
                    // About
                    case R.id.tab_about:
                        changeFragment(new FragmentAbout());
                        break;
                    // More
                    case R.id.tab_more:
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @SuppressLint("CommitTransaction")
    private void changeFragment(Fragment mFragment) {
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.container, mFragment);
        mFragmentTransaction.commit();
    }
}
