package com.appman.appmanager.activities;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.R;
import com.appman.appmanager.adapter.ClearMemoryAdapter;
import com.appman.appmanager.models.AppProcessInfo;
import com.appman.appmanager.models.StorageSize;
import com.appman.appmanager.service.CoreService;
import com.appman.appmanager.utils.AppPreferences;
import com.appman.appmanager.utils.MemoryUtils;
import com.appman.appmanager.utils.StorageUtil;
import com.appman.appmanager.widget.textcounter.CounterView;
import com.appman.appmanager.widget.textcounter.WaveView;
import com.appman.appmanager.widget.textcounter.formatters.DecimalFormatter;
import com.gc.materialdesign.views.ButtonRectangle;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rudraksh on 27-Dec-15.
 */
public class MemoryCleanActivity extends AppCompatActivity implements CoreService.OnPeocessActionListener{

    // Load Settings
    private AppPreferences appPreferences;
    Context mContext;
    ListView mListView;
    WaveView mwaveView;
    RelativeLayout header;
    List<AppProcessInfo> mAppProcessInfos = new ArrayList<>();
    ClearMemoryAdapter mClearMemoryAdapter;
    CounterView textCounter;
    TextView sufix;
    TextView txtTotalRam;
    TextView mProgressBarText;
    TextView txtViewRunningProcessCount;
    public long Allmemory;
    String totalRam;
    LinearLayout bottom_lin;
    ButtonRectangle clearRam;
    private Vibrator mVibrator;

