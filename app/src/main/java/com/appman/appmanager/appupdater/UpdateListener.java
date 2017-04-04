package com.appman.appmanager.appupdater;

/**
 * Created by rudhraksh.pahade on 3/8/2017.
 */

public interface UpdateListener {
    void onUpdateFound(boolean newVersion, String whatsNew);
}
