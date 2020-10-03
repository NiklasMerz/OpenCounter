package dev.merz.opencounter;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import dev.merz.opencounter.bubble.BubbleActivity;

import android.provider.Settings;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static android.app.AppOpsManager.*;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "notifychannel";
    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pm = this.getApplicationContext().getPackageManager();
        createNotificationChannel();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              selectApp();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_test) {
            getStatsAndShowNotification(this.getPackageName());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean checkForPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        assert appOps != null;
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS,  android.os.Process.myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
    }

    private void selectApp () {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = pm.queryIntentActivities( mainIntent, 0);
        for (ResolveInfo pkg: pkgAppsList) {
            System.out.println((pkg.loadLabel(pm)));
        }
    }

    private void getStatsAndShowNotification(String pkg) {
        if (!this.checkForPermission(this.getApplicationContext())) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            return;
        }

        UsageStatsManager usageStatsManager = (UsageStatsManager) this.getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        long start = calendar.getTimeInMillis();
        long end = System.currentTimeMillis();
        Map<String, UsageStats> stats = usageStatsManager.queryAndAggregateUsageStats(start, end);


        UsageStats selfEntry = stats.get(pkg);

        showNotification(selfEntry);
    }


    private void showNotification(UsageStats usageStats) {
        ResolveInfo pkg = AppHelper.getPackage(pm, usageStats);

        Notification.BubbleMetadata bubble = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
             bubble = getBubble();
        }


        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_mobile_off_24)
                .setContentTitle("Count: " + pkg.loadLabel(pm))
                .setContentText(Integer.toString(UsageHelper.getCount(usageStats)))
                .setAutoCancel(true);


        //if (bubble != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            builder.setBubbleMetadata(bubble);
        //}

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        int notificationId = 123;
        notificationManager.notify(notificationId, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private Notification.BubbleMetadata getBubble() {
        // Create bubble intent
        Context context = getApplicationContext();
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

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.open_channel_name);
            String description = getString(R.string.open_channel_description);
            int importance = NotificationManager
                    .IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
