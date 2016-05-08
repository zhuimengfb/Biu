package com.biu.biu.views.base;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {

	
	public void showNonCanclebleDialog(Context context, String message) {
		new AlertDialog.Builder(context).setMessage(message).setTitle("提示").show();
	}
	
	public void showToast(String message){
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	public void setBackableToolbar(Toolbar toolbar) {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
}
