package com.appman.appmanager.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.R;
import com.appman.appmanager.async.BackupSmsInBackground;
import com.appman.appmanager.models.SmsInfo;
import com.appman.appmanager.async.LoadSmsInBackground;
import com.appman.appmanager.utils.AppPreferences;
import com.appman.appmanager.utils.UtilsApp;
import com.appman.appmanager.utils.UtilsUI;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Rudraksh on 23-Dec-15.
 */
public class SmsActivity extends AppCompatActivity{

    public static final String TAG = SmsActivity.class.getSimpleName();
    private AppPreferences appPreferences;

    public static ListView listViewSms;
    private Toolbar toolbar;
    public static ArrayList<SmsInfo> arrayList;

    public static ProgressWheel progressWheel;
    public static TextView txtSmsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        this.appPreferences = AppManagerApplication.getAppPreferences();
        setUpToolbar();
        initViews();
        loadSmsInBackground();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }

    private void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(getResources().getString(R.string.action_sms));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setBackgroundColor(getResources().getColor(R.color.card_orange));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(UtilsUI.darker(getResources().getColor(R.color.card_orange), 0.8));


                toolbar.setBackgroundColor(getResources().getColor(R.color.card_orange));
                if (!appPreferences.getNavigationBlackPref()) {
                    getWindow().setNavigationBarColor(getResources().getColor(R.color.card_orange));
                }
            }
        }
    }
    private void initViews(){
        listViewSms = (ListView)findViewById(R.id.listViewSms);
        progressWheel = (ProgressWheel)findViewById(R.id.progress);
        txtSmsCount = (TextView)findViewById(R.id.txtViewSmsCount);
    }
    private void loadSmsInBackground(){
        new LoadSmsInBackground(SmsActivity.this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sms_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_backup_sms:
                new BackupSmsInBackground(SmsActivity.this).execute();
                break;
            case R.id.action_restore_sms:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /*private void backupSMS(){
        smsBuffer.clear();
        Uri mSmsinboxQueryUri = Uri.parse("content://sms");
        Cursor cursor1 = getContentResolver().query(
                mSmsinboxQueryUri,
                new String[] { "_id", "thread_id", "address", "person", "date",
                        "body", "type" }, null, null, null);
        String[] columns = new String[] { "_id", "thread_id", "address", "person", "date", "body",
                "type" };
        if (cursor1.getCount() > 0) {
            String count = Integer.toString(cursor1.getCount());
            Log.d("Count", count);
            while (cursor1.moveToNext()) {

                String messageId = cursor1.getString(cursor1
                        .getColumnIndex(columns[0]));

                String threadId = cursor1.getString(cursor1
                        .getColumnIndex(columns[1]));

                String address = cursor1.getString(cursor1
                        .getColumnIndex(columns[2]));
                String name = cursor1.getString(cursor1
                        .getColumnIndex(columns[3]));
                String date = cursor1.getString(cursor1
                        .getColumnIndex(columns[4]));
                String msg = cursor1.getString(cursor1
                        .getColumnIndex(columns[5]));
                String type = cursor1.getString(cursor1
                        .getColumnIndex(columns[6]));



                smsBuffer.add(messageId + ","+ threadId+ ","+ address + "," + name + "," + date + " ," + msg + " ,"
                        + type);

            }
            generateCSVFileForSMS(smsBuffer);
        }
    }*/

    /*private void generateCSVFileForSMS(ArrayList<String> list){
        Date date = new Date(System.currentTimeMillis());
        String smsFile = "SMS_"+date+".xml";
        try{
            String storagePath = Environment.getExternalStorageDirectory().toString() + "AppManager/SMS/" + smsFile;
            FileWriter writer = new FileWriter(storagePath);
            writer.append("messageId, threadId, Address, Name, Date, msg, type");
            writer.append('\n');

            for (String info : list){
                writer.append(info);
                writer.append('\n');
            }

        }catch (NullPointerException e)
        {
            System.out.println(TAG+"Nullpointer Exception "+e);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/
}
