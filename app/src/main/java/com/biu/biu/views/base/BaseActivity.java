package com.biu.biu.views.base;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {

	
	public void showNonCanclebleDialog(Context context, String message) {
		new AlertDialog.Builder(context).setMessage(message).setTitle("提示").show();
	}
	
	public void showToast(String message){
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

}
