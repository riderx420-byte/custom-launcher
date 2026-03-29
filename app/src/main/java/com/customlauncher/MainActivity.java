package com.customlauncher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView appsRecyclerView;
    private AppAdapter appAdapter;
    private List<AppInfo> appList;
    
    // Dock icons (quick access)
    private ImageView dockIcon1, dockIcon2, dockIcon3, dockIcon4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        appsRecyclerView = findViewById(R.id.appsRecyclerView);
        dockIcon1 = findViewById(R.id.dockIcon1);
        dockIcon2 = findViewById(R.id.dockIcon2);
        dockIcon3 = findViewById(R.id.dockIcon3);
        dockIcon4 = findViewById(R.id.dockIcon4);
        
        // Load all installed apps
        appList = loadInstalledApps();
        
        // Set up RecyclerView with grid layout
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        appsRecyclerView.setLayoutManager(layoutManager);
        
        appAdapter = new AppAdapter(this, appList);
        appsRecyclerView.setAdapter(appAdapter);
        
        // Set up dock click listeners
        setupDock();
    }
    
    private List<AppInfo> loadInstalledApps() {
        List<AppInfo> apps = new ArrayList<>();
        PackageManager packageManager = getPackageManager();
        
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(mainIntent, 0);
        
        for (ResolveInfo resolveInfo : resolveInfoList) {
            String appName = resolveInfo.loadLabel(packageManager).toString();
            apps.add(new AppInfo(
                appName,
                resolveInfo.loadIcon(packageManager),
                resolveInfo.activityInfo.packageName,
                resolveInfo.activityInfo.name
            ));
        }
        
        return apps;
    }
    
    private void setupDock() {
        // Default dock apps - you can customize these
        String[] dockPackages = {
            "com.android.phone",      // Phone
            "com.android.messaging",  // Messages
            "com.android.browser",    // Browser
            "com.android.settings"    // Settings
        };
        
        PackageManager pm = getPackageManager();
        ImageView[] dockIcons = {dockIcon1, dockIcon2, dockIcon3, dockIcon4};
        
        for (int i = 0; i < dockIcons.length; i++) {
            final int index = i;
            dockIcons[i].setOnClickListener(v -> {
                if (index < dockPackages.length) {
                    Intent launchIntent = pm.getLaunchIntentForPackage(dockPackages[index]);
                    if (launchIntent != null) {
                        startActivity(launchIntent);
                    }
                }
            });
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh app list when returning to launcher
        if (appAdapter != null) {
            appAdapter.updateApps(loadInstalledApps());
        }
    }
}
