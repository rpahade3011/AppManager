package com.appman.appmanager.fragments;

import android.animation.Animator;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.appman.appmanager.R;
import com.appman.appmanager.activities.ActivityMain;

/**
 * Created by rudhraksh.pahade on 11/2/2016.
 */

public class FragmentAbout extends Fragment {

    private static final String LogTag = "FragmentAbout";
    private View rootView = null;
    private ViewGroup mViewGroup = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            mViewGroup = container;
            rootView = inflater.inflate(R.layout.fragment_about, null, false);
            setupToolBar();
            rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                    Animator anim = animateRevealColorFromCoordinates(mViewGroup, R.color.gplus_color_1,
                            mViewGroup.getWidth() / 2, 0);
                    anim.start();
                }
            });
        }
        return rootView;
    }

    private void setupToolBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getActivity().getWindow().setStatusBarColor(Color.parseColor("#FF5252"));
            ActivityMain.mainToolbar.setBackgroundColor(Color.parseColor("#FF5252"));
            getActivity().getWindow().setNavigationBarColor(Color.parseColor("#FF5252"));
        }
    }

    private Animator animateRevealColorFromCoordinates(ViewGroup viewRoot, @ColorRes int color, int x, int y) {
        float finalRadius = (float) Math.hypot(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(viewRoot, x, y, 0, finalRadius);
        }
        //viewRoot.setBackgroundColor(ContextCompat.getColor(getActivity(), color));
        anim.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        return anim;
    }
}
