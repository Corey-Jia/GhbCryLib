package com.ghb.cry.app;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

public class CryApplication extends Application {
    private static final String TAG = "CryApplication";
    private static final String META_DATA_AUTO_START = "cry.auto_start";
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate");
        
        // 使用包内的CryUtils引用
        CryUtils.getInstance(this).init();
        // 移除了自动启动逻辑，仅通过BootReceiver在开机时启动服务
        // 如需在应用启动时也启动服务，可取消下面的注释
        /*
        boolean autoStart = checkAutoStartEnabled();
        Log.d(TAG, "Auto start enabled: " + autoStart);
        
        if (autoStart) {
            startBackgroundService();
        }
        */
    }
    
    private boolean checkAutoStartEnabled() {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA);
            if (ai.metaData != null) {
                return ai.metaData.getBoolean(META_DATA_AUTO_START, true); // 默认开启
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to get application info", e);
        }
        return true; // 默认开启
    }
    
    private void startBackgroundService() {
        Intent serviceIntent = new Intent(this, com.ghb.cry.app.CryBackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }
}