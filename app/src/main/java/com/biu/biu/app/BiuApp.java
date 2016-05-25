package com.biu.biu.app;

import android.app.Application;
import android.graphics.Typeface;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;

public class BiuApp extends Application {
	private static final String TAG = "BiuPush";
	public static Typeface globalTypeface;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d("BiuJPush", "JPush is started!!");
		// JPushInterface.setDebugMode(true);
		//JPushInterface.init(this);
		Fresco.initialize(this);
		// 百度地图的初始化，。在应用启动的时候初始化
		globalTypeface=Typeface.createFromAsset(getAssets(), "font/lantinghei.TTF");
	}
}
