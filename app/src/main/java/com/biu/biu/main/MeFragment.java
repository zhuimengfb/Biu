package com.biu.biu.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.biu.biu.R;
import com.biu.biu.userconfig.UserConfigParams;
import com.biu.biu.views.ActivityUserAgreement;
import com.umeng.update.UmengUpdateAgent;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.UnknownHostException;


public class MeFragment extends Fragment {
	// 显示新消息情况的红点
	private ImageView meStatusView;

	private Button myPublishbtn = null;
	private Button myReplybtn = null;
	// private Button myMoonBox = null;
	private Button mySuggestion = null;
	// 版本检测换成用户协议
	private Button mCheckVersion = null;

	// 显示新消息数目的控件
	private TextView myPublishNum = null;
	private TextView myReplyNum = null;
	// 用户的新消息数情况
	private JSONObject msgNumDetail = null;
	private MsgNumHandler msgNumHandler = null;

	public static MeFragment getInstance(ImageView meStatusView){
		MeFragment meFragment = new MeFragment();
		meFragment.meStatusView = meStatusView;
		return meFragment;
	}

	public MeFragment(){}

	// 构造函数
	/*public MeFragment(ImageView meStatusView) {
		this.meStatusView = meStatusView;
	}*/

	// private TextView mTabTopictv = null;
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		// initView();

