package com.appman.appmanager.async;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.appman.appmanager.activities.ActivityMain;
import com.appman.appmanager.models.AppInfo;
import com.appman.appmanager.utils.Utility;
import com.appman.appmanager.utils.UtilsDialog;
import com.appman.appmanager.utils.UtilsRoot;

/**
 * Created by rudhraksh.pahade on 11/29/2016.
 */

public class UninstallInBackground extends AsyncTask<Void, String, Boolean> {


    private Context context;
    private Activity activity;
    private MaterialDialog dialog;
    private AppInfo appInfo;

    public UninstallInBackground(Context context, MaterialDialog dialog, AppInfo appInfo) {
        this.context = context;
        this.activity = (Activity) context;
        this.dialog = dialog;
        this.appInfo = appInfo;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        Boolean status = false;

        if (Utility.checkPermissions(activity)) {
            status = UtilsRoot.uninstallWithRootPermission(appInfo.getSource());
        }

        return status;
    }

    @Override
    protected void onPostExecute(Boolean status) {
        super.onPostExecute(status);
        dialog.dismiss();
        if (status) {
            MaterialDialog.Builder materialDialog = UtilsDialog.showUninstalled(context, appInfo);
            materialDialog.callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    UtilsRoot.rebootSystem();
                    dialog.dismiss();
                }
            });
            materialDialog.callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onNegative(MaterialDialog dialog) {
                    dialog.dismiss();
                    Intent intent = new Intent(context, ActivityMain.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.finish();
                    context.startActivity(intent);
                }
            });
            materialDialog.show();
        }
    }
}
