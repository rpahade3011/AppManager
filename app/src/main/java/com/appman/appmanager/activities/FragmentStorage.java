package com.appman.appmanager.activities;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.R;
import com.appman.appmanager.adapter.StorageDirAdapter;
import com.appman.appmanager.async.ExternalStoragePercentageInBackground;
import com.appman.appmanager.async.LoadExternalStorageInBackground;
import com.appman.appmanager.async.LoadStorageInBackground;
import com.appman.appmanager.async.StoragePercentageInBackground;
import com.appman.appmanager.models.StorageDir;
import com.appman.appmanager.utils.AppPreferences;
import com.appman.appmanager.utils.SpaceStatistics;
import com.appman.appmanager.utils.UtilsUI;
import com.appman.appmanager.views.TextView_Light;
import com.appman.appmanager.views.TextView_Regular;
import com.gc.materialdesign.views.ProgressBarDeterminate;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;

/**
 * Created by rudhraksh.pahade on 29-12-2015.
 */
public class FragmentStorage extends AppCompatActivity{

    private AppPreferences appPreferences;

    Toolbar toolbar;
    public static ProgressBarDeterminate storageSimpleViewInternal;
    public static ProgressBarDeterminate storageSimpleViewExternal;

    public static ProgressWheel progressWheel;
    private ListView listViewStorage;
    private StorageDirAdapter adapter;
    private ArrayList<StorageDir> mArrayList = new ArrayList<StorageDir>();

    public static TextView_Regular txtInternal;
    public static TextView_Regular txtInternalPercent;

    public static TextView_Regular txtExternal;
    public static TextView_Regular txtExternalPercent;

    public static TextView_Regular txtImage;
    public static TextView_Light txtImageSize;
    public static TextView_Regular txtVideo;
    public static TextView_Light txtVideoSize;
    public static TextView_Regular txtSound;
    public static TextView_Light txtSoundSize;
    public static TextView_Regular txtDocuments;
    public static TextView_Light txtDocumentsSize;
    public static TextView_Regular txtApp;
    public static TextView_Light txtAppSize;
    public static TextView_Regular txtText;
    public static TextView_Light txtTextSize;
    public static TextView_Regular txtOther;
    public static TextView_Light txtOtherSize;

    public static String sd_card_total_space;
    public static String sd_card_used_space;
    public static String sd_card_free_space;

    public static String ext_sd_card_total_space;
    public static String ext_sd_card_used_space;
    public static String ext_sd_card_free_space;

    public static String apps_size;
    public static String alarms_size;
    public static String image_size;
    public static String documents_size;
    public static String downloads_size;
    public static String music_size;
    public static String movies_size;
    public static String pictures_size;
    public static String ringtones_size;
    public static String other_size;

    public static float sd_card_total_per;
    public static float sd_card_used_per;

    public static float ext_sd_card_total_per;
    public static float ext_sd_card_used_per;

    private String APPS_DIR = Environment.getExternalStorageDirectory()+"data/app";

    private String[] storageLocations = new String[]{APPS_DIR,
            Environment.DIRECTORY_ALARMS,
            Environment.DIRECTORY_DCIM,
            Environment.DIRECTORY_DOCUMENTS,
            Environment.DIRECTORY_DOWNLOADS,
            Environment.DIRECTORY_MOVIES,
            Environment.DIRECTORY_MUSIC,
            Environment.DIRECTORY_PICTURES,
            Environment.DIRECTORY_RINGTONES};
    private String[] storageLocationsName = new String[]{"Storage.APPS","Storage.ALARMS",
            "Storage.DCIM",
            "Storage.DOCUMENTS",
            "Storage.DOWNLOADS",
            "Storage.MOVIES",
            "Storage.MUSIC",
            "Storage.PICTURES",
            "Storage.RINGTONES"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_storage);
        this.appPreferences = AppManagerApplication.getAppPreferences();


        setInitialConfiguration();

        initializeViews();

        loadStorageInBackground();

        calculateStoragePercentage();

