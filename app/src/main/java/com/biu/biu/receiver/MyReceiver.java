package com.biu.biu.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.biu.biu.main.MainActivity;
import com.biu.biu.thread.PostRegIDThread;
import com.biu.biu.userconfig.UserConfigParams;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p/>
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
  private static final String TAG = "BiuPush";
  // private static final int SEND_RIGID = 1;
  // public static int newNum = 0;
  // 开启一个线程将regId传递给应用服务器后台
  private Handler regIdHandler = new Handler() {
    public void handleMessage(android.os.Message msg) {
      switch (msg.what) {
        case PostRegIDThread.RIGID_OK:
          break;
        case PostRegIDThread.RIGID_ERR:
          break;
        default:
          break;
      }
    }

    ;
  };

  @Override
  public void onReceive(Context context, Intent intent) {
    Bundle bundle = intent.getExtras();
    switch (intent.getAction()) {
      case JPushInterface.ACTION_REGISTRATION_ID:
        String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
        String regIdUrl = "http://api.bbbiu.com:1234/register/push";
        String device_id = UserConfigParams.device_id;
        if (device_id != null) {
          new Thread(new PostRegIDThread(regIdHandler, regIdUrl, device_id, regId)).start();
        }
        break;
      case JPushInterface.ACTION_MESSAGE_RECEIVED:
        Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: "
            + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        break;
      case JPushInterface.ACTION_NOTIFICATION_RECEIVED:
        Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
        int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
        // 每次收到通知的时候将数值+1
        UserConfigParams.badgeNum += 1;
        Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        break;
      case JPushInterface.ACTION_NOTIFICATION_OPENED:
        Log.i(TAG, "[MyReceiver] 用户点击打开了通知");
        // 获取点击的通知ID并让其消失
        Bundle tempDundle = intent.getExtras();
        int notificationId = tempDundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
        JPushInterface.clearNotificationById(context, notificationId);
        // 用户打开通知 先不做处理了
        // 打开自定义的Activity
        Intent i = new Intent(context, MainActivity.class);
        i.putExtras(bundle);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(i);
        break;
      case JPushInterface.ACTION_RICHPUSH_CALLBACK:
        Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface
            .EXTRA_EXTRA));
        break;
      case JPushInterface.ACTION_CONNECTION_CHANGE:
        boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        break;
      case "cn.jpush.im.android.action.IM_RESPONSE":
        Log.d("1", "1");
        break;
      default:
        break;
    }
  }






}
