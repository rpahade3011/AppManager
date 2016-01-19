package com.appman.appmanager.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.appman.appmanager.AppManagerApplication;
import com.appman.appmanager.R;
import com.appman.appmanager.activities.AboutActivity;
import com.appman.appmanager.activities.ActivityContacts;
import com.appman.appmanager.activities.DeviceInfo;
import com.appman.appmanager.activities.FragmentStorage;
import com.appman.appmanager.activities.MemoryCleanActivity;
import com.appman.appmanager.activities.SettingsActivity;
import com.appman.appmanager.activities.SmsActivity;
import com.appman.appmanager.adapter.AppAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.Calendar;

public class UtilsUI {

    public static int darker (int color, double factor) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return Color.argb(a, Math.max((int) (r * factor), 0), Math.max((int) (g * factor), 0), Math.max((int) (b * factor), 0));
    }

    public static Drawer setNavigationDrawer (final Activity activity, final Context context, final Toolbar toolbar, final AppAdapter appAdapter, final AppAdapter appSystemAdapter, final AppAdapter appFavoriteAdapter, final AppAdapter appHiddenAdapter, final RecyclerView recyclerView) {
        final String loadingLabel = "...";
        int header;
        AppPreferences appPreferences = AppManagerApplication.getAppPreferences();
        String apps, systemApps, favoriteApps, hiddenApps;

        if (getDayOrNight() == 1) {
            header = R.drawable.header_day;
        } else {
            header = R.drawable.header_night;
        }

        if (appAdapter != null) {
            apps = Integer.toString(appAdapter.getItemCount());
        } else {
            apps = loadingLabel;
        }
        if (appSystemAdapter != null) {
            systemApps = Integer.toString(appSystemAdapter.getItemCount());
        } else {
            systemApps = loadingLabel;
        }
        if (appFavoriteAdapter != null) {
            favoriteApps = Integer.toString(appFavoriteAdapter.getItemCount());
        } else {
            favoriteApps = loadingLabel;
        }
        if (appHiddenAdapter != null) {
            hiddenApps = Integer.toString(appHiddenAdapter.getItemCount());
        } else {
            hiddenApps = loadingLabel;
        }

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(header)
                .build();

        Integer badgeColor = context.getResources().getColor(R.color.divider);
        BadgeStyle badgeStyle = new BadgeStyle(badgeColor, badgeColor).withTextColor(Color.GRAY);

        DrawerBuilder drawerBuilder = new DrawerBuilder();
        drawerBuilder.withActivity(activity);
        drawerBuilder.withToolbar(toolbar);
        drawerBuilder.withAccountHeader(headerResult);
        drawerBuilder.withStatusBarColor(UtilsUI.darker(appPreferences.getPrimaryColorPref(), 0.8));

        drawerBuilder.addDrawerItems(
                new PrimaryDrawerItem().withName(context.getResources().getString(R.string.action_apps)).withIcon(GoogleMaterial.Icon.gmd_phone_android).withBadge(apps).withBadgeStyle(badgeStyle).withIdentifier(1),
                new PrimaryDrawerItem().withName(context.getResources().getString(R.string.action_system_apps)).withIcon(GoogleMaterial.Icon.gmd_android).withBadge(systemApps).withBadgeStyle(badgeStyle).withIdentifier(2),
                new DividerDrawerItem(),
                new PrimaryDrawerItem().withName(context.getResources().getString(R.string.action_favorites)).withIcon(GoogleMaterial.Icon.gmd_star).withBadge(favoriteApps).withBadgeStyle(badgeStyle).withIdentifier(3),
                new DividerDrawerItem(),
                new SecondaryDrawerItem().withName(context.getResources().getString(R.string.action_ram)).withIcon(GoogleMaterial.Icon.gmd_memory).withBadge(context.getResources().getString(R.string.action_ram_description)).withSelectable(false).withIdentifier(5),
                new SecondaryDrawerItem().withName(context.getResources().getString(R.string.action_storage)).withIcon(GoogleMaterial.Icon.gmd_storage).withSelectable(false).withIdentifier(6),
                new SecondaryDrawerItem().withName(context.getResources().getString(R.string.action_sms)).withIcon(GoogleMaterial.Icon.gmd_sms).withSelectable(false).withIdentifier(7),
                new SecondaryDrawerItem().withName(context.getResources().getString(R.string.action_contacts)).withIcon(GoogleMaterial.Icon.gmd_phone).withSelectable(false).withIdentifier(8),
                new SecondaryDrawerItem().withName(context.getResources().getString(R.string.action_device)).withIcon(GoogleMaterial.Icon.gmd_devices).withSelectable(false).withIdentifier(9),
                new SecondaryDrawerItem().withName(context.getResources().getString(R.string.action_settings)).withIcon(GoogleMaterial.Icon.gmd_settings).withSelectable(false).withIdentifier(10),
                new SecondaryDrawerItem().withName(context.getResources().getString(R.string.action_share)).withIcon(GoogleMaterial.Icon.gmd_share).withSelectable(false).withIdentifier(11),
                new SecondaryDrawerItem().withName(context.getResources().getString(R.string.action_about)).withIcon(GoogleMaterial.Icon.gmd_info).withSelectable(false).withIdentifier(12));

        drawerBuilder.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem iDrawerItem) {
                switch (iDrawerItem.getIdentifier()) {

                    // Installed Apps
                    case 1:
                        recyclerView.setAdapter(appAdapter);
                        toolbar.setTitle("Installed Apps");
                        break;
                    // System Apps
                    case 2:
                        recyclerView.setAdapter(appSystemAdapter);
                        toolbar.setTitle("System Apps");
                        break;
                    // Favorite Apps
                    case 3:
                        if (appFavoriteAdapter.getItemCount() == 0){
                            UtilsDialog.showTitleContent(activity, "NO APPS", "You have not added any apps to your favorite list. Try adding some apps by selecting the star icon on app details and refresh your list by pulling down the screen. ");
                        }
                        else {
                            recyclerView.setAdapter(appFavoriteAdapter);
                            toolbar.setTitle("Favorite Apps");
                        }
                        break;
                    case 4:
                        recyclerView.setAdapter(appHiddenAdapter);
                        break;

                    // RAM Info
                    case 5:
                        context.startActivity(new Intent(context, MemoryCleanActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;

                    // Storage
                    case 6:
                        context.startActivity(new Intent(context, FragmentStorage.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;
                    // SMS Activity
                    case 7:
                        context.startActivity(new Intent(context, SmsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;

                    // Contacts Activity
                    case 8:
                        context.startActivity(new Intent(context, ActivityContacts.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;


                    // Device Info
                    case 9:
                        context.startActivity(new Intent(context, DeviceInfo.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;

                    // Settings Activity
                    case 10:
                        context.startActivity(new Intent(context, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;

                    // Share Intent
                    case 11:
                        openShareIntent(context);
                        break;

                    // About App Activity
                    case 12:
                        context.startActivity(new Intent(context, AboutActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    default:
                        break;
                }

                return false;
            }
        });

        return drawerBuilder.build();
    }

    private static void openShareIntent(Context ctx){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, ctx.getResources().getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey, look out this awesome application monitor and backup tool for Android - " + "https://play.google.com/store/apps/details?id=com.appman.appmanager&hl=en");
        ctx.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }


    public static int getDayOrNight() {
        int actualHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if (actualHour >= 8 && actualHour < 19) {
            return 1;
        } else {
            return 0;
        }
    }

}
