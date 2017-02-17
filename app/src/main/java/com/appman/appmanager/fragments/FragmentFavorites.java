package com.appman.appmanager.fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.R;
import com.appman.appmanager.activities.ActivityMain;
import com.appman.appmanager.adapter.AppAdapter;
import com.appman.appmanager.interfaces.OnRevealAnimationListener;
import com.appman.appmanager.utils.AppPreferences;
import com.appman.appmanager.utils.GUIUtils;
import com.appman.appmanager.utils.UtilsDialog;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * Created by rudhraksh.pahade on 11/30/2016.
 */

public class FragmentFavorites extends Fragment {
    private static final String LogTag = "FragmentFavorites";

    private AppPreferences appPreferences;

    private RecyclerView recyclerViewFavorites;
    private VerticalRecyclerViewFastScroller verticalRecyclerViewFastScrollerFavorites;
    private AppAdapter appFavAdapter;

    private View rootView = null;
    private ViewGroup mViewGroup = null;
    private RelativeLayout relativeLayoutFavorites;
    private boolean isInitializeComplete = false;


    public static FragmentFavorites newInstance(final AppAdapter appFavoriteAdapter) {
        FragmentFavorites fragmentFavorites = new FragmentFavorites();
        fragmentFavorites.getAdapterValues(appFavoriteAdapter);
        return fragmentFavorites;
    }

    private void getAdapterValues(AppAdapter appAdapter) {
        this.appFavAdapter = appAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_favorites, null, false);
            mViewGroup = container;
            this.appPreferences = AppManagerApplication.getAppPreferences();
            this.relativeLayoutFavorites = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutFavorites);
            animateReveal();
        }
        setUpToolBar();
        return rootView;
    }

    private void animateReveal() {
        int cx = (relativeLayoutFavorites.getLeft() + relativeLayoutFavorites.getRight()) / 2;
        int cy = (relativeLayoutFavorites.getTop() + relativeLayoutFavorites.getBottom()) / 2;
        GUIUtils.animateRevealShow(getActivity(), mViewGroup, relativeLayoutFavorites.getWidth() / 2,
                R.color.white,
                cx, cy, new OnRevealAnimationListener() {
                    @Override
                    public void onRevealHide() {

                    }

                    @Override
                    public void onRevealShow() {
                        initViews();
                    }
                });
    }

    private void setUpToolBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getActivity().getWindow().setStatusBarColor(Color.parseColor("#5D4037"));
            ActivityMain.mainToolbar.setBackgroundColor(Color.parseColor("#5D4037"));
            getActivity().getWindow().setNavigationBarColor(Color.parseColor("#5D4037"));
        }
    }

    private void initViews() {
        recyclerViewFavorites = (RecyclerView) rootView.findViewById(R.id.appFavList);
        verticalRecyclerViewFastScrollerFavorites = (VerticalRecyclerViewFastScroller) rootView.findViewById(R.id.fast_scroller_fav);
        isInitializeComplete = true;
        if (isInitializeComplete) {
            setUpAdapter();
        }
    }

    private void setUpAdapter() {
        if (appFavAdapter.getItemCount() == 0){
            UtilsDialog.showTitleContent(getActivity(), "NO APPS", "You have not added any apps to your favorite list. Try adding some apps by selecting the star icon on app details and refresh your list by pulling down the screen. ");
        }
    }
}
