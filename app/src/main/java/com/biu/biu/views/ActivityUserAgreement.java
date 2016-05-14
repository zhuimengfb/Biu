package com.biu.biu.views;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.biu.biu.views.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import grf.biu.R;

public class ActivityUserAgreement extends BaseActivity {


	@BindView(R.id.toolbar_user_agreement)
	Toolbar toolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_user_agreement);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		setBackableToolbar(toolbar);
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
