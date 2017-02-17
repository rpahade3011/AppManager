package com.appman.appmanager.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.TextView;

import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.BuildConfig;
import com.appman.appmanager.R;
import com.appman.appmanager.utils.AdModDeviceIdUtils;
import com.appman.appmanager.utils.AppPreferences;
import com.appman.appmanager.utils.UtilsUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;


/**
 * Created by rudhraksh.pahade on 02-12-2015.
 */
public class DeviceInfo extends AppCompatActivity {

    Toolbar toolbar;
    TextView txtOSVersion, txtVersionRelease, txtApiLevel, txtDevice, txtModel,
    txtProduct, txtBrand, txtDisplay, txtCpuAbi1, txtHardware, txtId, txtManufacturer, txtSerial, txtUser, txtHost;
    ScrollView scrollView;
    private AppPreferences appPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_info);
        this.appPreferences = AppManagerApplication.getAppPreferences();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.action_device));
        toolbar.setBackgroundColor(getResources().getColor(R.color.md_grey_500));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        // Setting navigation bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(UtilsUI.darker(getResources().getColor(R.color.md_grey_700), 0.8));


            toolbar.setBackgroundColor(getResources().getColor(R.color.md_grey_500));
            if (!appPreferences.getNavigationBlackPref()) {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.md_grey_500));
            }
        }

        findViewsById();
        displayDeviceInfo();
        showInterstitialAd();
    }

    private void findViewsById(){
        scrollView = (ScrollView) findViewById (R.id.scrollDeviceInfo);
        txtOSVersion = (TextView) findViewById(R.id.txtOSVersion);
        txtVersionRelease = (TextView)findViewById(R.id.txtOSVersionRelease);
        txtApiLevel = (TextView)findViewById(R.id.txtAPILevel);
        txtDevice = (TextView)findViewById(R.id.txtDevice);
        txtModel = (TextView)findViewById(R.id.txtDeviceModel);
        txtProduct = (TextView)findViewById(R.id.txtDeviceProduct);
        txtBrand = (TextView)findViewById(R.id.txtDeviceBrand);
        txtDisplay = (TextView)findViewById(R.id.txtDisplay);
        txtCpuAbi1 = (TextView)findViewById(R.id.txtCpuAbi1);
        txtHardware = (TextView)findViewById(R.id.txtHardware);
        txtId = (TextView)findViewById(R.id.txtID);
        txtManufacturer = (TextView)findViewById(R.id.txtManufacturer);
        txtSerial = (TextView)findViewById(R.id.txtSerial);
        txtUser = (TextView)findViewById(R.id.txtUser);
        txtHost = (TextView)findViewById(R.id.txtHost);

    }

    private void displayDeviceInfo(){

        String _OSVERSION = System.getProperty("os.version");
        String _RELEASE = android.os.Build.VERSION.RELEASE;
        String _APILEVEL = String.valueOf(android.os.Build.VERSION.SDK_INT);
        String _DEVICE = android.os.Build.DEVICE;
        String _MODEL = android.os.Build.MODEL;
        String _PRODUCT = android.os.Build.PRODUCT;
        String _BRAND = android.os.Build.BRAND;
        String _DISPLAY = android.os.Build.DISPLAY;
        String _CPU_ABI = android.os.Build.CPU_ABI;
        String _HARDWARE = android.os.Build.HARDWARE;
        String _ID = android.os.Build.ID;
        String _MANUFACTURER = android.os.Build.MANUFACTURER;
        String _SERIAL = android.os.Build.SERIAL;
        String _USER = android.os.Build.USER;
        String _HOST = android.os.Build.HOST;

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.card_animation);
        scrollView.setAnimation(animation);

        txtOSVersion.setText(_OSVERSION);
        txtVersionRelease.setText(_RELEASE);
        txtApiLevel.setText(_APILEVEL);
        txtDevice.setText(_DEVICE);
        txtModel.setText(_MODEL);
        txtProduct.setText(_PRODUCT);
        txtBrand.setText(_BRAND);
        txtDisplay.setText(_DISPLAY);
        txtCpuAbi1.setText(_CPU_ABI);
        txtHardware.setText(_HARDWARE);
        txtId.setText(_ID);
        txtManufacturer.setText(_MANUFACTURER);
        txtSerial.setText(_SERIAL);
        txtUser.setText(_USER);
        txtHost.setText(_HOST);

    }

    private void showInterstitialAd() {
        MobileAds.initialize(DeviceInfo.this, getResources().getString(R.string.ad_mob_interstitial_id));
        AdView adView = (AdView) findViewById(R.id.adView);

        assert adView != null;
        adView.setVisibility(View.VISIBLE);

        if (BuildConfig.DEBUG) {
            String deviceIdForTestAds = AdModDeviceIdUtils.getAdMobDeviceId(DeviceInfo.this);
            Log.e("DeviceInfo", "Hashed device id to load test ads - " + deviceIdForTestAds);

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
}
