package com.biu.biu.app;

import android.app.Application;
import android.util.Log;

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
	}
}
