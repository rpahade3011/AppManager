package com.appman.appmanager.adapter;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appman.appmanager.R;

import java.util.List;

/**
 * Created by rudhraksh.pahade on 28-11-2015.
 */
public class ApkAdapter extends BaseAdapter {

    List<PackageInfo> packageList;
    Activity context;
    PackageManager packageManager;


    public ApkAdapter(Activity context, List<PackageInfo> packageList,
                      PackageManager packageManager) {
        super();
        this.context = context;
        this.packageList = packageList;
        this.packageManager = packageManager;
    }

    private class ViewHolder {
        TextView apkName;
        TextView txtPackageName;
        ImageView imgAppIcon;
    }

    public int getCount() {
        return packageList.size();
    }

    public Object getItem(int position) {
        return packageList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();

            holder.apkName = (TextView) convertView.findViewById(R.id.appname);
            holder.txtPackageName = (TextView) convertView.findViewById(R.id.txtPackageName);
            holder.imgAppIcon = (ImageView) convertView.findViewById(R.id.app_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PackageInfo packageInfo = (PackageInfo) getItem(position);
        Drawable appIcon = packageManager.getApplicationIcon(packageInfo.applicationInfo);
        String appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
        appIcon.setBounds(0, 0, 60, 60);

        holder.imgAppIcon.setImageDrawable(appIcon);
        holder.apkName.setText(appName);
        holder.apkName.setTextSize(18);
        holder.txtPackageName.setText(packageInfo.packageName);
        Typeface font = Typeface.createFromAsset(context.getAssets(),"fonts/Avenir-Book.ttf");
        holder.apkName.setTypeface(font);
        holder.txtPackageName.setTypeface(font);


        Animation animation = AnimationUtils.loadAnimation(context, R.anim.card_animation);
        convertView.setAnimation(animation);


        return convertView;
    }
    public void notifyDatasetChanged(){
        super.notifyDataSetChanged();
    }
}