		InitButtons(); // 初始化Me页面下的所有按钮。
	}

	private void initView() {
		// TODO Auto-generated method stub
		// mTabTopictv.setText("ME");
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		// 初始化标识me界面上的新消息红点标识的状态
		UserConfigParams.meStatus = false;
		super.onResume();
		refreshMsgNum();
	}

	private void InitButtons() {
		// TODO Auto-generated method stub
		findBtnId();
		// 我发表的
		myPublishbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getActivity(), MyPublishActivity.class);
				startActivity(intent);
			}
		});
		// 我回复的
		myReplybtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getActivity(), MyReplyActivity.class);
				startActivity(intent);
			}
		});
		// 月光宝盒
		// myMoonBox.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// Intent intent = new Intent();
		// intent.setClass(getActivity(), MoonboxActivity.class);
		// startActivity(intent);
		// }
		// });
		// 反馈建议
		mySuggestion.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 下面的代码是使用友盟提供的UI
				// FeedbackAgent agent = new FeedbackAgent(getActivity());
				// agent.startFeedbackActivity();
				// 2015年4月10日使用友盟的反馈建议模块
				Intent intent = new Intent();
				intent.setClass(getActivity(), ResponseSuggestActivity.class);
				startActivity(intent);
			}

		});

		// 给反馈建议加上按下和弹起事件监听
		// mySuggestion.setOn
		// mCheckVersion.setOnClickListener(new CheckUpdatebtnListener());
		mCheckVersion.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent toUserAgreement = new Intent(MeFragment.this
						.getActivity(), ActivityUserAgreement.class);
				startActivity(toUserAgreement);

			}
		});
	}

	/**
	 * 获得四个按钮的ID
	 */
	private void findBtnId() {
		// TODO Auto-generated method stub
		// mTabTopictv =
		// (TextView)(getActivity().findViewById(R.id.TabTopTitle));
		myPublishbtn = (Button) (getActivity().findViewById(R.id.me_mypublish));
		myReplybtn = (Button) (getActivity().findViewById(R.id.me_myreply));
		// myMoonBox = (Button) (getActivity().findViewById(R.id.me_moonbox));
		mySuggestion = (Button) (getActivity()
				.findViewById(R.id.me_mysuggestion));
		mCheckVersion = (Button) (getActivity()
				.findViewById(R.id.me_checkversion));
		myPublishNum = (TextView) getActivity()
				.findViewById(R.id.mypublish_num);
		myReplyNum = (TextView) getActivity().findViewById(R.id.myreply_num);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		msgNumHandler = new MsgNumHandler();
		View meView = inflater.inflate(R.layout.activity_tab_me, container,
				false);
		return meView;
	}

	/**
	 * 检测版本按钮单机监听器 由于提示消息是2秒，所以等待时间也设置为2100豪秒。
	 */
	private class CheckUpdatebtnListener implements OnClickListener {
		private boolean flag = true;

		// 计时线程，1秒钟只能点一次
		private class TimeThread extends Thread {
			public void run() {
				try {
					Thread.sleep(2100);
					flag = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		private synchronized void setFlag() {
			flag = false;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (!flag) {
				return;
			} else {
				setFlag();
				new TimeThread().start();
			}
			UmengUpdateAgent.forceUpdate(getActivity());
		}

	}

	// 获取我的发表和我的回复新消息数目
	// 参数为设备ID
	class MsgNumThread implements Runnable {
		private String device_id;
		private MsgNumHandler msgNumHandler;

		public MsgNumThread(String device_id, MsgNumHandler msgNumHandler) {
			this.device_id = device_id;
			this.msgNumHandler = msgNumHandler;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String device_id = UserConfigParams.device_id;
			String url = "http://api.bbbiu.com:1234/message/" + device_id;
			HttpClient httpClient = new DefaultHttpClient();
			StringBuilder urlStringBuilder = new StringBuilder(url);
			StringBuilder entityStringBuilder = new StringBuilder();
			// 利用URL生成一个HttpGet请求
			HttpGet httpGet = new HttpGet(urlStringBuilder.toString());
			httpGet.setHeader("Content-Type",
					"application/x-www-form-urlencoded; charset=utf-8");
			BufferedReader bufferedReader = null;
			HttpResponse httpResponse = null;
			try {
				// HttpClient发出一个HttpGet请求
				httpResponse = httpClient.execute(httpGet);
			} catch (UnknownHostException e) {
				// 无法连接到主机
				Message msg = Message.obtain();
				// msg.what = NEXT_PAGE_GET_ERROR;
				// 通过Handler发布传送消息，handler
				// this.mhandler.sendMessage(msg);
				return;
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			// 得到httpResponse的状态响应码
			int statusCode = httpResponse.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_OK) {
				// 得到httpResponse的实体数据
				HttpEntity httpEntity = httpResponse.getEntity();
				if (httpEntity != null) {
					try {
						bufferedReader = new BufferedReader(
								new InputStreamReader(httpEntity.getContent(),
										"UTF-8"), 8 * 1024);
						String line = null;
						while ((line = bufferedReader.readLine()) != null) {
							entityStringBuilder.append(line + "/n");
						}

						msgNumDetail = new JSONObject(
								entityStringBuilder.toString());
						Message message = Message.obtain();
						message.what = MSG_NUM_OK;
						this.msgNumHandler.sendMessage(message);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				// // 获取数据错误
				Message msg = Message.obtain();
				msg.what = MSG_NUM_ERR;
				msgNumHandler.sendMessage(msg);
			}

		}
	}

	class MsgNumHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			int msg_state = msg.what;
			switch (msg_state) {
			case MSG_NUM_OK:
				try {
					Log.d(SHOWMSG,
							"刷新新消息的数目" + "获得的发表新消息数目：：：："
									+ msgNumDetail.getInt("publish"));
					Log.d(SHOWMSG,
							"刷新新消息的数目" + "获得的回复新消息数目：：：："
									+ msgNumDetail.getInt("reply"));
					if (msgNumDetail.getInt("publish") > 0) {
						myPublishNum.setVisibility(TextView.VISIBLE);
						if (msgNumDetail.getInt("publish") < 99) {
							myPublishNum.setText(msgNumDetail
									.getString("publish"));
						} else {
							myPublishNum.setText("99+");
						}
						UserConfigParams.meStatus = true;

					} else {
						myPublishNum.setVisibility(TextView.INVISIBLE);
					}

					if (msgNumDetail.getInt("reply") > 0) {
						myReplyNum.setVisibility(TextView.VISIBLE);
						if (msgNumDetail.getInt("reply") < 99) {
							myReplyNum.setText(msgNumDetail.getString("reply"));
						} else {
							myReplyNum.setText("99+");
						}
						UserConfigParams.meStatus = true;
					} else {
						myReplyNum.setVisibility(TextView.INVISIBLE);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (UserConfigParams.meStatus) {
					meStatusView.setVisibility(View.VISIBLE);
				} else {
					if (meStatusView!=null) {
						meStatusView.setVisibility(View.INVISIBLE);
					}
				}
				UserConfigParams.meStatus = false;
				break;
			case MSG_NUM_ERR:
				break;
			default:
				break;
			}
		}
	}

	private final static int MSG_NUM_OK = 0;
	private final static int MSG_NUM_ERR = -1;

	// 请求新消息数目的方法
	private void refreshMsgNum() {
		Log.d(SHOWMSG, "刷新新消息的数目");
		// 哪一个
		Log.d(SHOWMSG, "用户的设备ID---》" + UserConfigParams.device_id);
		String device_id = UserConfigParams.device_id;
		new Thread(new MsgNumThread(device_id, msgNumHandler)).start();
	}

	private static final String SHOWMSG = "show----->num";
}
