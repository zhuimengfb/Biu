package com.biu.biu.user.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.biu.biu.app.BiuApp;
import com.biu.biu.views.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import grf.biu.R;

/**
 * Created by fubo on 2016/5/17 0017.
 * email:bofu1993@163.com
 */
public class UserInfoActivity extends BaseActivity implements IUserInfo {

  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.toolbar_title)
  TextView toolbarTitle;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_info);
    ButterKnife.bind(this);
    initView();
  }

  private void initView() {
    setBackableToolbar(toolbar);
    toolbarTitle.setText(getString(R.string.edit_user_info));
    toolbarTitle.setTypeface(BiuApp.globalTypeface);
  }
  public static void toThisActivity(Context context) {
    Intent intent = new Intent();
    intent.setClass(context, UserInfoActivity.class);
    context.startActivity(intent);
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
  }
}
