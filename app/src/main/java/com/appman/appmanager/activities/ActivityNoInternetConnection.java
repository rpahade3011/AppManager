package com.appman.appmanager.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;

import com.appman.appmanager.R;
import com.gc.materialdesign.views.ButtonFlat;

/**
 * Created by Rudraksh on 24-Jan-16.
 */
public class ActivityNoInternetConnection extends Activity implements View.OnClickListener{

    private ButtonFlat buttonGoToWifi;
    private ButtonFlat buttonGoToData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setUpParameters();
        super.onCreate(savedInstanceState);
    }

    private void setUpParameters()
    {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.no_internet_connection);
        initComponents();
    }

    private void initComponents(){
        buttonGoToWifi = (ButtonFlat) findViewById (R.id.buttonGoToWifi);
        buttonGoToData = (ButtonFlat) findViewById (R.id.buttonGoToData);

        //Providing Listeners

        buttonGoToWifi.setOnClickListener(this);
        buttonGoToData.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonGoToWifi){
            finish();
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }else if (v == buttonGoToData){
            finish();
            startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
        }
    }

}
