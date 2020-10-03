package dev.merz.opencounter;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;
public class UsageHelper {

    public static List<UsageStats> getUsageList(Context context) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        long start = calendar.getTimeInMillis();
        long end = System.currentTimeMillis();

         return usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);
    }

    public static int getCount(UsageStats usageStats) {
        try {
            Field privateField = UsageStats.class.
                    getDeclaredField("mLaunchCount");

            privateField.setAccessible(true);

            int fieldValue = (int) privateField.get(usageStats);
            return  fieldValue;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //TODO error
        return 0;
    }
}
