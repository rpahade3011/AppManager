package com.appman.appmanager.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appman.appmanager.R;
import com.appman.appmanager.models.ContactsInfo;

import java.util.ArrayList;

/**
 * Created by rudhraksh.pahade on 20-01-2016.
 */
public class ContactsAdapter extends BaseAdapter {
    private Activity mActivity;
    private ArrayList<ContactsInfo> arrayList;
    private LayoutInflater inflater;

    public ContactsAdapter(Activity activity, ArrayList<ContactsInfo> list) {
        this.mActivity = activity;
        this.arrayList = list;
        this.inflater = LayoutInflater.from(mActivity);
    }


    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ContactsInfo contactsInfo = arrayList.get(position);
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_contacts_row, null);
        }
        // Referencing Views from {@link list_item_contacts_row}
        ImageView imgVwUserIcon = (ImageView) view.findViewById(R.id.imgVwUserIcon);
        Button buttonCallIcon = (Button) view.findViewById(R.id.buttonCallIcon);
        Button buttonSendMessage = (Button) view.findViewById(R.id.buttonSendMessage);
        TextView tvContactName = (TextView) view.findViewById(R.id.tvContactName);
        TextView tvContactNumber = (TextView) view.findViewById(R.id.tvContactNumber);
        buttonCallIcon.setTag(position);

        //If User icon is not detected
        if (contactsInfo.getContactImage() == null) {
            imgVwUserIcon.setImageDrawable(mActivity.getResources().getDrawable(R.mipmap.ic_circled_user));
        } else {
            imgVwUserIcon.setImageBitmap(contactsInfo.getContactImage());
        }
        // Setting Contact Number and Name
        tvContactName.setText(contactsInfo.getContactName());
        tvContactNumber.setText(contactsInfo.getContactNumber());

        // If a user may want to call that person
        buttonCallIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String callNumber = arrayList.get(position).getContactNumber();

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                callIntent.setData(Uri.parse("tel:" + callNumber));
                if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mActivity.startActivity(callIntent);
            }
        });

        // If user may want to send a message to that person
        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contactNumber = arrayList.get(position).getContactNumber();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(mActivity);
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra("address", contactNumber);

                    if (defaultSmsPackageName != null){
                        sendIntent.setPackage(defaultSmsPackageName);
                    }
                    mActivity.startActivity(sendIntent);
                }else {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    sendIntent.setType("vnd.android-dir/mms-sms");
                    sendIntent.putExtra("address", contactNumber);
                    mActivity.startActivity(sendIntent);
                }
            }
        });
        return view;
    }
}
