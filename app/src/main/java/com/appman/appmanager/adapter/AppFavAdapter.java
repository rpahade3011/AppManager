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

import com.afollestad.materialdialogs.MaterialDialog;
import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.R;
import com.appman.appmanager.activities.ActivityAppDetail;
import com.appman.appmanager.async.ExtractFileInBackground;
import com.appman.appmanager.models.AppFavInfo;
import com.appman.appmanager.models.AppInfo;
import com.appman.appmanager.utils.AppPreferences;
import com.appman.appmanager.utils.Utility;
import com.appman.appmanager.utils.UtilsDialog;
import com.elyeproj.loaderviewlibrary.LoaderImageView;
import com.elyeproj.loaderviewlibrary.LoaderTextView;

import java.util.List;

/**
 * Created by rudhraksh.pahade on 12/1/2016.
 */

public class AppFavAdapter extends RecyclerView.Adapter<AppFavAdapter.AppViewHolder> {

    // Load Settings
    private AppPreferences appPreferences;
    private List<AppFavInfo> appFavInfoList;

    private Context context;
    private Activity mActivity;

    public AppFavAdapter(List<AppFavInfo> appList, Activity context) {
        this.appFavInfoList = appList;
        this.mActivity = context;
        this.appPreferences = AppManagerApplication.getAppPreferences();
    }

    @Override
    public AppFavAdapter.AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View appAdapterView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home_adapter, parent, false);
        return new AppFavAdapter.AppViewHolder(appAdapterView);
    }

    @Override
    public void onBindViewHolder(AppFavAdapter.AppViewHolder appViewHolder, int position) {
        AppFavInfo favInfo = appFavInfoList.get(position);

        appViewHolder.vName.setText(favInfo.getName());
        appViewHolder.vApk.setText(favInfo.getAPK());
        appViewHolder.vIcon.setImageDrawable(favInfo.getIcon());

        setButtonEvents(appViewHolder, favInfo);
    }

    @Override
    public int getItemCount() {
        return appFavInfoList.size();
    }

    private void setButtonEvents(AppFavAdapter.AppViewHolder appViewHolder, final AppFavInfo appInfo) {
        Button appExtract = appViewHolder.vExtract;
        Button appShare = appViewHolder.vShare;
        final LoaderImageView appIcon = appViewHolder.vIcon;
        final CardView cardView = appViewHolder.vCard;

        appExtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MaterialDialog dialog = UtilsDialog.showTitleContentWithProgress(AppManagerApplication.getInstance().getCurrentActivity()
                        , String.format(mActivity.getResources().getString(R.string.dialog_saving), appInfo.getName())
                        , mActivity.getResources().getString(R.string.dialog_saving_description));
                new ExtractFileInBackground(mActivity, dialog, appInfo).execute();
            }
        });
        appShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.copyFavFile(appInfo);
                Intent shareIntent = Utility.getShareIntent(Utility.getFavOutputFilename(appInfo));
                mActivity.startActivity(Intent.createChooser(shareIntent, String.format(mActivity.getResources().getString(R.string.send_to), appInfo.getName())));
            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, ActivityAppDetail.class);
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
                    String transitionName = mActivity.getResources().getString(R.string.transition_app_icon);

                    ActivityOptions transitionActivityOptions = ActivityOptions
                            .makeSceneTransitionAnimation(mActivity, appIcon, transitionName);
                    mActivity.startActivity(intent, transitionActivityOptions.toBundle());
                } else {
                    context.startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.fade_back);
                }
            }
        });

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
