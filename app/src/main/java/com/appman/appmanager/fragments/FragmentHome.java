package com.appman.appmanager.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.R;
import com.appman.appmanager.activities.ActivityMain;
import com.appman.appmanager.adapter.AppAdapter;
import com.appman.appmanager.models.AppInfo;
import com.appman.appmanager.utils.AppPreferences;
import com.appman.appmanager.utils.Utility;
import com.appman.appmanager.utils.UtilsDialog;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.yalantis.phoenix.PullToRefreshView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * Created by rudhraksh.pahade on 11/29/2016.
 */

public class FragmentHome extends Fragment implements SearchView.OnQueryTextListener {
    private static final String LogTag = "FragmentHome";

    private static final int MY_PERMISSIONS_REQUEST_WRITE_READ = 1;

    private static VerticalRecyclerViewFastScroller fastScroller;
    private static LinearLayout noResults;
    private View rootView = null;
    private ViewGroup mViewGroup = null;

    // Load Settings
    private AppPreferences appPreferences;
    // General variables
    private List<AppInfo> appList;
    private List<AppInfo> appSystemList;
    private List<AppInfo> appHiddenList;
    private AppAdapter appAdapter;
    private AppAdapter appSystemAdapter;
    public static AppAdapter appFavoriteAdapter;
    private AppAdapter appHiddenAdapter;
    // Configuration variables
    private Boolean doubleBackToExitPressedOnce = false;
    private Toolbar toolbar;
    private Activity activity;
    private Context context;
    private RecyclerView recyclerView;
    private PullToRefreshView pullToRefreshView;
    private MenuItem searchItem;
    private SearchView searchView;
    public static BottomBarTab bb = null;

