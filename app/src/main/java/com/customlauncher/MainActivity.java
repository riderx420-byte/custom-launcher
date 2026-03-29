package com.customlauncher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView appsRecyclerView;
    private AppAdapter appAdapter;
    private List<AppInfo> appList;
    
    private EditText searchEditText;
    private ImageView clearSearchButton;
    
    // Dock icons (quick access)
    private LinearLayout dockContainer;
    private ImageView[] dockIcons = new ImageView[5];
    private TextView[] dockLabels = new TextView[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize search
        searchEditText = findViewById(R.id.searchEditText);
        clearSearchButton = findViewById(R.id.clearSearchButton);
        
        // Setup search functionality
        setupSearch();
        
        // Initialize apps RecyclerView
        appsRecyclerView = findViewById(R.id.appsRecyclerView);
        
        // Load all installed apps
        appList = loadInstalledApps();
        
        // Set up RecyclerView with grid layout
        GridLayoutManager layoutManager = new GridLayoutManager(this, 5);
        appsRecyclerView.setLayoutManager(layoutManager);
        
        appAdapter = new AppAdapter(this, appList);
        appsRecyclerView.setAdapter(appAdapter);
        
        // Set up dock
        setupDock();
        
        // Set wallpaper
        setupWallpaper();
    }
    
    private void setupSearch() {
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                appAdapter.filter(query);
                clearSearchButton.setVisibility(View.VISIBLE);
            }
            return true;
        });
        
        clearSearchButton.setOnClickListener(v -> {
            searchEditText.setText("");
            clearSearchButton.setVisibility(View.GONE);
            appAdapter.filter("");
        });
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
        DockItem[] dockItems = {
            new DockItem("com.android.phone", "Phone", R.drawable.ic_phone),
            new DockItem("com.android.messaging", "Messages", R.drawable.ic_message),
            new DockItem("com.android.browser", "Browser", R.drawable.ic_browser),
            new DockItem("com.android.settings", "Settings", R.drawable.ic_settings),
            new DockItem("com.google.android.apps.maps", "Maps", R.drawable.ic_maps)
        };
        
        dockContainer = findViewById(R.id.dockContainer);
        dockContainer.removeAllViews();
        
        PackageManager pm = getPackageManager();
        
        for (int i = 0; i < dockItems.length; i++) {
            // Create dock item container
            LinearLayout dockItemLayout = new LinearLayout(this);
            dockItemLayout.setOrientation(LinearLayout.VERTICAL);
            dockItemLayout.setGravity(android.view.Gravity.CENTER);
            dockItemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                0, 
                LinearLayout.LayoutParams.MATCH_PARENT, 
                1f
            ));
            dockItemLayout.setPadding(8, 8, 8, 8);
            
            // Create icon
            ImageView iconView = new ImageView(this);
            iconView.setLayoutParams(new LinearLayout.LayoutParams(
                dpToPx(50), 
                dpToPx(50)
            ));
            iconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iconView.setBackgroundResource(R.drawable.dock_icon_bg);
            iconView.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
            
            // Create label
            TextView labelView = new TextView(this);
            labelView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            labelView.setMaxWidth(dpToPx(60));
            labelView.setSingleLine();
            labelView.setEllipsize(android.text.TextUtils.TruncateAt.END);
            labelView.setTextSize(11);
            labelView.setTextColor(getResources().getColor(android.R.color.white));
            labelView.setGravity(android.view.Gravity.CENTER);
            
            // Set dock item data
            final DockItem dockItem = dockItems[i];
            dockIcons[i] = iconView;
            dockLabels[i] = labelView;
            
            // Try to load actual app icon if app is installed
            Intent launchIntent = pm.getLaunchIntentForPackage(dockItem.packageName);
            if (launchIntent != null) {
                try {
                    iconView.setImageDrawable(pm.getApplicationIcon(dockItem.packageName));
                    labelView.setText(pm.getApplicationLabel(pm.getApplicationInfo(dockItem.packageName, 0)));
                } catch (Exception e) {
                    iconView.setImageResource(dockItem.iconRes);
                    labelView.setText(dockItem.label);
                }
            } else {
                iconView.setImageResource(dockItem.iconRes);
                labelView.setText(dockItem.label);
            }
            
            // Set click listener
            dockItemLayout.setOnClickListener(v -> {
                Intent launchIntent2 = pm.getLaunchIntentForPackage(dockItem.packageName);
                if (launchIntent2 != null) {
                    startActivity(launchIntent2);
                } else {
                    Toast.makeText(this, "App not installed", Toast.LENGTH_SHORT).show();
                }
            });
            
            // Add views
            dockItemLayout.addView(iconView);
            dockItemLayout.addView(labelView);
            dockContainer.addView(dockItemLayout);
        }
    }
    
    private void setupWallpaper() {
        // Set a gradient background as default wallpaper
        // You can customize this or add wallpaper picker later
        View mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setBackgroundResource(R.drawable.wallpaper_gradient);
    }
    
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh app list when returning to launcher
        if (appAdapter != null) {
            appAdapter.updateApps(loadInstalledApps());
        }
    }
    
    // Dock item helper class
    private static class DockItem {
        String packageName;
        String label;
        int iconRes;
        
        DockItem(String packageName, String label, int iconRes) {
            this.packageName = packageName;
            this.label = label;
            this.iconRes = iconRes;
        }
    }
}
