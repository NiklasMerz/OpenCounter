package dev.merz.opencounter;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Icon;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;
import dev.merz.opencounter.bubble.BubbleActivity;

public class NotificationHelper {

    private final Context context;
    private final PackageManager pm;
    public static final String CHANNEL_ID = "notifychannel";

    NotificationHelper(Context context, PackageManager pm) {
        this.context = context;
        this.pm = pm;
    }

    public void showNotification(UsageStats usageStats) {
        ResolveInfo pkg = AppHelper.getPackage(pm, usageStats);

        Notification.BubbleMetadata bubble = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            bubble = getBubble();
        }


        Notification.Builder builder = new Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_mobile_off_24)
                .setContentTitle("Count: " + pkg.loadLabel(pm))
                .setContentText(Integer.toString(UsageHelper.getCount(usageStats)))
                .setAutoCancel(true);


        if (bubble != null) {
            builder.setBubbleMetadata(bubble);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        int notificationId = 123;
        notificationManager.notify(notificationId, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private Notification.BubbleMetadata getBubble() {
        // Create bubble intent
        Intent target = new Intent(context, BubbleActivity.class);
        PendingIntent bubbleIntent =
                PendingIntent.getActivity(context, 0, target, 0 /* flags */);

        // Create bubble metadata

        // Notification.BubbleMetadata.Builder(bubbleIntent, Icon.createWithResource(context, R.drawable.ic_baseline_mobile_off_24))
        Notification.BubbleMetadata bubbleData =
                new Notification.BubbleMetadata.Builder()
                        .setIntent(bubbleIntent)
                        .setIcon(Icon.createWithResource(context, R.drawable.ic_baseline_mobile_off_24))
                        .setDesiredHeight(600)
                        .setSuppressNotification(true)
                        .build();

        return bubbleData;
    }
}
