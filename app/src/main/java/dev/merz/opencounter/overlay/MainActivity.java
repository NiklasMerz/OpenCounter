package dev.merz.opencounter.overlay;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class MainActivity  extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent svc = new Intent(this, OverlayShowingService.class);

        checkPermissionOverlay();

        startService(svc);
        finish();
    }

    public void checkPermissionOverlay() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Request permission", Toast.LENGTH_SHORT).show();
            Intent intentSettings = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivityForResult(intentSettings, 1);
        }
    }
}