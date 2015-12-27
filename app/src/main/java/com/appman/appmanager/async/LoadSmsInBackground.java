package com.appman.appmanager.async;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.appman.appmanager.SmsInfo;
import com.appman.appmanager.activities.SmsActivity;
import com.appman.appmanager.adapter.SmsAdapter;

import java.util.ArrayList;

/**
 * Created by Rudraksh on 23-Dec-15.
 */
public class LoadSmsInBackground extends AsyncTask<Void, String, Void>{

    private Activity mActivity;
    private ArrayList<SmsInfo> arrayList;

    public LoadSmsInBackground(Activity activity){
        this.mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        SmsActivity.progressWheel.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... params) {
        arrayList = new ArrayList<SmsInfo>();
        try{
            Uri uri = Uri.parse("content://sms");
            ContentResolver contentResolver = mActivity.getContentResolver();
            Cursor SMSL = contentResolver.query(uri, null, null, null, "date asc");
            while (SMSL.moveToFirst()){
                long dateTimeMillis = SMSL.getLong(SMSL.getColumnIndex("date"));
                Integer id1 = SMSL.getInt(SMSL.getColumnIndex("_id"));
                String body = SMSL.getString(SMSL.getColumnIndex("body"));
                Integer type1 = SMSL.getInt(SMSL.getColumnIndex("type"));
                String address = SMSL.getString(SMSL.getColumnIndex("address"));
                String read = SMSL.getString(SMSL.getColumnIndex("read"));
                String seen = SMSL.getString(SMSL.getColumnIndex("seen"));

                SmsInfo smsInfo = new SmsInfo();
                smsInfo.setId(id1);
                smsInfo.setDate(dateTimeMillis);
                smsInfo.setBody(body);
                smsInfo.setType(type1);
                smsInfo.setAddress(address);
                smsInfo.setRead(read);
                smsInfo.setSeen(seen);
                SMSL.close();
                arrayList.add(smsInfo);

                Log.d(SmsActivity.TAG, "--ID-->" + smsInfo.getId());
                Log.d(SmsActivity.TAG, "--DATE-->" +smsInfo.getDate());
                Log.d(SmsActivity.TAG, "--TYPE-->" +smsInfo.getType());
                Log.d(SmsActivity.TAG, "--SEEN-->" +smsInfo.getSeen());
                Log.d(SmsActivity.TAG, "--READ-->" +smsInfo.getRead());
            }

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
        SmsActivity.progressWheel.setVisibility(View.GONE);

        int count = arrayList.size();
        SmsActivity.listViewSms.setVisibility(View.VISIBLE);
        SmsActivity.txtSmsCount.setText(String.valueOf(count));

        SmsActivity.listViewSms.setAdapter(new SmsAdapter(mActivity, arrayList));
//        SmsActivity.smsAdapter.notifyDataSetChanged();


    }
}
