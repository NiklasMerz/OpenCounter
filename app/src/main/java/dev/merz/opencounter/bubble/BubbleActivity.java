package dev.merz.opencounter.bubble;

import androidx.appcompat.app.AppCompatActivity;
import dev.merz.opencounter.AppHelper;
import dev.merz.opencounter.R;
import dev.merz.opencounter.UsageHelper;

import android.app.ListActivity;
import android.app.usage.UsageStats;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BubbleActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadAppList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadAppList();
    }

    private void loadAppList() {
        List<UsageStats> apps = UsageHelper.getUsageList(getApplicationContext());

        List<AppListItem> usedApps = new ArrayList<AppListItem>();
        PackageManager pm = getApplicationContext().getPackageManager();
        for (UsageStats app : apps) {
            int count = UsageHelper.getCount(app);
            if (count > 1) {
                ResolveInfo pkg = AppHelper.getPackage(pm, app);
                if (pkg == null) {
                    //No valid package, ignore it
                    continue;
                }

                if (app.getPackageName().equals(this.getPackageName())) {
                    continue;
                }

                AppListItem item = new AppListItem();
                item.count = count;
                item.label = pkg.loadLabel(pm).toString();

                usedApps.add(item);
            }
        }

        // use your custom layout
        CustomAdapter adapter = new CustomAdapter(this,
                R.layout.rowlayout, usedApps);
        setListAdapter(adapter);
    }
}