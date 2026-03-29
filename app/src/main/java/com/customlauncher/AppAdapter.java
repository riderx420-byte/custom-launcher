package com.customlauncher;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {
    
    private Context context;
    private List<AppInfo> appList;
    private List<AppInfo> filteredList;
    
    public AppAdapter(Context context, List<AppInfo> appList) {
        this.context = context;
        this.appList = appList;
        this.filteredList = new ArrayList<>(appList);
    }
    
    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.app_item, parent, false);
        return new AppViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppInfo appInfo = filteredList.get(position);
        holder.appIcon.setImageDrawable(appInfo.getAppIcon());
        holder.appName.setText(appInfo.getAppName());
        
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setPackage(appInfo.getPackageName());
            intent.setClassName(appInfo.getPackageName(), appInfo.getActivityName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
        
        holder.itemView.setOnLongClickListener(v -> {
            // Long press - could open app info or show options
            android.widget.Toast.makeText(context, 
                "Long press: " + appInfo.getAppName(), 
                android.widget.Toast.LENGTH_SHORT).show();
            return true;
        });
    }
    
    @Override
    public int getItemCount() {
        return filteredList.size();
    }
    
    public void updateApps(List<AppInfo> newApps) {
        this.appList = newApps;
        this.filteredList = new ArrayList<>(newApps);
        notifyDataSetChanged();
    }
    
    public void filter(String query) {
        if (query.isEmpty()) {
            filteredList = new ArrayList<>(appList);
        } else {
            filteredList = new ArrayList<>();
            String lowerQuery = query.toLowerCase();
            for (AppInfo app : appList) {
                if (app.getAppName().toLowerCase().contains(lowerQuery)) {
                    filteredList.add(app);
                }
            }
        }
        notifyDataSetChanged();
    }
    
    static class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        
        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
        }
    }
}
