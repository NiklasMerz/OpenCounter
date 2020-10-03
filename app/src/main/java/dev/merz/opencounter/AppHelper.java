package dev.merz.opencounter;

import android.app.usage.UsageStats;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class AppHelper {

    public static ResolveInfo getPackage(PackageManager pm, UsageStats usageStats) {
        Intent intent = pm.getLaunchIntentForPackage(usageStats.getPackageName());
        if (intent != null) {
            return pm.resolveActivity(intent, PackageManager.MATCH_ALL);
        }
        return null;
    }
}
