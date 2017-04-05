package com.appman.appmanager.appupdater;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.utils.InternetConnection;
import com.appman.appmanager.utils.UtilsDialog;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.appman.appmanager.appupdater.Config.PLAY_STORE_HTML_TAGS_TO_GET_RIGHT_POSITION;
import static com.appman.appmanager.appupdater.Config.PLAY_STORE_HTML_TAGS_TO_REMOVE_USELESS_CONTENT;
import static com.appman.appmanager.appupdater.Config.PLAY_STORE_VARIES_W_DEVICE;

/**
 * Created by rudhraksh.pahade on 3/8/2017.
 */

public class AppUpdateHandler {
    public static String TAG = AppUpdateHandler.class.getName();

    private AppCompatActivity activity;
    private RequestQueue queue;

    private AppUpdateAlert appUpdateAlert;
    private AppUpdatePrefManager appUpdatePrefManager;

    private boolean info = true;
    private boolean showAlert = true;
    private UpdateListener updateListener;
    private String PACKAGE_NAME = "";
    private String CONNECTION_ERROR_TITLE = "Connection Error";
    private String CONNECTION_ERROR_MESSAGE = "Unable to check for updates, Please check your internet connection";

    public AppUpdateHandler(AppCompatActivity activity) {
        this.activity = activity;
        appUpdateAlert = new AppUpdateAlert(activity);
        appUpdatePrefManager = new AppUpdatePrefManager(activity);
        queue = AppManagerApplication.getInstance().getRequestQueue();
        PACKAGE_NAME = activity.getPackageName();
    }

    public void setOnUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    public void setCount(int count) {
        appUpdatePrefManager.setPref(count);
    }

    public void setWhatsNew(boolean info) {
        this.info = info;
    }

    public void showDefaultAlert(boolean showAlert) {
        this.showAlert = showAlert;
    }

    public void startCheckingUpdate() {
        String NETWORK_STATUS = InternetConnection.getConnectivityStatusString(activity.getApplicationContext());
        if (NETWORK_STATUS.equals("Wifi enabled") || NETWORK_STATUS.equals("Mobile data enabled")) {
            StringRequest request = new StringRequest(Request.Method.GET,
                    Config.PLAY_STORE_ROOT_URL + PACKAGE_NAME, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        checker(response);
                    } catch (Exception e) {
                        Log.e(TAG + " Exception: ", e.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    try {
                        String res = new String(networkResponse.data,
                                HttpHeaderParser.parseCharset(networkResponse.headers, "utf-8"));
                        checker(res);
                    } catch (Exception e) {
                        Log.e(TAG + " Exception: ", e.toString());
                    }
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(Config.DEFAULT_TIMEOUT_MS,
                    Config.DEFAULT_MAX_RETRIES, Config.DEFAULT_BACKOFF_MULT));
            queue.add(request);
        } else {
            new AlertDialog.Builder(activity)
                    .setTitle(CONNECTION_ERROR_TITLE)
                    .setMessage(CONNECTION_ERROR_MESSAGE)
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startCheckingUpdate();
                            dialog.dismiss();
                        }
                    }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    activity.finish();
                }
            }).setCancelable(false).show();
        }
    }

    private void checker(String response) throws IOException {
        InputStream is = new ByteArrayInputStream(response.getBytes());
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains(PLAY_STORE_HTML_TAGS_TO_GET_RIGHT_POSITION)) {
                String containingVersion = line.substring(line.lastIndexOf(PLAY_STORE_HTML_TAGS_TO_GET_RIGHT_POSITION) + 28);
                String[] removingUnUsefulTags = containingVersion.split(PLAY_STORE_HTML_TAGS_TO_REMOVE_USELESS_CONTENT);
                if (!removingUnUsefulTags[0].toUpperCase().equals(PLAY_STORE_VARIES_W_DEVICE)) {
                    if (Comparator.isVersionNewer(activity, removingUnUsefulTags[0])) {
                        if (appUpdatePrefManager.getCount() == 0) {
                            if (showAlert)
                                appUpdateAlert.showDialog(response, info, removingUnUsefulTags[0]);
                            this.updateListener.onUpdateFound(true, appUpdateAlert.whatsNew(response));
                        }
                        appUpdatePrefManager.setCount();
                    }
                }
            } else if (line.contains(Config.PLAY_STORE_PACKAGE_NOT_PUBLISHED_IDENTIFIER)) {
                Log.e(TAG, Config.PLAY_STORE_PACKAGE_NOT_PUBLISHED_IDENTIFIER);
                this.updateListener.onUpdateFound(false, "");
            }
        }
    }
}
