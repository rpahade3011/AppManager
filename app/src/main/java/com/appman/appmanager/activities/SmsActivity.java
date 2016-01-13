package com.appman.appmanager.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.R;
import com.appman.appmanager.async.BackupSmsInBackground;
import com.appman.appmanager.async.LoadSmsInBackground;
import com.appman.appmanager.models.SmsInfo;
import com.appman.appmanager.utils.AppPreferences;
import com.appman.appmanager.utils.UtilsUI;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;

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
        registerForContextMenu(listViewSms);



    }
    private void loadSmsInBackground(){

        try{
            new LoadSmsInBackground(SmsActivity.this).execute();
        }catch (Exception e){
            e.getMessage().toString();
        }

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
                try{
                    new BackupSmsInBackground(SmsActivity.this).execute();
                }catch (Exception e){
                    e.getMessage().toString();
                }
                break;
            case R.id.action_restore_sms:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {


        if (v.getId() == R.id.listViewSms){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("Select Options");
            String[] menuItems = getResources().getStringArray(R.array.sms_array);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.sms_array);
        String menuItemName = menuItems[menuItemIndex];
        Toast.makeText(SmsActivity.this, menuItemName, Toast.LENGTH_SHORT).show();
        return true;

    }
}
