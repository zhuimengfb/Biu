package com.biu.biu.views;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

import com.biu.biu.R;
import com.biu.biu.main.MainActivity;
import com.biu.biu.userconfig.UserConfigParams;
import com.biu.biu.views.base.BaseActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.UUID;

public class LaunchActivity extends BaseActivity {

	private static final int REQUEST_READ_PHONE_STATE_CODE = 1;
	private SharedPreferences preferences; // 存储设备ID

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
			finish();
		}
		checkReadStatePermission();
	}
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	private void checkReadStatePermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			int checkDeviceIdPermission = ContextCompat.checkSelfPermission(
					this, Manifest.permission.READ_PHONE_STATE);
			if (checkDeviceIdPermission != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.READ_PHONE_STATE},
						REQUEST_READ_PHONE_STATE_CODE);
			} else {
				toMainActivity();
			}
		} else {
			toMainActivity();
		}
	}
	private void toMainActivity() {
		getDeviceuuId();
		Intent mainIntent = new Intent(LaunchActivity.this, MainActivity.class);
		startActivity(mainIntent);
		finish();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	@Override
	public void onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {
		// TODO Auto-generated method stub
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_READ_PHONE_STATE_CODE) {
			if (grantResults.length > 0
					&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				toMainActivity();
			} else {
				new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.hint))
					.setMessage(getResources().getString(
						R.string.you_need_to_grant_read_phone_state_permission))
						.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								checkReadStatePermission();
							}
						}).show();

			}
		}
	}
	/*
	 * 获得设备ID编号
	 */
	private String getDeviceID() {
		// TODO Auto-generated method stub
		String result = null;
		final TelephonyManager tm = (TelephonyManager) getBaseContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		result = deviceUuid.toString();
		return result;
	}
	private void getDeviceuuId() {
		preferences = getSharedPreferences("user_Params", MODE_PRIVATE);
		// device_ID = preferences.getString("device_ID", "");
		String device_ID = getDeviceID();
		UserConfigParams.device_id = device_ID;
		Editor editor = preferences.edit();
		editor.putString("device_ID", device_ID);
		editor.commit();
		// if(UserConfigParams.device_id.isEmpty()){
		// // 如果设备ID为空，则获得并重写配置参数
		// Editor editor = preferences.edit();
		// getDeviceID();
		// // 存入数据
		// editor.putString("device_ID", device_ID);
		// editor.commit();
		// }
	}
}
