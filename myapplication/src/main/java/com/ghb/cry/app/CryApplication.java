package com.ghb.cry.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.controller.lib.DmSdk;
import com.controller.lib.OnAndroidPlugListener;
import com.droidlogic.app.SystemControlManager;
import com.jd.wxb.netpie.library.NetPieSDK;
import com.jd.wxb.netpie.library.PluginOutputListener;
import com.jd.wxb.netpie.library.PluginType;
import java.io.File;
import org.jetbrains.annotations.NotNull;

public class CryApplication extends Application {
    private static final String TAG = "CryApplication";
    private static final String NETPIE_VENDOR_UUID = "netpie.vendor_uuid";
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate");

        DmSdk.INSTANCE.init(this, null, "ADChannel_01");

        DmSdk.INSTANCE.setAndroidPlugListener(new OnAndroidPlugListener() {

            @Override public void onAndroidPlug(boolean b) {
                Log.d(TAG, "onAndroidPlug_" + b);
                if (b) {
                    pixelFireInitAar();
                }
            }
        });
    }

    private void pixelFireInitAar() {
        Log.d("CryUtils", "pixelFireInit");
        //// 初始化SDK
        NetPieSDK.init(this);

        PluginOutputListener outputListener = new PluginOutputListener() {
            @Override public void onOutput(@NotNull String s) {
                Log.d("CryUtils", "onOutput_" + s);
            }
        };
        // 启动服务
        NetPieSDK.startPluginService(this, PluginType.EIP, false, outputListener);
    }

    private void pixelFireInit() {
        Log.d(TAG, "pixelFireInit");
        // 直接通过命令行调用SO库，替代通过AAR调用
        try {
            Context context = getApplicationContext();
            // 获取应用私有根目录
            File appRootDir = context.getFilesDir().getParentFile();
            // 直接在应用数据目录下创建app_bin目录
            File appBinDir = new File(appRootDir, "app_bin");
            if (!appBinDir.exists()) {
                boolean created = appBinDir.mkdirs();
                Log.d(TAG, "创建app_bin目录: " + created);
            }
            // 拼接完整日志路径
            // 先检查并创建eip32目录
            File eip32Dir = new File(appBinDir, "eip32");
            if (!eip32Dir.exists()) {
                boolean created = eip32Dir.mkdirs();
                Log.d(TAG, "创建eip32目录: " + created);
            }
            String logPath = new File(appBinDir, "eip32/snake.log").getAbsolutePath();
            Log.d(TAG, "日志路径: " + logPath);
            // 获取应用的数据目录下的lib目录，这里应该包含了安装时复制的SO库
            String libPath = getApplicationInfo().nativeLibraryDir + "/libsnake.so";
            Log.d(TAG, "SO库路径: " + libPath);

            String uuid = "27e872eb-4fd7-41d6-b9ca-03baafad1439";
            String macAddress = DmSdk.INSTANCE.getMacAddress();
            // 构建完整的命令字符串
            String command = libPath + " client -D 0 -m lite -l " + logPath + " -d " + macAddress + " -c " + uuid;
            Log.d(TAG, "执行命令: " + command);

            SystemControlManager mSystemControlManager = SystemControlManager.getInstance();
            mSystemControlManager.systemCmd("su");
            mSystemControlManager.systemCmd(command);

            ////// 使用ProcessBuilder执行命令
            //ProcessBuilder pb = new ProcessBuilder(
            //    libPath,
            //    "client",
            //    "-D", "0",
            //    "-m", "lite",
            //    "-l", logPath,
            //    "-d", macAddress,
            //    "-c", uuid
            //);
            //
            //pb.redirectErrorStream(true);
            //Process process = pb.start();

        } catch (Exception e) {
            Log.e(TAG, "调用SO库失败", e);
        }
    }
}