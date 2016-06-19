package com.biu.biu.utils;

import com.biu.biu.app.BiuApp;

/**
 * Created by fubo on 2016/6/2 0002.
 * email:bofu1993@163.com
 */
public class GlobalString {

  public static final String PACKAGE_NAME = BiuApp.getContext().getPackageName();
  public static final String BASE_URL = "http://api.bbbiu.com:1234";
  public static final String URI_RES_PREFIX = "android.resource://" + PACKAGE_NAME + "/";

}
