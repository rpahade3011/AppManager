package com.appman.appmanager.adapter;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.appman.appmanager.R;

import java.util.List;

/**
 * Created by rudhraksh.pahade on 03-12-2015.
 */
public class ListAdapter extends ArrayAdapter<ActivityManager.RunningAppProcessInfo> {
    // List context
    private final Context context;
    // List values
    private final List<ActivityManager.RunningAppProcessInfo> values;

    public ListAdapter(Context context, List<ActivityManager.RunningAppProcessInfo> values) {
        super(context, R.layout.process_info, values);
        this.context = context;
        this.values = values;
    }


    /**
     * Constructing list element view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String packagName;
        packagName = values.get(position).processName;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.process_info_list_item, parent, false);

        TextView appName = (TextView) rowView.findViewById(R.id.appNameText);
//        appName.setText(values.get(position).processName);
//        appName.setTextColor(Color.parseColor("#9E9E9E"));
        appName.setText(packagName);
        appName.setTextColor(Color.parseColor("#9E9E9E"));
        if (packagName.equals("com.appman.appmanager")){
            appName.setTextColor(Color.parseColor("#F44336"));
        }

        return rowView;
    }
}

