package com.appman.appmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.appman.appmanager.R;
import com.appman.appmanager.models.StorageDir;

import java.util.ArrayList;

/**
 * Created by Rudraksh on 01-Jan-16.
 */
public class StorageDirAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<StorageDir> mArrayList;
    private LayoutInflater inflater;

    public StorageDirAdapter(Context ctx, ArrayList<StorageDir> list){
        this.mContext = ctx;
        this.mArrayList = list;
        this.inflater = LayoutInflater.from(mContext);
    }


    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final StorageDir storageDir = mArrayList.get(position);
        View view = convertView;
        if (convertView == null){
            view =  inflater.inflate(R.layout.space_statistics_list_item, null);
        }

        TextView txtHeader = (TextView) view.findViewById (R.id.textViewDirectoryHeaderName);
        TextView txtPath = (TextView) view.findViewById (R.id.textViewStoragePath);

        txtHeader.setText(storageDir.getDirectoryName());
        txtPath.setText("Path : " + "/" +storageDir.getDirectoryPath());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
                builder.title(storageDir.getDirectoryName());
                builder.icon(mContext.getDrawable(R.mipmap.ic_action_file_folder));
                builder.content("Path : " + "/" + storageDir.getDirectoryPath());
                builder.positiveText("OK");
                builder.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                });
                MaterialDialog dialog = builder.build();
                dialog.show();
            }
        });
        return view;
    }
}
