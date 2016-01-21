package com.appman.appmanager.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.appman.appmanager.utils.UtilsApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by rudhraksh.pahade on 21-01-2016.
 */
public class BackupContactsInBackground extends AsyncTask<Void, String, Void> {


    private Activity mActivity;
    private ProgressDialog progressDialog;



    public BackupContactsInBackground(Activity activity){
        this.mActivity = activity;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressDialog == null){
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setMessage("Please wait while we are creating a backup.");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        Date date = new Date(System.currentTimeMillis());
        final String vfile = "auto_contacts_"+date+".vcf";
        Cursor phones = mActivity.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                null, null, null);
        phones.moveToFirst();
        for (int i = 0; i < phones.getCount(); i++){
            String lookupKey = phones.getString(phones
                    .getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
            Uri uri = Uri.withAppendedPath(
                    ContactsContract.Contacts.CONTENT_VCARD_URI,
                    lookupKey);
            AssetFileDescriptor fd;
            try{
                fd = mActivity.getContentResolver().openAssetFileDescriptor(uri, "r");
                FileInputStream fis = fd.createInputStream();
                byte[] buf = new byte[(int) fd.getDeclaredLength()];
                fis.read(buf);
                String VCard = new String(buf);
                File path = UtilsApp.getDefaultContactsFolder();
                if (!path.exists()){
                    path.mkdir();
                }
                String fileName = String.valueOf(path) + File.separator + vfile;
                FileOutputStream mFileOutputStream = new FileOutputStream(fileName,
                        true);
                mFileOutputStream.write(VCard.toString().getBytes());
                phones.moveToNext();
                Log.d("Vcard", VCard);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog = null;
        }
        Toast.makeText(mActivity, "Backup performed successfully", Toast.LENGTH_SHORT).show();
    }

}
