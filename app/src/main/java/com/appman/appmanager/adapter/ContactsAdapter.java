package com.appman.appmanager.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

        ImageView imgVwUserIcon = (ImageView) view.findViewById(R.id.imgVwUserIcon);
        ImageView imgVwCallIcon = (ImageView) view.findViewById(R.id.imgVwCallIcon);
        TextView tvContactName = (TextView) view.findViewById(R.id.tvContactName);
        TextView tvContactNumber = (TextView) view.findViewById(R.id.tvContactNumber);
        imgVwCallIcon.setTag(position);

        //If User icon is not detected
        if (contactsInfo.getContactImage() == null) {
            imgVwUserIcon.setImageDrawable(mActivity.getResources().getDrawable(R.mipmap.ic_circled_user));
        } else {
            imgVwUserIcon.setImageBitmap(contactsInfo.getContactImage());
        }

        tvContactName.setText(contactsInfo.getContactName());
        tvContactNumber.setText(contactsInfo.getContactNumber());

        imgVwCallIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*ImageView img = (ImageView) v;
                String numberPos = v.getTag().toString();*/
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
        return view;
    }
}
