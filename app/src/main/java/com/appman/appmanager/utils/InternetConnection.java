package com.appman.appmanager.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by rudraksh on 24/01/2016.
 */
public class InternetConnection {
    private Context context;

    /**
     * THIS METHOD WILL CHECK THE INTERNET CONNECTION OF THE DEVICE AND RETURN THE BOOLEAN VALUE
     * @param context
     * @return
     */


    public boolean isInternetConnetion(Context context)
    {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null)
        {
            NetworkInfo[] networkInfos = manager.getAllNetworkInfo();
            if (networkInfos != null)
            {
                for (int i = 0; i < networkInfos.length; i++)
                {
                    if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
