package com.appman.appmanager.async;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.appman.appmanager.activities.FragmentStorage;
import com.appman.appmanager.utils.StorageSpaceHandler;

/**
 * Created by Rudraksh on 01-Jan-16.
 */
public class ExternalStoragePercentageInBackground extends AsyncTask<Void, String, Void> {

    private Activity mActivity;
    float tempTotalPer;
    float tempUsedPer;
    float tempPer;
    int per, totalPer;

    public ExternalStoragePercentageInBackground(Activity activity){
        this.mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Void doInBackground(Void... params) {
        FragmentStorage.ext_sd_card_total_per = StorageSpaceHandler.getExternalStorageSpace();
        FragmentStorage.ext_sd_card_used_per = StorageSpaceHandler.getExternalUsedSpace();
        return null;
    }
    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        try {
            // Calculating the percentage
            tempTotalPer = FragmentStorage.ext_sd_card_total_per;
            tempUsedPer = FragmentStorage.ext_sd_card_used_per;
            tempPer = tempUsedPer / tempTotalPer * 100;
            Log.i("TOTAL STORAGE VALUE-->", String.valueOf(tempTotalPer));
            Log.d("STORAGE PERCENTAGE-->", String.valueOf(tempPer));
            per = (int)tempPer;
            totalPer = (int) tempTotalPer;
            Log.i("TOTAL EXT STORAGE VALUE AFTER TYPECASTING-->", String.valueOf(totalPer));

            // Setting up the progress bar and textview
            //FragmentStorage.storageSimpleViewInternal = new NumberProgressBar(mActivity);
            FragmentStorage.storageSimpleViewExternal.setProgress(per);
            FragmentStorage.txtExternalPercent.setText(String.valueOf(per)+ "%");
        }catch (NumberFormatException nfe){
            nfe.getMessage().toString();
        }catch (Exception e){
            e.getMessage().toString();
        }
    }
}
