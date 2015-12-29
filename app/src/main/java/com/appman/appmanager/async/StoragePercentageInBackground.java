package com.appman.appmanager.async;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.appman.appmanager.activities.FragmentStorage;
import com.appman.appmanager.activities.StorageActivity;
import com.appman.appmanager.utils.StorageSpaceHandler;

/**
 * Created by Rudraksh on 22-Dec-15.
 */
public class StoragePercentageInBackground extends AsyncTask<Void, String, Void>{

    private Activity mActivity;
    float tempTotalPer;
    float tempUsedPer;
    float tempPer;
    int per;

    public StoragePercentageInBackground(Activity activity){
        this.mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        FragmentStorage.sd_card_total_per = StorageSpaceHandler.getInternalStorageSpace();
        FragmentStorage.sd_card_used_per = StorageSpaceHandler.getInternalUsedSpace();
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
            tempTotalPer = FragmentStorage.sd_card_total_per;
            tempUsedPer = FragmentStorage.sd_card_used_per;
            tempPer = tempUsedPer / tempTotalPer * 100;
            per = (int)tempPer;

            /*StorageActivity.progressBarCircularIndeterminate.setMax((int)tempTotalPer);
            StorageActivity.progressBarCircularIndeterminate.setProgress(per);
            StorageActivity.txtStoragePercentage.setVisibility(View.VISIBLE);
            StorageActivity.txtStoragePercentage.setText(String.valueOf(per)+ "%");*/
            //FragmentStorage.storageSimpleViewInternal.mea
            FragmentStorage.storageSimpleViewInternal.setMax((int) tempTotalPer);
            FragmentStorage.storageSimpleViewInternal.setProgress(per);
            FragmentStorage.txtInternalPercent.setText(String.valueOf(per)+ "%");
        }catch (NumberFormatException nfe){
            nfe.getMessage().toString();
        }catch (Exception e){
            e.getMessage().toString();
        }

    }
}
