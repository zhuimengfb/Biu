package com.biu.biu.app;

import android.app.Application;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.backends.pipeline.Fresco;

import cn.jpush.android.api.JPushInterface;

public class BiuApp extends Application {
	private static final String TAG = "BiuPush";

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d("BiuJPush", "JPush is started!!");
		// JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
		Fresco.initialize(this);
		// 百度地图的初始化，。在应用启动的时候初始化
		SDKInitializer.initialize(getApplicationContext());
		Log.d("packageName", getPackageName());
	}
}
