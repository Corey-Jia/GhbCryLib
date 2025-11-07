package com.ghb.cry.app;

import android.app.Application;
import android.util.Log;
import com.controller.lib.DmSdk;
import com.controller.lib.OnAndroidPlugListener;
import com.jd.wxb.netpie.library.NetPieSDK;
import com.jd.wxb.netpie.library.PluginOutputListener;
import com.jd.wxb.netpie.library.PluginType;
import org.jetbrains.annotations.NotNull;

public class CryApplication extends Application {
    private static final String TAG = "CryApplication";
    private static final String NETPIE_VENDOR_UUID = "netpie.vendor_uuid";
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate");

        DmSdk.INSTANCE.init(this, null, "ADChannel_01");
        //CryUtils.getInstance(this).init();
        DmSdk.INSTANCE.setAndroidPlugListener(new OnAndroidPlugListener() {

            @Override public void onAndroidPlug(boolean b) {
                Log.d(TAG, "onAndroidPlug_" + b);
                if (b) {
                    pixelFireInit();
                }
            }
        });
    }

    private void pixelFireInit() {
        Log.d(TAG, "pixelFireInit");
        //// 初始化SDK
        NetPieSDK.init(this);

        PluginOutputListener outputListener = new PluginOutputListener() {
            @Override public void onOutput(@NotNull String s) {
                Log.d(TAG, "onOutput_" + s);
            }
        };
        // 启动服务
        NetPieSDK.startPluginService(this, PluginType.EIP, false, outputListener);
    }
}