package com.appman.appmanager.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

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
        Cursor cursor1 = mActivity.getContentResolver().query(
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
//            generateCSVFileForSMS(smsBuffer);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        generateCSVFileForSMS(smsBuffer);
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog = null;
        }

    }

    private void generateCSVFileForSMS(ArrayList<String> list){
        Date date = new Date(System.currentTimeMillis());
        String smsFile = "SMS_"+".xml";
        try{
            //String storagePath = Environment.getExternalStorageDirectory().toString() + File.separator + smsFile;
            File file = new File(Environment.getExternalStorageDirectory() + "AppManager/SMS/");
            if (!file.exists()){
                file.mkdirs();
            }
            File f = new File(file, smsFile);
            FileWriter writer = new FileWriter(f);
            writer.append("messageId, threadId, Address, Name, Date, msg, type");
            writer.append('\n');

            for (String info : list){
                writer.append(info);
                writer.append('\n');
            }

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
        Toast.makeText(mActivity, "Backup performed successfully", Toast.LENGTH_SHORT).show();
    }
}
