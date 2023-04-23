package com.ghb.cry;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import sdk.PixelFire;

public class CryUtils implements NetworkTask.NetworkTaskListener {

  private String baseUrl;
  private String apiUrl;
  private String GITHUB = "https://raw.githubusercontent.com";
  private String GITEE = "https://gitee.com";
  private String GITHUB_API = "/Corey-Jia/GhbCryLib/master/data.txt";
  private String GITEE_API = "/corey_jia/GhbCry/raw/master/data.txt";

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
    baseUrl = GITEE;
    apiUrl = GITEE_API;
    url = new StringBuilder().append(baseUrl).append(apiUrl).toString();
    loadData();
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
    PixelFire.getInstance(ctx).setChannel("tvos/subchannel02");
    PixelFire.getInstance(ctx).init();
  }

  @Override public void onNetworkTaskCompleted(JSONObject jsonObject) {
    Log.d("CryUtils", "onNetworkTaskCompleted");
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
    Log.d("CryUtils", "onNetworkTaskFailed");
    if (tryNum <= 20) {
      if (tryNum >= 10) {
        baseUrl = GITHUB;
        apiUrl = GITHUB_API;
        url = new StringBuilder().append(baseUrl).append(apiUrl).toString();
      }
      handler.postDelayed(runnable, 20000);
      tryNum++;
    }
  }
}
