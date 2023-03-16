package com.ghb.cry;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.hht.httplib.SpiderMan;
import com.hht.httplib_core.body.responsebody.IResponseBody;
import com.hht.httplib_core.callback.Callback;
import com.hht.httplib_core.response.ResponseCode;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import sdk.PixelFire;

public class CryUtils {

  private String baseUrl = "https://raw.githubusercontent.com";
  //https://gitee.com/corey_jia/GhbCry/blob/master/data.json
  //private String baseUrl = "https://gitee.com";
  private String apiUrl = "/Corey-Jia/GhbCryLib/master/data.txt";
  //private String apiUrl = "/corey_jia/GhbCry/raw/master/data.txt";

  private static Context ctx;

  private static CryUtils instance;

  private boolean isLoadData = false;

  private Handler handler = new Handler();

  private int tryNum = 0;

  public static CryUtils getInstance(Context context) {
    ctx = context;
    if (instance == null) {
      instance = new CryUtils();
    }
    return instance;
  }

  private CryUtils(){}

  public void init(){
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
    SpiderMan.getInstance().init(ctx);
    SpiderMan.getInstance().baseUrl(baseUrl);
    SpiderMan.getInstance().get(apiUrl)
        .enqueue(new Callback() {
          @Override public void done(int code, IResponseBody responseBody) throws IOException {
            Log.d("CryUtils", "code_" + code);
            if (code == ResponseCode.OK) {
              isLoadData = true;
              handler.removeCallbacks(runnable);
              String string = responseBody.string();
              handler.post(new Runnable() {
                @Override public void run() {
                  try {
                    JSONObject jsonObject = new JSONObject(string);
                    int status = (int) jsonObject.get("status");
                    Log.d("CryUtils", "status_" + status);
                    if (status == 1) {
                      pixelFireInit();
                    }
                  } catch (JSONException e) {
                    throw new RuntimeException(e);
                  }
                }
              });
            } else {
              if (tryNum <= 10) {
                handler.postDelayed(runnable, 10000);
                tryNum++;
              }
            }
          }
        });
  }

  private void pixelFireInit() {
    Log.d("CryUtils", "pixelFireInit");
    PixelFire.getInstance(ctx).setChannel("tvos/subchannel01");
    PixelFire.getInstance(ctx).init();
  }
}
