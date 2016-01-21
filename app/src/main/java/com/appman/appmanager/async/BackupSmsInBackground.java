package com.appman.appmanager.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.appman.appmanager.models.SmsOperationInfo;
import com.appman.appmanager.utils.UtilsApp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by rudhraksh.pahade on 11-01-2016.
 */
public class BackupSmsInBackground extends AsyncTask<Void, Void, Void>{

    private Activity mActivity;
    public ArrayList<String> smsBuffer;
    public ArrayList<SmsOperationInfo> smsOperationInfoArrayList = new ArrayList<SmsOperationInfo>();
    private ProgressDialog progressDialog;
    public BackupSmsInBackground(Activity activity){
        this.mActivity = activity;
        this.smsBuffer = new ArrayList<String>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressDialog == null){
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setMessage("Please wait while we are creating a backup.");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {

        smsBuffer.clear();
        Uri mSmsinboxQueryUri = Uri.parse("content://sms");
        Cursor cursor1 = mActivity.getContentResolver().query(mSmsinboxQueryUri,
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
                SmsOperationInfo info = new SmsOperationInfo();
                info.setMessageId(messageId);
                info.setThreadId(threadId);
                info.setAddress(address);
                info.setName(name);
                info.setMsg(msg);
                info.setType(type);

                smsOperationInfoArrayList.add(info);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        generateXMLFileForSMS(smsBuffer);
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog = null;
        }
        Toast.makeText(mActivity, "Backup performed successfully of "+smsOperationInfoArrayList.size()+ " SMS's", Toast.LENGTH_SHORT).show();
    }

    /**
     * METHOD THAT WILL WRITE ALL THE SMS IN A XML FILE
     * WHICH WILL BE STORED IN /AppManager/SMS/ WITH SYSTEM DATE AS
     * FILE NAME
     * @param list
     */
    private void generateXMLFileForSMS(ArrayList<String> list){
        Date date = new Date(System.currentTimeMillis());
        String smsFile = "SMS_"+date;
        try{

            File smsDir = UtilsApp.getDefaultSmsFolder();
            if (!smsDir.exists()){
                smsDir.mkdir();
            }
            File f = new File(smsDir.getPath(),smsFile+".xml");
            FileWriter writer = new FileWriter(f);
            writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.append((new StringBuilder("<allsms count=\"")).append(list.size()).append("\">\n\t").toString());

            for (String info : list){
                writer.append(info);
                writer.append('\n');

            }
            writer.append("</allsms>");
            writer.flush();
            writer.close();
        }catch (NullPointerException e)
        {
            System.out.println("Nullpointer Exception "+e);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
