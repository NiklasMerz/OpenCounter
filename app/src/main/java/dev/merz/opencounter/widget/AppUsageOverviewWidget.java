package dev.merz.opencounter.widget;

import android.app.usage.UsageStats;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

import dev.merz.opencounter.AppHelper;
import dev.merz.opencounter.R;
import dev.merz.opencounter.UsageHelper;
import dev.merz.opencounter.bubble.AppListItem;
import dev.merz.opencounter.bubble.CustomAdapter;

/**
 * Implementation of App Widget functionality.
 */
public class AppUsageOverviewWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_usage_overview_widget);
        loadAppList(context, views);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static void loadAppList(Context context, RemoteViews views) {
        List<UsageStats> apps = UsageHelper.getUsageList(context);

        List<AppListItem> usedApps = new ArrayList<AppListItem>();
        PackageManager pm = context.getPackageManager();
        for (UsageStats app : apps) {
            int count = UsageHelper.getCount(app);
            if (count > 1) {
                ResolveInfo pkg = AppHelper.getPackage(pm, app);
                if (pkg == null) {
                    //No valid package, ignore it
                    continue;
                }

                if (app.getPackageName().equals(context.getPackageName())) {
                    continue;
                }

                AppListItem item = new AppListItem();
                item.count = count;
                item.label = pkg.loadLabel(pm).toString();

                usedApps.add(item);
            }
        }

        // use your custom layout
        CustomAdapter adapter = new CustomAdapter(context,
                R.layout.rowlayout, usedApps);
        views.setRemoteAdapter(adapter, R.id.widget_list_view);
    }
}

