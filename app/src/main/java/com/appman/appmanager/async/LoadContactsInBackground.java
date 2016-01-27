package com.appman.appmanager.async;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import com.appman.appmanager.R;
import com.appman.appmanager.activities.ActivityContacts;
import com.appman.appmanager.adapter.ContactsAdapter;
import com.appman.appmanager.models.ContactsInfo;

import java.util.ArrayList;

/**
 * Created by rudhraksh.pahade on 20-01-2016.
 */
public class LoadContactsInBackground extends AsyncTask<Void, String, Void>{

    private Activity mActivity;
    String name = "";
    String number = "";
    long id = 0;
    Bitmap bitmap;

    public LoadContactsInBackground(Activity activity){
        this.mActivity = activity;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ActivityContacts.progressWheel.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... params) {

        ActivityContacts.arrayList = new ArrayList<ContactsInfo>();
        ActivityContacts.arrayList = getAllContacts();
        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        ActivityContacts.progressWheel.setVisibility(View.GONE);
        if (ActivityContacts.arrayList.size() < 0 || ActivityContacts.arrayList == null){
            Toast.makeText(mActivity, "No Contacts found on your device", Toast.LENGTH_SHORT).show();
        }else {
            int count  = ActivityContacts.arrayList.size();
            ActivityContacts.relativeLayoutContactsCount.setVisibility(View.VISIBLE);
            ActivityContacts.txtContactsCount.setText("" + count);
            ActivityContacts.listViewContacts.setVisibility(View.VISIBLE);
            try{
                ActivityContacts.listViewContacts.setAdapter(new ContactsAdapter(mActivity, ActivityContacts.arrayList));
            }catch (NullPointerException npe){
                npe.getMessage().toString();
            }catch (Exception e){
                e.getMessage().toString();
            }
        }
    }

    /**
     * THIS METHOD WILL FETCH ALL THE CONTACTS STORED IN THE DEVICE,
     * AND SHOW IT IN THE LISTVIEW.
     * @return info
     */
    /*public ArrayList<ContactsInfo> getAllContacts(){
        ArrayList<ContactsInfo> info = new ArrayList<ContactsInfo>();
        try{

            Cursor cursor = mActivity.getContentResolver().query(Contacts.People.CONTENT_URI, null,
                    null, null, Contacts.People.NAME + " ASC");
            mActivity.startManagingCursor(cursor);

            //BIND THE NAME AND THE NUMBER FIELDS
            String[] columns = new String[] { Contacts.People.NAME, Contacts.People.NUMBER };

            if (cursor.getCount() > 0){

                int idCol = cursor.getColumnIndex(Contacts.People._ID);
                int nameCol = cursor.getColumnIndex(Contacts.People.NAME);
                int numCol = cursor.getColumnIndex(Contacts.People.NUMBER);

                while (cursor.moveToNext()){
                    name = cursor.getString(nameCol);
                    number = cursor.getString(numCol);
                    id = cursor.getLong(idCol);

                    // RETRIEVE THE CONTACT PHOTO AS A BITMAP
                    Uri uri = ContentUris.withAppendedId(Contacts.People.CONTENT_URI, id);
                    bitmap = Contacts.People.loadContactPhoto(mActivity, uri, R.mipmap.ic_circled_user, null);

                    // Setting values in our model classes
                    ContactsInfo contactsInfo = new ContactsInfo();
                    contactsInfo.setContactId(id);
                    contactsInfo.setContactImage(bitmap);
                    contactsInfo.setContactName(name);
                    contactsInfo.setContactNumber(number);

                    //Adding model class to ArrayList<ContactsInfo>
                    info.add(contactsInfo);
//                    cursor.close();
                }
            }
        }catch (Exception e){
            e.getMessage().toString();
        }
        return info;
    }*/
    public ArrayList<ContactsInfo> getAllContacts(){
        ArrayList<ContactsInfo> info = new ArrayList<ContactsInfo>();
        try{

            ContentResolver cr = mActivity.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            if (cur.getCount() > 0){
                while (cur.moveToNext()){
                    id = Long.parseLong(cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID)));
                    name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0){
                        System.out.println("name : " + name + ", ID : " + id);
                        // get the phone number
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                new String[]{String.valueOf(id)}, null);
                        while (pCur.moveToNext()) {
                            number = pCur.getString(
                                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            System.out.println("phone" + number);
                            // RETRIEVE THE CONTACT PHOTO AS A BITMAP
                            Uri uri = ContentUris.withAppendedId(Contacts.People.CONTENT_URI, id);
                            bitmap = Contacts.People.loadContactPhoto(mActivity, uri, R.mipmap.ic_circled_user, null);

                            // Setting values in our model classes
                            ContactsInfo contactsInfo = new ContactsInfo();
                            contactsInfo.setContactId(id);
                            contactsInfo.setContactImage(bitmap);
                            contactsInfo.setContactName(name);
                            contactsInfo.setContactNumber(number);

                            //Adding model class to ArrayList<ContactsInfo>
                            info.add(contactsInfo);

                        }
                        pCur.close();
                    }
                }
            }
        }catch (Exception e){
            e.getMessage().toString();
        }
        return info;
    }
}
