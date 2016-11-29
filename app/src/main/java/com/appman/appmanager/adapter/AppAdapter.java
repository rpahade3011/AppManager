package com.appman.appmanager.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.R;
import com.appman.appmanager.activities.ActivityAppDetail;
import com.appman.appmanager.async.ExtractFileInBackground;
import com.appman.appmanager.fragments.FragmentHome;
import com.appman.appmanager.models.AppInfo;
import com.appman.appmanager.utils.AppPreferences;
import com.appman.appmanager.utils.Utility;
import com.appman.appmanager.utils.UtilsDialog;
import com.elyeproj.loaderviewlibrary.LoaderImageView;
import com.elyeproj.loaderviewlibrary.LoaderTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rudhraksh.pahade on 11/29/2016.
 */

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> implements Filterable {

    // Load Settings
    private AppPreferences appPreferences;
    // AppAdapter variables
    private List<AppInfo> appList;
    private List<AppInfo> appListSearch;
    private Context context;
    private Activity mActivity;


    /*public AppAdapter(List<AppInfo> appList, Context context) {
        this.appList = appList;
        this.context = context;
        this.appPreferences = AppManagerApplication.getAppPreferences();
    }*/

    public AppAdapter(List<AppInfo> appList, Activity context) {
        this.appList = appList;
        this.mActivity = context;
        this.appPreferences = AppManagerApplication.getAppPreferences();
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View appAdapterView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home_adapter, parent, false);
        return new AppViewHolder(appAdapterView);
    }

    @Override
    public void onBindViewHolder(AppViewHolder appViewHolder, int position) {
        AppInfo appInfo = appList.get(position);
        appViewHolder.vName.setText(appInfo.getName());
        appViewHolder.vApk.setText(appInfo.getAPK());
        appViewHolder.vIcon.setImageDrawable(appInfo.getIcon());

        setButtonEvents(appViewHolder, appInfo);
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    public void clear() {
        appList.clear();
        notifyDataSetChanged();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                final FilterResults oReturn = new FilterResults();
                final List<AppInfo> results = new ArrayList<>();
                if (appListSearch == null) {
                    appListSearch = appList;
                }
                if (charSequence != null) {
                    if (appListSearch != null && appListSearch.size() > 0) {
                        for (final AppInfo appInfo : appListSearch) {
                            if (appInfo.getName().toLowerCase().contains(charSequence.toString())) {
                                results.add(appInfo);
                            }
                        }
                    }
                    oReturn.values = results;
                    oReturn.count = results.size();
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults.count > 0) {
                    FragmentHome.setResultsMessage(false);
                } else {
                    FragmentHome.setResultsMessage(true);
                }
                appList = (ArrayList<AppInfo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    private void setButtonEvents(AppViewHolder appViewHolder, final AppInfo appInfo) {
        Button appExtract = appViewHolder.vExtract;
        Button appShare = appViewHolder.vShare;
        final LoaderImageView appIcon = appViewHolder.vIcon;
        final CardView cardView = appViewHolder.vCard;

        appExtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*MaterialDialog dialog = UtilsDialog.showTitleContentWithProgress(AppManagerApplication.getInstance().getCurrentActivity()
                        , String.format(context.getResources().getString(R.string.dialog_saving), appInfo.getName())
                        , context.getResources().getString(R.string.dialog_saving_description));
                new ExtractFileInBackground(context, dialog, appInfo).execute();*/

                MaterialDialog dialog = UtilsDialog.showTitleContentWithProgress(AppManagerApplication.getInstance().getCurrentActivity()
                        , String.format(mActivity.getResources().getString(R.string.dialog_saving), appInfo.getName())
                        , mActivity.getResources().getString(R.string.dialog_saving_description));
                new ExtractFileInBackground(mActivity, dialog, appInfo).execute();
            }
        });
        appShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.copyFile(appInfo);
                Intent shareIntent = Utility.getShareIntent(Utility.getOutputFilename(appInfo));
                //context.startActivity(Intent.createChooser(shareIntent, String.format(context.getResources().getString(R.string.send_to), appInfo.getName())));
                mActivity.startActivity(Intent.createChooser(shareIntent, String.format(mActivity.getResources().getString(R.string.send_to), appInfo.getName())));
            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Activity activity = (Activity) context;
                Activity activity = AppManagerApplication.getInstance().getCurrentActivity();
                Intent intent = new Intent(context, ActivityAppDetail.class);
                intent.putExtra("app_name", appInfo.getName());
                intent.putExtra("app_apk", appInfo.getAPK());
                intent.putExtra("app_version", appInfo.getVersion());
                intent.putExtra("app_source", appInfo.getSource());
                intent.putExtra("app_data", appInfo.getData());
                Bitmap bitmap = ((BitmapDrawable) appInfo.getIcon()).getBitmap();
                intent.putExtra("app_icon", bitmap);
                intent.putExtra("app_isSystem", appInfo.isSystem());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String transitionName = context.getResources().getString(R.string.transition_app_icon);

                    ActivityOptions transitionActivityOptions = ActivityOptions
                            .makeSceneTransitionAnimation(activity, appIcon, transitionName);
                    context.startActivity(intent, transitionActivityOptions.toBundle());
                } else {
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.fade_back);
                }
            }
        });

    }

    public void resetLoaderView() {
        //AppViewHolder viewHolder = new AppViewHolder();
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        protected LoaderTextView vName;
        protected LoaderTextView vApk;
        protected LoaderImageView vIcon;
        protected Button vExtract;
        protected Button vShare;
        protected CardView vCard;

        public AppViewHolder(View v) {
            super(v);
            vName = (LoaderTextView) v.findViewById(R.id.txtName);
            vApk = (LoaderTextView) v.findViewById(R.id.txtApk);
            vIcon = (LoaderImageView) v.findViewById(R.id.imgIcon);
            vExtract = (Button) v.findViewById(R.id.btnExtract);
            vShare = (Button) v.findViewById(R.id.btnShare);
            vCard = (CardView) v.findViewById(R.id.app_card);

        }
    }
}
