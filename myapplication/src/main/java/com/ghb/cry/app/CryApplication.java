package com.ghb.cry.app;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import com.controller.lib.DmSdk;

public class CryApplication extends Application {
    private static final String TAG = "CryApplication";
    private static final String NETPIE_VENDOR_UUID = "netpie.vendor_uuid";
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate");
        
        // 使用包内的CryUtils引用
        CryUtils.getInstance(this).init();
        String channel = getChannel();
        DmSdk.INSTANCE.init(this, null);
    }
    
    private String getChannel() {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA);
            if (ai.metaData != null) {
                return ai.metaData.getString(NETPIE_VENDOR_UUID, ""); // 默认开启
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to get application info", e);
        }
        return "";
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