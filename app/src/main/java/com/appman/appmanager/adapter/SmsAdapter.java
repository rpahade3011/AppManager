package com.appman.appmanager.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appman.appmanager.R;
import com.appman.appmanager.SmsInfo;

import java.util.ArrayList;

/**
 * Created by Rudraksh on 23-Dec-15.
 */
public class SmsAdapter extends BaseAdapter{

    private Activity mActivity;
    private ArrayList<SmsInfo> arrayList;
    LayoutInflater inflater;
    public SmsAdapter(Activity activity, ArrayList<SmsInfo> list){
        this.mActivity = activity;
        this.arrayList = list;
        inflater = LayoutInflater.from(mActivity);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final SmsInfo smsInfo = arrayList.get(position);
        View view = convertView;
        if (convertView == null){
            view = inflater.inflate(R.layout.list_item_sms_row, null);
        }
        TextView txtHeading = (TextView)view.findViewById(R.id.txtSmsHeading);
        TextView txtBody = (TextView) view.findViewById(R.id.txtSmsBody);

        txtHeading.setText(smsInfo.getAddress());
        txtBody.setText(smsInfo.getBody());
        return view;
    }
}
