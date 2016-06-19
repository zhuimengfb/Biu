package com.biu.biu.user.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.biu.biu.app.BiuApp;
import com.biu.biu.utils.GlobalString;

/**
 * Created by fubo on 2016/6/2 0002.
 * email:bofu1993@163.com
 */
public class UserPreferenceUtil {

  public static final String USER_PREFERENCE_KEY = "user_Params";
  public static final String USER_PREFERENCE_NAME_KEY = GlobalString.PACKAGE_NAME + ".user_name";
  public static final String USER_UPDATE_NICK_NAME_FAIL = GlobalString.PACKAGE_NAME + "" +
      ".update_nick_name";
  public static final String USER_ID_KEY = "device_ID";
  public static final String USER_ICON_ADDRESS_KEY = GlobalString.PACKAGE_NAME + "" +
      ".user_icon_address";
  private static SharedPreferences preferences;

  private UserPreferenceUtil() {
  }

  public static SharedPreferences getPreferences() {
    if (preferences == null) {
      synchronized (UserPreferenceUtil.class) {
        if (preferences == null) {
          preferences = BiuApp.getContext().getSharedPreferences(USER_PREFERENCE_KEY,
              Context.MODE_PRIVATE);
        }
      }
    }
    return preferences;
  }

  public static void setUpdateNickNameFail() {
    getPreferences().edit().putBoolean(USER_UPDATE_NICK_NAME_FAIL, true).apply();
  }

  public static void setUpdateNickNameSuccess() {
    getPreferences().edit().putBoolean(USER_UPDATE_NICK_NAME_FAIL, false).apply();
  }

  public static String getUserPreferenceNickName() {
    return getPreferences().getString(USER_PREFERENCE_NAME_KEY, "");
  }

  public static String getUserPreferenceId() {
    return getPreferences().getString(USER_ID_KEY, "");
  }

  public static void setUserIconLocalAddress(String localAddress) {
    getPreferences().edit().putString(USER_ICON_ADDRESS_KEY, localAddress).apply();
  }

  public static String getUserIconAddress() {
    return getPreferences().getString(USER_ICON_ADDRESS_KEY, "");
  }
}
