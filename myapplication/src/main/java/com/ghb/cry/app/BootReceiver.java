package com.ghb.cry.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";
    private static final String META_DATA_AUTO_START = "cry.auto_start";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent != null ? intent.getAction() : "null";
            Log.d(TAG, "BootReceiver onReceive: action = " + action);
            Log.d(TAG, "Context package: " + context.getPackageName());
            Log.d(TAG, "Is system app: " + isSystemApp(context));
            
            //// 检查是否启用自动启动
            //boolean autoStartEnabled = checkAutoStartEnabled(context);
            //Log.d(TAG, "Auto start enabled: " + autoStartEnabled);
            //
            //if (autoStartEnabled) {
            //    Log.d(TAG, "Attempting to start service after boot");
            //    startBackgroundService(context);
            //} else {
            //    Log.d(TAG, "Auto start disabled, skipping service start");
            //}
        } catch (Exception e) {
            Log.e(TAG, "Error in onReceive: " + e.getMessage(), e);
        }
    }
    
    private boolean isSystemApp(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), 0);
            return (ai.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking if system app", e);
            return false;
        }
    }
    
    private boolean checkAutoStartEnabled(Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (ai.metaData != null) {
                boolean value = ai.metaData.getBoolean(META_DATA_AUTO_START, true);
                Log.d(TAG, "Meta-data auto_start value: " + value);
                return value; // 默认开启
            } else {
                Log.d(TAG, "No meta-data found, using default value");
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to get application info", e);
        }
        return true; // 默认开启
    }
    
    private void startBackgroundService(Context context) {
        try {
            Log.d(TAG, "Starting CryBackgroundService...");
            /*Intent serviceIntent = new Intent(context, CryBackgroundService.class);
            
            // 添加额外的标识，便于跟踪服务启动来源
            serviceIntent.putExtra("START_FROM_BOOT", true);
            
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //    Log.d(TAG, "Using startForegroundService (Oreo+)");
            //    context.startForegroundService(serviceIntent);
            //} else {
                Log.d(TAG, "Using startService (pre-Oreo)");
                context.startService(serviceIntent);
            //}
            Log.d(TAG, "Service start intent sent successfully");*/
        } catch (Exception e) {
            Log.e(TAG, "Error starting service: " + e.getMessage(), e);
        }
    }
}