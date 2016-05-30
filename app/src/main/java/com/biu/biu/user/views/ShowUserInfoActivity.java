package com.biu.biu.user.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;

import com.biu.biu.views.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import grf.biu.R;

/**
 * Created by fubo on 2016/5/27 0027.
 * email:bofu1993@163.com
 */
public class ShowUserInfoActivity extends BaseActivity {

  @BindView(R.id.view_pager_show_user_pic)
  ViewPager viewPager;
  @BindView(R.id.indicator_layout)
  LinearLayout indicatorLayout;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_show_user_info);
    ButterKnife.bind(this);
  }
}