        sample();
    }

    private void sample() {
        //List<File> file = storage.getFiles(storageLocationsName, OrderType.NAME, "");
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
     * SETTING UP THE TOOLBAR WITH TRANSLUCENT STATUS BAR
     */

    private void setInitialConfiguration(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null ) {
            getSupportActionBar().setTitle("Storage Details");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setBackgroundColor(getResources().getColor(R.color.list_txt_info_3));
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(UtilsUI.darker(getResources().getColor(R.color.list_txt_info_3), 0.8));


            toolbar.setBackgroundColor(getResources().getColor(R.color.list_txt_info_3));
            if (!appPreferences.getNavigationBlackPref()) {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.list_txt_info_3));
            }
        }
    }

    /**
     * REFERENCING UI WIDGETS FROM {@link R.layout.fragment_storage}
     */

    private void initializeViews(){
        progressWheel = (ProgressWheel) findViewById(R.id.progressWheel);
        txtInternal = (TextView_Regular) findViewById(R.id.txtInternal);
        txtInternalPercent = (TextView_Regular) findViewById (R.id.txtPercent1);
        txtExternal = (TextView_Regular) findViewById (R.id.txtExternal);
        txtExternalPercent = (TextView_Regular) findViewById(R.id.txtPercent2);
        storageSimpleViewInternal = (ProgressBarDeterminate) findViewById (R.id.memoryInternal);
        storageSimpleViewExternal = (ProgressBarDeterminate) findViewById (R.id.memoryExternal);

        txtImage = (TextView_Regular) findViewById (R.id.txtImage);
        txtImageSize = (TextView_Light) findViewById (R.id.txtImageSize);
        txtVideo = (TextView_Regular) findViewById (R.id.txtVideo);
        txtVideoSize= (TextView_Light) findViewById (R.id.txtVideoSize);
        txtSound = (TextView_Regular) findViewById (R.id.txtSound);
        txtSoundSize = (TextView_Light) findViewById (R.id.txtSoundSize);
        txtDocuments = (TextView_Regular) findViewById (R.id.txtDocument);
        txtDocumentsSize = (TextView_Light) findViewById (R.id.txtDocumentSize);
        txtApp = (TextView_Regular) findViewById (R.id.txtApp);
        txtAppSize = (TextView_Light) findViewById (R.id.txtAppSize);
        txtText = (TextView_Regular) findViewById (R.id.txtText);
        txtTextSize = (TextView_Light) findViewById (R.id.txtTextSize);
        txtOther = (TextView_Regular) findViewById (R.id.txtOther);
        txtOtherSize = (TextView_Light) findViewById (R.id.txtOtherSize);
        listViewStorage = (ListView) findViewById (R.id.listview);
    }

    /**
     * LOADS THE STORAGE DETAILS BY CALLING ASYNC TASK
     */

    private void loadStorageInBackground() {
        try{
            // AN ASYNC CALL TO LOAD INTERNAL STORAGE DETAILS
            new LoadStorageInBackground(FragmentStorage.this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            // CALL THE METHOD TO CHECK WHETHER EXTERNAL STORAGE SUCH AS SD CARD
            // IS AVAILABLE AND MOUNTED.
            new LoadExternalStorageInBackground(FragmentStorage.this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            getFullStatistics();

            setDataInListView();
        }catch (Exception e){
            e.getMessage().toString();
        }
    }

    private void setDataInListView() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < storageLocations.length; i++){
                    StorageDir storageDir = new StorageDir();
                    storageDir.setDirectoryName(storageLocationsName[i]);
                    storageDir.setDirectoryPath(storageLocations[i]);
                    mArrayList.add(storageDir);
                }
                adapter = new StorageDirAdapter(FragmentStorage.this, mArrayList);
                adapter.notifyDataSetChanged();
                listViewStorage.setAdapter(adapter);
            }
        });
        thread.start();

    }

    /**
     * THIS METHOD IS USED TO CALCULATE THE TOTAL SIZES
     * OF EACH FOLDER PRESENT IN INTERNAL STORAGE.
     */
    private void getFullStatistics() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                apps_size = SpaceStatistics.getAppsStatistics(FragmentStorage.this);
                alarms_size = SpaceStatistics.getAlarmsStatistics(FragmentStorage.this);
                image_size = SpaceStatistics.getImagesStatistics(FragmentStorage.this);
                documents_size = SpaceStatistics.getDocumentsStatistics(FragmentStorage.this);
                downloads_size = SpaceStatistics.getDownloadsStatistics(FragmentStorage.this);
                music_size = SpaceStatistics.getMusicStatistics(FragmentStorage.this);
                movies_size = SpaceStatistics.getMoviesStatistics(FragmentStorage.this);
                pictures_size = SpaceStatistics.getPicturesStatistics(FragmentStorage.this);
                ringtones_size = SpaceStatistics.getRingtonesStatistics(FragmentStorage.this);

                txtImageSize.setText(image_size);
                txtVideoSize.setText(movies_size);
                txtSoundSize.setText(music_size);
                txtDocumentsSize.setText(documents_size);
                txtAppSize.setText(apps_size);
                txtTextSize.setText(pictures_size);
                txtDocumentsSize.setText(downloads_size);
                txtOtherSize.setText(alarms_size);
            }
        });
        t.start();

    }

    /**
     * LOADS STORAGE PERCENTAGE BY CALLING ASYNC TASK
     */

    private void calculateStoragePercentage() {
        try{
            // AN ASYNC CALL TO CALCULATE INTERNAL STORAGE SPACE AND LOAD IN BACKGROUND
            new StoragePercentageInBackground(FragmentStorage.this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            // AN ASYNC CALL TO CALCULATE EXTERNAL STORAGE SPACE AND LOAD IN BACKGROUND
            new ExternalStoragePercentageInBackground(FragmentStorage.this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }catch (Exception e){
            e.getMessage().toString();
        }
    }

    /**
     * Method to check whether external media available and writable.
     * */
    private void checkExternalMedia(){
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            mExternalStorageAvailable = mExternalStorageWriteable = true;

           new LoadExternalStorageInBackground(FragmentStorage.this).execute();
        }
        else if (Environment.MEDIA_MOUNTED.equals(state))
        {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
            Toast.makeText(getApplicationContext(), "Can't read your external storage.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
            txtExternal.setVisibility(View.GONE);
            txtExternalPercent.setVisibility(View.GONE);
            storageSimpleViewExternal.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),"Please check your storage is mounted.", Toast.LENGTH_SHORT).show();
        }
        System.out.println("External Media: readable = " + mExternalStorageAvailable + " writable = " + mExternalStorageWriteable);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }
}
