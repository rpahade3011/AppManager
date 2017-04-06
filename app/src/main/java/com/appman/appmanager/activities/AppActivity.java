package com.appman.appmanager.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.appman.appmanager.AppInfo;
import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.BuildConfig;
import com.appman.appmanager.R;
import com.appman.appmanager.async.ExtractFileInBackground;
import com.appman.appmanager.utils.AdModDeviceIdUtils;
import com.appman.appmanager.utils.AppPreferences;
import com.appman.appmanager.utils.UtilsApp;
import com.appman.appmanager.utils.UtilsDialog;
import com.appman.appmanager.utils.UtilsUI;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Set;

public class AppActivity extends AppCompatActivity {
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

    // UI variables
    private FloatingActionsMenu fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        this.context = this;
        this.activity = (Activity) context;
        this.appPreferences = AppManagerApplication.getAppPreferences();

        getInitialConfiguration();
        setInitialConfiguration();
        setScreenElements();
        showInterstitialAd();

    }

    private void setInitialConfiguration() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null ) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setBackgroundColor(getResources().getColor(R.color.card_blue));
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(UtilsUI.darker(getResources().getColor(R.color.card_blue), 0.8));


            toolbar.setBackgroundColor(getResources().getColor(R.color.card_blue));
            if (!appPreferences.getNavigationBlackPref()) {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.card_blue));
            }
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

        // Configure Colors

        header.setBackgroundColor(Color.parseColor("#00BED4"));

        // CardView
        if (appInfo.isSystem()) {
            icon_googleplay.setVisibility(View.GONE);
            start.setVisibility(View.GONE);
        } else {
            googleplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UtilsApp.goToGooglePlay(context, appInfo.getAPK());
                }
            });

            googleplay.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ClipData clipData;

                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipData = ClipData.newPlainText("text", appInfo.getAPK());
                    clipboardManager.setPrimaryClip(clipData);
                    UtilsDialog.showSnackbar(activity, context.getResources().getString(R.string.copied_clipboard), null, null, 2).show();

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
                        UtilsDialog.showSnackbar(activity, String.format(getResources().getString(R.string.dialog_cannot_open), appInfo.getName()), null, null, 2).show();
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
                MaterialDialog dialog = UtilsDialog.showTitleContentWithProgress(context
                        , String.format(getResources().getString(R.string.dialog_saving), appInfo.getName())
                        , getResources().getString(R.string.dialog_saving_description));
                new ExtractFileInBackground(context, dialog, appInfo).execute();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UNINSTALL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.i("App", "OK");
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            } else if (resultCode == RESULT_CANCELED) {
                Log.i("App", "CANCEL");
            }
        }
    }

    private void getInitialConfiguration() {
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

    private void showInterstitialAd() {
        MobileAds.initialize(AppActivity.this, getResources().getString(R.string.ad_mob_interstitial_id));
        AdView adView = (AdView) findViewById(R.id.adView);

        assert adView != null;
        adView.setVisibility(View.VISIBLE);

        if (BuildConfig.DEBUG) {
            String deviceIdForTestAds = AdModDeviceIdUtils.getAdMobDeviceId(AppActivity.this);
            Log.e("AppActivity", "Hashed device id to load test ads - " + deviceIdForTestAds);

            AdRequest adRequest = new AdRequest.Builder().addTestDevice(deviceIdForTestAds).build();;
            adView.loadAd(adRequest);
        } else {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();

            // Start loading the ad in the background.
            adView.loadAd(adRequest);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        item_favorite = menu.findItem(R.id.action_favorite);
        UtilsApp.setAppFavorite(context, item_favorite, UtilsApp.isAppFavorite(appInfo.getAPK(), appsFavorite));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;
            case R.id.action_favorite:
                if (UtilsApp.isAppFavorite(appInfo.getAPK(), appsFavorite)) {
                    appsFavorite.remove(appInfo.getAPK());
                    appPreferences.setFavoriteApps(appsFavorite);
                } else {
                    appsFavorite.add(appInfo.getAPK());
                    appPreferences.setFavoriteApps(appsFavorite);
                }
                UtilsApp.setAppFavorite(context, item_favorite, UtilsApp.isAppFavorite(appInfo.getAPK(), appsFavorite));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
