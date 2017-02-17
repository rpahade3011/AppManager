package com.appman.appmanager.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.R;
import com.appman.appmanager.utils.AppPreferences;
import com.appman.appmanager.utils.UtilsApp;
import com.appman.appmanager.utils.UtilsUI;

import net.rdrei.android.dirchooser.DirectoryChooserConfig;
import net.rdrei.android.dirchooser.DirectoryChooserFragment;

public class SettingsActivity extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        DirectoryChooserFragment.OnFragmentInteractionListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    // Load Settings
    private AppPreferences appPreferences;
    private Toolbar toolbar;
    private Context context;

    private Preference prefVersion, prefDeleteAll, prefCustomPath,
            prefCustomSMSPath, prefCustomContactsPath;
    private Preference prefPrivacyPolicy;

    private ListPreference prefCustomFilename, prefSortMode;
    private DirectoryChooserFragment chooserDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        this.context = this;
        this.appPreferences = AppManagerApplication.getAppPreferences();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        prefVersion = findPreference("prefVersion");
        prefDeleteAll = findPreference("prefDeleteAll");
        prefCustomFilename = (ListPreference) findPreference("prefCustomFilename");
        prefSortMode = (ListPreference) findPreference("prefSortMode");
        prefCustomPath = findPreference("prefCustomPath");
        prefCustomSMSPath = findPreference("prefCustomSMSPath");
        prefCustomContactsPath = findPreference("prefCustomContactsPath");
        prefPrivacyPolicy = findPreference("prefPrivacyPolicy");
        setInitialConfiguration();

        String versionName = UtilsApp.getAppVersionName(context);
        int versionCode = UtilsApp.getAppVersionCode(context);

        prefVersion.setTitle(getResources().getString(R.string.app_name) + " v" + versionName + " (" + versionCode + ")");
        prefVersion.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(context, AboutActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.fade_back);
                return false;
            }
        });

        // prefCustomFilename
        setCustomFilenameSummary();

        // prefSortMode
        setSortModeSummary();

        // prefCustomPath
        setCustomPathSummary();

        // prefSmsPath
        setCustomSMSPath();

        // prefContactsPath
        setCustomContactsPath();

        // prefDeleteAll
        prefDeleteAll.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                prefDeleteAll.setSummary(R.string.deleting);
                prefDeleteAll.setEnabled(false);
                Boolean deleteAll = UtilsApp.deleteAppFiles(context);
                if (deleteAll) {
                    prefDeleteAll.setSummary(R.string.deleting_done);
                } else {
                    prefDeleteAll.setSummary(R.string.deleting_error);
                }
                prefDeleteAll.setEnabled(true);
                return true;
            }
        });

        // prefCustomPath
        prefCustomPath.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final DirectoryChooserConfig chooserConfig = DirectoryChooserConfig.builder()
                        .newDirectoryName("AppManager APKs")
                        .allowReadOnlyDirectory(false)
                        .allowNewDirectoryNameModification(true)
                        .initialDirectory(appPreferences.getCustomPath())
                        .build();

                chooserDialog = DirectoryChooserFragment.newInstance(chooserConfig);
                chooserDialog.show(getFragmentManager(), null);

                return false;
            }
        });
        // Privacy Policy
        prefPrivacyPolicy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Opening a url
                Intent privacyPolicyIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getResources().getString(R.string.appmanager_privacy_policy_url)));
                startActivity(privacyPolicyIntent);
                return true;
            }
        });
    }

    @Override
    public void setContentView(int layoutResID) {
        ViewGroup contentView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_settings, new LinearLayout(this), false);
        toolbar = (Toolbar) contentView.findViewById(R.id.toolbar);
        //TODO Toolbar should load the default style in XML (white title and back arrow), but doesn't happen
        toolbar.setBackgroundColor(getResources().getColor(R.color.md_blue_grey_500));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ViewGroup contentWrapper = (ViewGroup) contentView.findViewById(R.id.content_wrapper);
        LayoutInflater.from(this).inflate(layoutResID, contentWrapper, true);
        getWindow().setContentView(contentView);

    }

    private void setInitialConfiguration() {
        toolbar.setTitle(getResources().getString(R.string.action_settings));

        // Android 5.0+ devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(UtilsUI.darker(getResources().getColor(R.color.md_blue_grey_700), 0.8));
            toolbar.setBackgroundColor(getResources().getColor(R.color.md_blue_grey_500));
            if (!appPreferences.getNavigationBlackPref()) {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.md_blue_grey_500));
            }
        }

    }

    private void setCustomFilenameSummary() {
        int filenameValue = Integer.valueOf(appPreferences.getCustomFilename())-1;
        prefCustomFilename.setSummary(getResources().getStringArray(R.array.filenameEntries)[filenameValue]);
    }

    private void setSortModeSummary() {
        int sortValue = Integer.valueOf(appPreferences.getSortMode())-1;
        prefSortMode.setSummary(getResources().getStringArray(R.array.sortEntries)[sortValue]);
    }

    private void setCustomPathSummary() {
        String path = appPreferences.getCustomPath();
        if (path.equals(UtilsApp.getDefaultAppFolder().getPath())) {
            prefCustomPath.setSummary(getResources().getString(R.string.button_default) + ": " + UtilsApp.getDefaultAppFolder().getPath());
        } else {
            prefCustomPath.setSummary(path);
        }
    }
    private void setCustomSMSPath() {
        String path = appPreferences.getSmsPath();
        if (path.equals(UtilsApp.getDefaultAppFolder().getPath())) {
            prefCustomSMSPath.setSummary(getResources().getString(R.string.button_default) + ": " + UtilsApp.getDefaultSmsFolder().getPath());
        } else {
            prefCustomSMSPath.setSummary(path);
        }
    }
    private void setCustomContactsPath() {
        String path = appPreferences.getContactsPath();
        if (path.equals(UtilsApp.getDefaultContactsFolder().getPath())) {
            prefCustomContactsPath.setSummary(getResources().getString(R.string.button_default) + ": " + UtilsApp.getDefaultContactsFolder().getPath());
        } else {
            prefCustomContactsPath.setSummary(path);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);

        if (pref == prefCustomFilename) {
            setCustomFilenameSummary();
        } else if (pref == prefSortMode) {
            setSortModeSummary();
        } else if (pref == prefCustomPath) {
            setCustomPathSummary();
        }else if (pref == prefCustomSMSPath){
            setCustomSMSPath();
        }else if (pref == prefCustomContactsPath){
            setCustomContactsPath();
        }
    }

    @Override
    public void onSelectDirectory(@NonNull String path) {
        Log.d(TAG, "PATH --> " + path);
        appPreferences.setCustomPath(path);
        setCustomPathSummary();
        chooserDialog.dismiss();
    }

    @Override
    public void onCancelChooser() {
        chooserDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }

}
