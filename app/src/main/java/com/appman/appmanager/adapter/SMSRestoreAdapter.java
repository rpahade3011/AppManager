package com.appman.appmanager.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appman.appmanager.R;

import java.util.List;

/**
 * Created by Rudraksh on 19-Jan-16.
 */
public class SMSRestoreAdapter extends BaseAdapter {

    private List<String> list;
    private Context context;
    private LayoutInflater inflater;
    private String path;

    public SMSRestoreAdapter(Context ctx, List<String> list, String path){
        this.context = ctx;
        this.list = list;
        this.path = path;
        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null){
            view = inflater.inflate(R.layout.my_file_manager_list_row, null);
        }
        TextView tvFileManagerText = (TextView) view.findViewById(R.id.tvFileManagerText);
        AppCompatRadioButton rbSelectFile = (AppCompatRadioButton) view.findViewById(R.id.rbSelectFile);

        tvFileManagerText.setText(list.get(position));


        return view;
    }
}
