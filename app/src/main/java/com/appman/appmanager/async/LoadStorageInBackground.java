package com.appman.appmanager.async;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;

import com.appman.appmanager.activities.StorageActivity;
import com.appman.appmanager.utils.StorageViewer;

/**
 * Created by rudhraksh.pahade on 21-12-2015.
 */
public class LoadStorageInBackground extends AsyncTask<Void, String, Void>{


    private Activity mActivity;

    public LoadStorageInBackground(Activity activity){
        this.mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        StorageActivity.progressWheel.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try{
            StorageActivity.sd_card_total_space = StorageViewer.showSDCardTotalSpace(mActivity);
            StorageActivity.sd_card_used_space = StorageViewer.showSDCardUsedSpace(mActivity);
            StorageActivity.sd_card_free_space = StorageViewer.showSDCardFreeSpace(mActivity);
        }catch (NumberFormatException nfe){
            nfe.getMessage().toString();
        }catch (Exception e){
            e.getMessage().toString();
        }

        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        StorageActivity.progressWheel.setVisibility(View.GONE);
        StorageActivity.txtTotalStorage.setText("Total : " + StorageActivity.sd_card_total_space);
        StorageActivity.txtUsedStorage.setText("Used : " + StorageActivity.sd_card_used_space);
        StorageActivity.txtFreeStorage.setText("Free : " + StorageActivity.sd_card_free_space);
    }
}
