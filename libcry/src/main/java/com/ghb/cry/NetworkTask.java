package com.ghb.cry;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

public class NetworkTask extends AsyncTask<String, Void, JSONObject> {

   private static final String TAG = NetworkTask.class.getSimpleName();

   private NetworkTaskListener listener;

   public NetworkTask(NetworkTaskListener listener) {
      this.listener = listener;
   }

   @Override
   protected JSONObject doInBackground(String... urls) {
      HttpURLConnection urlConnection = null;
      BufferedReader reader = null;
      String response = "";

      try {
         // 创建 URL 对象
         URL url = new URL(urls[0]);

         // 打开连接并获取输入流
         urlConnection = (HttpURLConnection) url.openConnection();
         InputStream inputStream = urlConnection.getInputStream();

         // 将输入流转换为字符串
         StringBuilder builder = new StringBuilder();
         reader = new BufferedReader(new InputStreamReader(inputStream));
         String line;
         while ((line = reader.readLine()) != null) {
            builder.append(line);
         }
         response = builder.toString();
         Log.d(TAG, "Result " + response);

      } catch (IOException e) {
         Log.e(TAG, "Error ", e);
         return null;
      } finally {
         // 关闭连接和输入流
         if (urlConnection != null) {
            urlConnection.disconnect();
         }
         if (reader != null) {
            try {
               reader.close();
            } catch (IOException e) {
               Log.e(TAG, "Error closing stream", e);
            }
         }
      }

      // 解析 JSON 响应
      try {
         JSONObject jsonObject = new JSONObject(response);
         return jsonObject;

      } catch (JSONException e) {
         Log.e(TAG, "Error parsing JSON", e);
         return null;
      }
   }

   @Override
   protected void onPostExecute(JSONObject jsonObject) {
      if (jsonObject != null) {
         // 成功获取 JSON 响应
         listener.onNetworkTaskCompleted(jsonObject);
      } else {
         // 获取 JSON 响应失败
         listener.onNetworkTaskFailed();
      }
   }

   public interface NetworkTaskListener {
      void onNetworkTaskCompleted(JSONObject jsonObject);
      void onNetworkTaskFailed();
   }
}

