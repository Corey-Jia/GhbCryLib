package com.ghb.cry;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //// Step 1， 设置渠道（可选），只允许0-9A-Za-z
    //sdk.PixelFire.getInstance(this).setChannel("tvos/subchannel01");
    //
    //// Step 2， 初始化
    //// 建议 Step 1、2 两步只在Application中进行一次全局初始化
    //sdk.PixelFire.getInstance(this).init();
    CryUtils.getInstance(this).init();

  }
}