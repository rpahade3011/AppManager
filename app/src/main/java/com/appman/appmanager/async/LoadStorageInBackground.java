package com.appman.appmanager.async;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.appman.appmanager.R;
import com.appman.appmanager.activities.FragmentStorage;
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
        FragmentStorage.progressWheel.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try{
            FragmentStorage.sd_card_total_space = StorageViewer.showSDCardTotalSpace(mActivity);
            FragmentStorage.sd_card_used_space = StorageViewer.showSDCardUsedSpace(mActivity);
            FragmentStorage.sd_card_free_space = StorageViewer.showSDCardFreeSpace(mActivity);
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
        FragmentStorage.progressWheel.setVisibility(View.GONE);


        Toast.makeText(mActivity, FragmentStorage.sd_card_total_space, Toast.LENGTH_SHORT).show();

        FragmentStorage.txtInternal.setText(mActivity.getString(R.string.storage_internal)+": "+FragmentStorage.sd_card_used_space + "/" +FragmentStorage.sd_card_total_space);
        /*FragmentStorage.txtUsedStorage.setText("Used : " + StorageActivity.sd_card_used_space);
        FragmentStorage.txtFreeStorage.setText("Free : " + StorageActivity.sd_card_free_space);*/
    }
}
