package com.appman.appmanager.async;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;

import com.appman.appmanager.R;
import com.appman.appmanager.activities.FragmentStorage;
import com.appman.appmanager.utils.StorageViewer;

/**
 * Created by Rudraksh on 01-Jan-16.
 */
public class LoadExternalStorageInBackground extends AsyncTask<Void, String, Void> {

    private Activity mActivity;

    public LoadExternalStorageInBackground(Activity activity){
        this.mActivity = activity;
    }



    @Override
    protected Void doInBackground(Void... params) {
        FragmentStorage.ext_sd_card_total_space = StorageViewer.showExternalSDCardTotalSpace(mActivity);
        FragmentStorage.ext_sd_card_used_space = StorageViewer.showExternalSDCardUsedSpace(mActivity);
        FragmentStorage.ext_sd_card_free_space = StorageViewer.showExternalSDCardFreeSpace(mActivity);
        return null;
    }
    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        FragmentStorage.progressWheel.setVisibility(View.GONE);

        FragmentStorage.txtExternal.setText(mActivity.getString(R.string.storage_external) + ": " + FragmentStorage.ext_sd_card_used_space + " / " + FragmentStorage.ext_sd_card_total_space);
    }
}