    private CoreService mCoreService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCoreService = ((CoreService.ProcessServiceBinder) service).getService();
            mCoreService.setOnActionListener(MemoryCleanActivity.this);
            mCoreService.scanRunProcess();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCoreService.setOnActionListener(null);
            mCoreService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_clean);
        this.appPreferences = AppManagerApplication.getAppPreferences();

        setTranslucentStatus(true);
        setScreenElements();
        showInterstitialAd();

        mContext = getApplicationContext();
        mVibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        mClearMemoryAdapter = new ClearMemoryAdapter(mContext, mAppProcessInfos);
        mListView.setAdapter(mClearMemoryAdapter);
        bindService(new Intent(mContext, CoreService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE);
        textCounter.setAutoFormat(false);
        textCounter.setFormatter(new DecimalFormatter());
        textCounter.setAutoStart(false);
        textCounter.setIncrement(50f); // the amount the number increments at each time interval
        textCounter.setTimeInterval(200); // the time interval (ms) at which the text changes
    }


    /**
     * REFERENCING THE UI ELEMENTS OF {@link com.appman.appmanager.R.layout.activity_memory_clean}
     */
    private void setScreenElements(){
        mListView = (ListView) findViewById (R.id.listview);
        mwaveView = (WaveView) findViewById(R.id.wave_view);
        header = (RelativeLayout) findViewById (R.id.header);
        textCounter = (CounterView) findViewById (R.id.textCounter);
        txtTotalRam = (TextView) findViewById (R.id.textViewTotalRam);
        txtViewRunningProcessCount = (TextView) findViewById (R.id.txtViewRunningProcessCount);
        sufix = (TextView) findViewById (R.id.sufix);
        mProgressBarText = (TextView) findViewById (R.id.progressBarText);
        bottom_lin = (LinearLayout) findViewById(R.id.bottom_lin);
        clearRam = (ButtonRectangle) findViewById (R.id.buttonCleanRam);
    }

    /**
     * THIS METHOD IS USED TO LOAD THE BANNER AD
     */

    private void showInterstitialAd(){
        final InterstitialAd interstitialAd = new InterstitialAd(MemoryCleanActivity.this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.ad_mob_interstitial_id));
        AdView adView = (AdView) findViewById (R.id.adView);
        adView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();

        adView.loadAd(adRequest);
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (interstitialAd.isLoaded()){
                    interstitialAd.show();
                }
            }
        });
    }

    @Override
    public void onScanStarted(Context context) {
        mProgressBarText.setText(R.string.scanning);

    }

    @Override
    public void onScanProgressUpdated(Context context, int current, int max) {
        mProgressBarText.setText(getString(R.string.scanning_m_of_n, current, max));

    }

    @Override
    public void onScanCompleted(Context context, List<AppProcessInfo> apps) {
        mProgressBarText.setVisibility(View.GONE);
        mAppProcessInfos.clear();
        Allmemory = 0;

        for (AppProcessInfo appInfo : apps) {
            if (!appInfo.isSystem) {
                mAppProcessInfos.add(appInfo);
                Allmemory += appInfo.memory;
            }
        }
        refeshTextCounter();
        mClearMemoryAdapter.notifyDataSetChanged();

        if (apps.size() > 0) {
            header.setVisibility(View.VISIBLE);
            bottom_lin.setVisibility(View.VISIBLE);


        } else {
            header.setVisibility(View.GONE);
            bottom_lin.setVisibility(View.GONE);
        }
        int count = apps.size();
        txtViewRunningProcessCount.setText(String.valueOf(count));
        getAvailableRam();

    }

    /**
     * THIS METHOD WILL REFRESH THE {@link com.appman.appmanager.R.id.textCounter}
     * TO SET THE TOTAL FREEABLE MEMORY.
     */

    private void refeshTextCounter() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                StorageSize mStorageSize = StorageUtil.convertStorageSize(Allmemory);
                textCounter.setStartValue(0f);
                textCounter.setEndValue(mStorageSize.value);
                mwaveView.setProgress((int)mStorageSize.value);
                sufix.setText(mStorageSize.suffix);
                textCounter.start();
            }
        });
        thread.start();


    }

    @Override
    public void onCleanStarted(Context context) {

    }

    @Override
    public void onCleanCompleted(Context context, long cacheSize) {


    }

    /**
     * THIS METHOD WILL SET THE STATUS BAR TRANSLUCENT ONLY ON KITKAT
     * ENABLED DEVICES.
     * @param on
     */
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * THIS WILL LOAD THE TOTAL AND USED RAM FROM {@link MemoryUtils}
     * AND UPDATE THE TEXT ACCORDINGLY
     */

    private void getAvailableRam(){
        final Animation animator = AnimationUtils.loadAnimation(mContext, R.anim.shake);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    totalRam = MemoryUtils.getTotalMemory(mContext);
                    String usedRam = MemoryUtils.getUsedRam(mContext);
                    txtTotalRam.setVisibility(View.VISIBLE);
                    txtTotalRam.setText(usedRam + " / " + totalRam);
                    txtTotalRam.setAnimation(animator);
                }
                catch (RuntimeException re){
                    re.getMessage().toString();
                }catch (Exception e){
                    e.getMessage().toString();
                }

            }
        });
        t.start();

        /**
         * WHEN USER CLICKS ON THE CLEAR MEMORY BUTTON
         */
        clearRam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfimationDialog();

            }
        });
    }

    private void showConfimationDialog(){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(MemoryCleanActivity.this);
        builder.title("WARNING");
        builder.content(getString(R.string.ram_clear_message));
        builder.negativeText("Cancel");
        builder.positiveText("OK");
        builder.negativeColor(getResources().getColor(R.color.colorPrimary));
        builder.positiveColor(getResources().getColor(R.color.card_blue));
        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                mVibrator.vibrate(100);
                clearMemory();
            }
        });
        builder.show();
    }

    /**
     * THIS METHOD WILL CLEAR THE BACKGROUND RUNNING PROCESSES
     * BY CALLING {@link MemoryUtils}
     */

    private void clearMemory(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    killAllBackgroundProcess();
                } catch (RuntimeException re) {
                    re.getMessage().toString();
                } catch (Exception e) {
                    e.getMessage().toString();
                }

            }
        });
    }

    /**
     * THIS METHOD WILL KILL THE BACKGROUND PROCESSES
     * AND WILL AGAIN RESET THE TOTAL AVAILABLE & USED RAM TEXT
     */

    private void killAllBackgroundProcess(){
        long killAppmemory = 0;

        for (int i = mAppProcessInfos.size() - 1; i >= 0; i--) {
            if (mAppProcessInfos.get(i).checked) {
                killAppmemory += mAppProcessInfos.get(i).memory;
                mCoreService.killBackgroundProcesses(mAppProcessInfos.get(i).processName);
                mAppProcessInfos.remove(mAppProcessInfos.get(i));
                mClearMemoryAdapter.notifyDataSetChanged();
                //getAvailableRam();
            }
        }
        Allmemory = Allmemory - killAppmemory;
        String memKilled = textCounter.getText().toString();
        Intent intent = new Intent(MemoryCleanActivity.this, ActivityAfterMemoryCleaned.class);
        intent.putExtra("memoryKilled", memKilled);
        intent.putExtra("memorySuffix", sufix.getText().toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();




    }
    @Override
    public void onDestroy() {
        unbindService(mServiceConnection);
        super.onDestroy();
    }
}
