package com.ghb.cry;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpTask extends AsyncTask<String, Void, String> {

   private static final String TAG = "HttpTask";
   public interface HttpTaskListener {
      void onCallBack();
   }

   @Override
   protected String doInBackground(String... urls) {
      String result = null;
      try {
         result = downloadUrl(urls[0]);
      } catch (IOException e) {
         Log.e(TAG, "Error downloading URL: " + e.getMessage());
      }
      return result;
   }

   @Override
   protected void onPostExecute(String result) {
      super.onPostExecute(result);
      try {
         JSONObject jsonObject = new JSONObject(result);
         JSONArray jsonArray = jsonObject.getJSONArray("results");
         for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            String name = object.getString("name");
            int age = object.getInt("age");
            Log.d(TAG, "Name: " + name + ", Age: " + age);
         }
      } catch (JSONException e) {
         Log.e(TAG, "Error parsing JSON: " + e.getMessage());
      }
   }

   private String downloadUrl(String urlString) throws IOException {
      InputStream inputStream = null;
      HttpURLConnection urlConnection = null;
      String result = null;
      try {
         URL url = new URL(urlString);
         urlConnection = (HttpURLConnection) url.openConnection();
         urlConnection.setRequestMethod("GET");
         urlConnection.setReadTimeout(10000 /* milliseconds */);
         urlConnection.setConnectTimeout(15000 /* milliseconds */);
         urlConnection.connect();
         inputStream = urlConnection.getInputStream();
         result = readStream(inputStream);
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
         if (urlConnection != null) {
            urlConnection.disconnect();
         }
      }
      return result;
   }

   private String readStream(InputStream inputStream) throws IOException {
      BufferedReader reader = null;
      StringBuilder builder = new StringBuilder();
      try {
         reader = new BufferedReader(new InputStreamReader(inputStream));
         String line;
         while ((line = reader.readLine()) != null) {
            builder.append(line);
         }
      } finally {
         if (reader != null) {
            reader.close();
         }
      }
      return builder.toString();
   }
}

