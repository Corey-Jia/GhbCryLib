package com.ghb.cry.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

public class CryBackgroundService extends Service {
    private static final String TAG = "CryBackgroundService";
    private static final String EXTRA_START_FROM_BOOT = "START_FROM_BOOT";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate - Process ID: " + Process.myPid());
        Log.d(TAG, "Service process name: " + getProcessName());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service onStartCommand - startId: " + startId);
        
        // 检查启动来源
        if (intent != null) {
            boolean startedFromBoot = intent.getBooleanExtra(EXTRA_START_FROM_BOOT, false);
            Log.d(TAG, "Service started from boot: " + startedFromBoot);
            Log.d(TAG, "Intent action: " + intent.getAction());
        } else {
            Log.d(TAG, "Service started with null intent");
        }
        
        // 初始化CryUtils
        try {
            Log.d(TAG, "Attempting to initialize CryUtils...");
            // 调用CryUtils的初始化方法
            CryUtils.getInstance(this).init();
            Log.d(TAG, "CryUtils initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize CryUtils", e);
            // 尝试在错误后重新启动服务
            scheduleRestart();
        }
        
        // 返回START_STICKY以确保服务被杀死后能自动重启
        Log.d(TAG, "Service started with START_STICKY policy");
        return START_STICKY;
    }
    
    private void scheduleRestart() {
        try {
            Log.d(TAG, "Scheduling service restart after initialization failure");
            Intent restartIntent = new Intent(this, CryBackgroundService.class);
            restartIntent.putExtra("RESTART_AFTER_FAILURE", true);
            
            // 使用延迟启动，避免立即重启导致的循环
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000); // 5秒延迟
                        startService(restartIntent);
                        Log.d(TAG, "Restart intent sent successfully");
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to schedule restart", e);
                    }
                }
            }).start();
        } catch (Exception e) {
            Log.e(TAG, "Error scheduling restart", e);
        }
    }
    
    private String getProcessName() {
        try {
            return getApplicationContext().getPackageName() + ":" + 
                   getApplicationContext().getApplicationInfo().processName;
        } catch (Exception e) {
            return "unknown";
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service onDestroy - Process ID: " + Process.myPid());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}