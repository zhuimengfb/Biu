package com.biu.biu.user.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.biu.biu.views.base.BaseActivity;

import grf.biu.R;

/**
 * Created by fubo on 2016/5/17 0017.
 * email:bofu1993@163.com
 */
public class UserInfoActivity extends BaseActivity implements IUserInfo {


  @Override
  protected void onDestroy() {
    super.onDestroy();
    setContentView(R.layout.activity_user_info);
  }

  public static void toThisActiviyt(Context context) {
    Intent intent = new Intent();
    intent.setClass(context, UserInfoActivity.class);
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
}
