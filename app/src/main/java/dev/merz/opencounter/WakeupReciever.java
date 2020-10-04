package dev.merz.opencounter;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Map;

public class WakeupReciever extends BroadcastReceiver {

    private static final String TAG = "WakeupReciever";

    @Override
    public void onReceive(Context context, Intent intent) {
        StringBuilder sb = new StringBuilder();
        sb.append("Action: " + intent.getAction() + "\n");
        sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
        String log = sb.toString();
        Log.d(TAG, log);
        Toast.makeText(context, log, Toast.LENGTH_LONG).show();
        selfTest(context);
    }

    private void selfTest(Context context) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        long start = calendar.getTimeInMillis();
        long end = System.currentTimeMillis();
        Map<String, UsageStats> stats = usageStatsManager.queryAndAggregateUsageStats(start, end);


        UsageStats selfEntry = stats.get(context.getPackageName());

        NotificationHelper nh = new NotificationHelper(context, context.getPackageManager());
        nh.showNotification(selfEntry);
    }
}
