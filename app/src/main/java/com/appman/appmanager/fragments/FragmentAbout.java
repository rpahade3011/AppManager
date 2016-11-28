package com.appman.appmanager.fragments;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

import com.appman.appmanager.R;

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
            rootView = inflater.inflate(R.layout.activity_about, null, false);
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