    private GetInstalledApps getInstalledApps = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, null, false);
            mViewGroup = container;
            this.appPreferences = AppManagerApplication.getAppPreferences();
            this.activity = getActivity();
            this.context = getActivity().getApplicationContext();
        }
        setHasOptionsMenu(true);
        setInitialConfiguration();
        checkAndAddPermissions(activity);
        setAppDir();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.appList);
        pullToRefreshView = (PullToRefreshView) rootView.findViewById(R.id.pull_to_refresh);
        fastScroller = (VerticalRecyclerViewFastScroller) rootView.findViewById(R.id.fast_scroller);
        noResults = (LinearLayout) rootView.findViewById(R.id.noResults);

        pullToRefreshView.setEnabled(false);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadInstalledApps();

        return rootView;
    }

    private void setInitialConfiguration() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimary));
            ActivityMain.mainToolbar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
            getActivity().getWindow().setNavigationBarColor(getActivity().getResources().getColor(R.color.colorPrimary));
        }
    }

    private void loadInstalledApps() {
        if (getInstalledApps != null) {
            getInstalledApps.cancel(true);
        }
        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getInstalledApps = new GetInstalledApps();
                getInstalledApps.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }, 1000);*/

        getInstalledApps = new GetInstalledApps();
        getInstalledApps.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setPullToRefreshView(final PullToRefreshView pullToRefreshView) {
        pullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                appAdapter.clear();
                appSystemAdapter.clear();
                appFavoriteAdapter.clear();
                recyclerView.setAdapter(null);
                if (noResults.getVisibility() == View.VISIBLE) {
                    noResults.setVisibility(View.GONE);
                }
                loadInstalledApps();

                pullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefreshView.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    private void checkAndAddPermissions(Activity activity) {
        Utility.checkPermissions(activity);
    }

    private void setAppDir() {
        File appDir = Utility.getAppFolder();
        File smsDir = Utility.getDefaultSmsFolder();
        File contactsDir = Utility.getDefaultContactsFolder();
        if(!appDir.exists()) {
            appDir.mkdir();
        }else if (!smsDir.exists()){
            smsDir.mkdir();
        }else if (!contactsDir.exists()){
            contactsDir.mkdir();
        }

    }

    public static void setResultsMessage(Boolean result) {
        if (result) {
            noResults.setVisibility(View.VISIBLE);
            fastScroller.setVisibility(View.GONE);
        } else {
            noResults.setVisibility(View.GONE);
            fastScroller.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_READ: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    UtilsDialog.showTitleContent(context, getResources()
                            .getString(R.string.dialog_permissions),
                            getResources().getString(R.string.dialog_permissions_description));
                }
            }
        }
    }

    private List<AppInfo> getFavoriteList(List<AppInfo> appList, List<AppInfo> appSystemList) {
        List<AppInfo> res = new ArrayList<>();

        for (AppInfo app : appList) {
            if (Utility.isAppFavorite(app.getAPK(), appPreferences.getFavoriteApps())) {
                res.add(app);
            }
        }
        for (AppInfo app : appSystemList) {
            if (Utility.isAppFavorite(app.getAPK(), appPreferences.getFavoriteApps())) {
                res.add(app);
            }
        }

        return res;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty()) {
            ((AppAdapter) recyclerView.getAdapter()).getFilter().filter("");
            noResults.setVisibility(View.GONE);
        } else {
            ((AppAdapter) recyclerView.getAdapter()).getFilter().filter(newText.toLowerCase());
        }

        return false;
    }

    private class GetInstalledApps extends AsyncTask<Void, String, Void> {

        private Integer totalApps;
        private Integer actualApps;


        public GetInstalledApps() {
            actualApps = 0;
            appList = new ArrayList<>();
            appSystemList = new ArrayList<>();
            appHiddenList = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final PackageManager packageManager = getActivity().getPackageManager();
            List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
            Set<String> hiddenApps = appPreferences.getHiddenApps();
            totalApps = packages.size() + hiddenApps.size();

            // Get Sort Mode
            switch (appPreferences.getSortMode()) {
                default:
                    // Comparator by Name (default)
                    Collections.sort(packages, new Comparator<PackageInfo>() {
                        @Override
                        public int compare(PackageInfo p1, PackageInfo p2) {
                            return packageManager.getApplicationLabel(p1.applicationInfo)
                                    .toString().toLowerCase().compareTo(packageManager.getApplicationLabel
                                            (p2.applicationInfo).toString().toLowerCase());
                        }
                    });
                    break;
                case "2":
                    // Comparator by Size
                    Collections.sort(packages, new Comparator<PackageInfo>() {
                        @Override
                        public int compare(PackageInfo p1, PackageInfo p2) {
                            Long size1 = new File(p1.applicationInfo.sourceDir).length();
                            Long size2 = new File(p2.applicationInfo.sourceDir).length();
                            return size2.compareTo(size1);
                        }
                    });
                    break;
                case "3":
                    // Comparator by Installation Date (default)
                    Collections.sort(packages, new Comparator<PackageInfo>() {
                        @Override
                        public int compare(PackageInfo p1, PackageInfo p2) {
                            return Long.toString(p2.firstInstallTime).compareTo(Long.toString(p1.firstInstallTime));
                        }
                    });
                    break;
                case "4":
                    // Comparator by Last Update
                    Collections.sort(packages, new Comparator<PackageInfo>() {
                        @Override
                        public int compare(PackageInfo p1, PackageInfo p2) {
                            return Long.toString(p2.lastUpdateTime).compareTo(Long.toString(p1.lastUpdateTime));
                        }
                    });
                    break;
            }

            // Installed & System Apps
            for (PackageInfo packageInfo : packages) {
                if (!(packageManager.getApplicationLabel(packageInfo.applicationInfo).equals("") || packageInfo.packageName.equals(""))) {
                    if (packageManager.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                        try {
                            // Non System Apps
                            AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo)
                                    .toString(), packageInfo.packageName, packageInfo.versionName,
                                    packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir,
                                    packageManager.getApplicationIcon(packageInfo.applicationInfo), false);
                            appList.add(tempApp);
                        } catch (OutOfMemoryError e) {
                            //TODO Workaround to avoid FC on some devices (OutOfMemoryError). Drawable should be cached before.
                            AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo)
                                    .toString(), packageInfo.packageName, packageInfo.versionName,
                                    packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir,
                                    getResources().getDrawable(R.drawable.ic_android), false);
                            appList.add(tempApp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            // System Apps
                            AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo)
                                    .toString(), packageInfo.packageName, packageInfo.versionName,
                                    packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir,
                                    packageManager.getApplicationIcon(packageInfo.applicationInfo), true);
                            appSystemList.add(tempApp);
                        } catch (OutOfMemoryError e) {
                            //TODO Workaround to avoid FC on some devices (OutOfMemoryError). Drawable should be cached before.
                            AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo)
                                    .toString(), packageInfo.packageName, packageInfo.versionName,
                                    packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir,
                                    getResources().getDrawable(R.drawable.ic_android), false);
                            appSystemList.add(tempApp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                actualApps++;
                //publishProgress(Double.toString((actualApps * 100) / totalApps));
            }

            // Hidden Apps
            for (String app : hiddenApps) {
                AppInfo tempApp = new AppInfo(app);
                Drawable tempAppIcon = Utility.getIconFromCache(context, tempApp);
                tempApp.setIcon(tempAppIcon);
                appHiddenList.add(tempApp);

                actualApps++;
                //publishProgress(Double.toString((actualApps * 100) / totalApps));
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            appAdapter = new AppAdapter(appList, getActivity());
            appSystemAdapter = new AppAdapter(appSystemList, getActivity());
            appFavoriteAdapter = new AppAdapter(getFavoriteList(appList, appSystemList), getActivity());
            appHiddenAdapter = new AppAdapter(appHiddenList, getActivity());

            fastScroller.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(appAdapter);
            pullToRefreshView.setEnabled(true);

            searchItem.setVisible(true);

            fastScroller.setRecyclerView(recyclerView);
            recyclerView.setOnScrollListener(fastScroller.getOnScrollListener());

            if (ActivityMain.bottomBar != null) {
                bb = ActivityMain.bottomBar.getTabWithId(R.id.tab_home);
                bb.setBadgeCount(appList.size());
            }

            setPullToRefreshView(pullToRefreshView);
        }
    }
}
