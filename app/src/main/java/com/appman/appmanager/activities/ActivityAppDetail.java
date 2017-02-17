package com.appman.appmanager.activities;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.R;
import com.appman.appmanager.async.ExtractFileInBackground;
import com.appman.appmanager.interfaces.OnRevealAnimationListener;
import com.appman.appmanager.models.AppInfo;
import com.appman.appmanager.utils.AppPreferences;
import com.appman.appmanager.utils.GUIUtils;
import com.appman.appmanager.utils.Utility;
import com.appman.appmanager.utils.UtilsDialog;

import java.util.Set;

/**
 * Created by rudhraksh.pahade on 11/29/2016.
 */

public class ActivityAppDetail extends AppCompatActivity {

    private static final String LogTag = "ActivityAppDetail";
    private Toolbar appDetailToolbar = null;
    private RelativeLayout relativeLayoutMainAppDetails;

    // Load Settings
    private AppPreferences appPreferences;

    // General variables
    private AppInfo appInfo;
    private Set<String> appsFavorite;
    private Set<String> appsHidden;

    // Configuration variables
    private int UNINSTALL_REQUEST_CODE = 1;
    private Context context;
    private Activity activity;
    private MenuItem item_favorite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);
        this.context = getApplicationContext();
        this.activity = ActivityAppDetail.this;
        this.appPreferences = AppManagerApplication.getAppPreferences();
        this.relativeLayoutMainAppDetails = (RelativeLayout) findViewById (R.id.relativeLayoutMainAppDetails);
        setUpEnterAnimations();
        getIntentData();
        setUpToolBar();
        setScreenElements();
    }

    @SuppressLint("NewApi")
    private void setUpEnterAnimations() {
        Transition transition = TransitionInflater.from(this)
                .inflateTransition(R.transition.changebounds_with_arcmotion);
        transition.setDuration(300);
        getWindow().setSharedElementEnterTransition(transition);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                animateRevealShow(relativeLayoutMainAppDetails);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }

    private void animateRevealShow(final View viewRoot) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        GUIUtils.animateRevealShow(this, viewRoot, relativeLayoutMainAppDetails.getWidth() / 2, R.color.white,
                cx, cy, new OnRevealAnimationListener() {
                    @Override
                    public void onRevealHide() {

                    }

                    @Override
                    public void onRevealShow() {
                        setScreenElements();
                    }
                });
    }

    private void getIntentData() {
        String appName = getIntent().getStringExtra("app_name");
        String appApk = getIntent().getStringExtra("app_apk");
        String appVersion = getIntent().getStringExtra("app_version");
        String appSource = getIntent().getStringExtra("app_source");
        String appData = getIntent().getStringExtra("app_data");
        Bitmap bitmap = getIntent().getParcelableExtra("app_icon");
        Drawable appIcon = new BitmapDrawable(getResources(), bitmap);
        Boolean appIsSystem = getIntent().getExtras().getBoolean("app_isSystem");

        appInfo = new AppInfo(appName, appApk, appVersion, appSource, appData, appIcon, appIsSystem);
        appsFavorite = appPreferences.getFavoriteApps();
        appsHidden = appPreferences.getHiddenApps();
    }

    private void setUpToolBar() {
        this.appDetailToolbar = (Toolbar) findViewById (R.id.appDetailToolbar);
        if (appDetailToolbar != null) {
            setSupportActionBar(appDetailToolbar);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            appDetailToolbar.setBackgroundColor(getResources().getColor(R.color.list_txt_info_3));
        }
        appDetailToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.list_txt_info_3));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.list_txt_info_3));
        }
    }

    private void setScreenElements() {
        TextView header = (TextView) findViewById(R.id.header);
        ImageView icon = (ImageView) findViewById(R.id.app_icon);
        ImageView icon_googleplay = (ImageView) findViewById(R.id.app_googleplay);
        TextView name = (TextView) findViewById(R.id.app_name);
        TextView version = (TextView) findViewById(R.id.app_version);
        TextView apk = (TextView) findViewById(R.id.app_apk);
        CardView googleplay = (CardView) findViewById(R.id.id_card);
        CardView start = (CardView) findViewById(R.id.start_card);
        CardView extract = (CardView) findViewById(R.id.extract_card);
        CardView uninstall = (CardView) findViewById(R.id.uninstall_card);

        icon.setImageDrawable(appInfo.getIcon());
        name.setText(appInfo.getName());
        apk.setText(appInfo.getAPK());
        version.setText(appInfo.getVersion());

        header.setBackgroundColor(getResources().getColor(R.color.list_txt_info_3));

        // CardView
        if (appInfo.isSystem()) {
            icon_googleplay.setVisibility(View.GONE);
            start.setVisibility(View.GONE);
        } else {
            googleplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utility.goToGooglePlay(context, appInfo.getAPK());
                }
            });

            googleplay.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ClipData clipData;

                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipData = ClipData.newPlainText("text", appInfo.getAPK());
                    clipboardManager.setPrimaryClip(clipData);
                    UtilsDialog.showSnackbar(activity, context.getResources()
                            .getString(R.string.copied_clipboard), null, null, 2).show();

                    return false;
                }
            });

            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (appInfo.getName().equalsIgnoreCase("AppManager")){
                            UtilsDialog.showTitleContent(activity, "ERROR","This app can't be opened, because this app is already in its working state.");
                        }
                        else{
                            Intent intent = getPackageManager().getLaunchIntentForPackage(appInfo.getAPK());
                            startActivity(intent);
                        }

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        UtilsDialog.showSnackbar(activity, String.format(getResources()
                                .getString(R.string.dialog_cannot_open), appInfo.getName()), null, null, 2).show();
                    }
                }
            });

            uninstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (appInfo.getName().equalsIgnoreCase("AppManager")){
                        UtilsDialog.showTitleContent(activity, "UNINSTALL ERROR","This app can't be uninstalled, because this app is in its working state. Try uninstalling manually.");
                    }else{
                        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                        intent.setData(Uri.parse("package:" + appInfo.getAPK()));
                        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                        startActivityForResult(intent, UNINSTALL_REQUEST_CODE);
                    }

                }
            });
        }

        extract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDialog dialog = UtilsDialog.showTitleContentWithProgress(activity
                        , String.format(getResources().getString(R.string.dialog_saving), appInfo.getName())
                        , getResources().getString(R.string.dialog_saving_description));
                new ExtractFileInBackground(activity, dialog, appInfo).execute();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UNINSTALL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.e(LogTag,"App uninstall - " + "OK");
                Intent intent = new Intent(context, ActivityMain.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            } else if (resultCode == RESULT_CANCELED) {
                Log.i(LogTag,"App Uninstall - " + "CANCEL");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        item_favorite = menu.findItem(R.id.action_favorite);
        Utility.setAppFavorite(context, item_favorite, Utility.isAppFavorite(appInfo.getAPK(), appsFavorite));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;
            case R.id.action_favorite:
                if (Utility.isAppFavorite(appInfo.getAPK(), appsFavorite)) {
                    appsFavorite.remove(appInfo.getAPK());
                    appPreferences.setFavoriteApps(appsFavorite);
                } else {
                    appsFavorite.add(appInfo.getAPK());
                    appPreferences.setFavoriteApps(appsFavorite);
                }
                Utility.setAppFavorite(context, item_favorite, Utility.isAppFavorite(appInfo.getAPK(), appsFavorite));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
