package com.biu.biu.main;


import android.app.Notification;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.baidu.mapapi.SDKInitializer;
import com.biu.biu.R;
import com.biu.biu.thread.PostTempPosition;
import com.biu.biu.userconfig.UserConfigParams;
import com.biu.biu.views.base.BaseActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

/**
 * 
 * @author grf
 * @brief HOME和ME的滑动页面
 */
public class MainActivity extends BaseActivity implements
		AMapLocationListener {
	private List<Fragment> mhomeFragmentList = new ArrayList<Fragment>();
	private HomeFragmentAdapter mFragmentAdapter;

	private LinearLayout clickArea;
	// private ImageView mTabLineIv;
	private HomeFragment mHomeFg;
	private MeFragment mMeFg;
	// 定义热帖页面的碎片
	private HotHomeFragment hotHomeFragment;
	private MoonBoxFragment mMoonboxFg;
	private PeepFragment mPeepFg;
	private ViewPager mPageVp;
	// private GetGPS getGps= null;
	private LocationManagerProxy mLocationManagerProxy;
	// private TextView mTabTopicHomeTv;
	private ImageButton mTabSubmitbtn;
	private int currentIndex; // ViewPager的当前页索引
	private int screenWidth; // 屏幕的宽度
	private boolean mfirstStartInit = true;
	private Double mainLat = 38.00; // 维度
	private Double mainLng = 125.00; // 经度
	private boolean mtellupdateresult = false;
	private LinearLayout mLayout = null;
	// private boolean mIsMoonBoxPub = false; // true则使用MoonBox的发表操作
	final int AIRPLAY_MESSAGE_HIDE_TOAST = 2;
	MainHandler m_Handler;
	private Toast mToast;
	private LinearLayout topLayout;
	// private GestureListener gestureListener;
	private GestureDetector mGestureDetector;
	private boolean timerRefreshOrNot = false;

	// 探索页面的响应区域
	private LinearLayout LinearmainPeepMenu = null;
	// 首页页面的响应区域
	private LinearLayout LinearmainHomeMenu = null;
	// "我"页面的响应区域
	private LinearLayout LinearmainMeMenu = null;
	// "我"页面的响应区域

	private ImageButton mainHomeMenu;
	private ImageButton mainPeepMenu;
	private ImageButton mainMeMenu;
	// 定义两个改变首页内容的按钮控件
	private Button homeFrgNew;
	private Button homeFrgHot;

	// 定义一个变量识别当前菜单按钮的状态
	private int menuStatus = 1;

	// 设定一个变量用于标识和确定最新页面和热门页面的发表按钮的显示和隐藏
	private Boolean showOrNotSubmit = true;
	private TextView mainTitleshow;
	private LinearLayout homeFrgChange;

	// 定义两个imageView显示两个页面的新消息的状态

	private ImageView peepStatusView = null;
	private ImageView meStatusView = null;

	private BottomNavigationBar bottomNavigationBar;
	private Toolbar toolbar;

	// private TopTouchListener topTouchListener;

	/**
	 * 是否提示版本更新结果
	 * 
	 * @param istell
	 */
	public void setistellupdateresult(boolean istell) {
		this.mtellupdateresult = istell;
	}

	// 将推送消息的声音控制放在main这边试试
	// public void setNotification() {
	// BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(
	// getApplicationContext());
	// builder.statusBarDrawable = R.drawable.icon;
	// builder.notificationFlags = Notification.FLAG_AUTO_CANCEL; // 设置为自动消失
	// builder.notificationDefaults = Notification.DEFAULT_SOUND
	// | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS; //
	// // 设置为铃声与震动都要
	// JPushInterface.setDefaultPushNotificationBuilder(builder);
	// }

	// public void setMoonBoxFlag(boolean bFlag){
	// this.mIsMoonBoxPub = bFlag;
	// }
	// 定义通知的样式
	private void defineNotificationStyle() {
		// 传统通知样式
		// 定义通知
		BasicPushNotificationBuilder basicBuild = new BasicPushNotificationBuilder(
				MainActivity.this);
		basicBuild.statusBarDrawable = R.drawable.icon;
		basicBuild.notificationFlags = Notification.FLAG_AUTO_CANCEL
				| Notification.FLAG_SHOW_LIGHTS; // 设置为自动消失和呼吸灯闪烁
		basicBuild.notificationDefaults = Notification.DEFAULT_SOUND
				| Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;
		;
		JPushInterface.setPushNotificationBuilder(1, basicBuild);

		// 自定义样式
		CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(
				MainActivity.this, R.layout.biu_notication_style, R.id.icon,
				R.id.title, R.id.text);
		// 指定定制的 Notification Layout
		builder.statusBarDrawable = R.drawable.icon;
		// 指定最顶层状态栏小图标
		builder.layoutIconDrawable = R.drawable.icon;

		JPushInterface.setPushNotificationBuilder(2, builder);
	}

	// 显示和隐藏topbar内容
	private void showOrHideBar(int num) {
		if (num == 0) {
			homeFrgChange.setVisibility(View.GONE);
			mainTitleshow.setVisibility(View.VISIBLE);
			mainTitleshow.setText("探索");
		}
		if (num == 1) {
			homeFrgChange.setVisibility(View.VISIBLE);
			mainTitleshow.setVisibility(View.GONE);
			// mainTitleshow.setText("探索");
		}
		if (num == 2) {
			homeFrgChange.setVisibility(View.GONE);
			mainTitleshow.setVisibility(View.VISIBLE);
			mainTitleshow.setText("我");
		}
	}

	// 判定是否显示菜单条上面的两个红点标记
	private void showOrHideStatus() {
		if (UserConfigParams.peepStatus) {
			peepStatusView.setVisibility(View.VISIBLE);
		} else {
			peepStatusView.setVisibility(View.INVISIBLE);
		}

		if (UserConfigParams.meStatus) {
			meStatusView.setVisibility(View.VISIBLE);
		} else {
			meStatusView.setVisibility(View.INVISIBLE);
		}

	}

	// 菜单按钮的初始化
	private void initMenuItem() {

		if (UserConfigParams.peepStatus) {
			peepStatusView.setVisibility(View.VISIBLE);
		} else {
			peepStatusView.setVisibility(View.INVISIBLE);
		}

		if (UserConfigParams.meStatus) {
			meStatusView.setVisibility(View.VISIBLE);
		} else {
			meStatusView.setVisibility(View.INVISIBLE);
		}

		// 菜单按钮的触摸事件
		// LinearmainHomeMenu.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// // TODO Auto-generated method stub
		// // 显示首页的两按钮 隐藏TextView
		// showOrHideBar(1);
		// if (menuStatus != 1) {
		// Bitmap tempPic1 = BitmapFactory.decodeResource(
		// getResources(), R.drawable.foot2_1);
		// mainHomeMenu.setImageBitmap(tempPic1);
		// mPageVp.setCurrentItem(1);
		// if (menuStatus == 0) {
		// Bitmap tempPic2 = BitmapFactory.decodeResource(
		// getResources(), R.drawable.foot1_2);
		// mainPeepMenu.setImageBitmap(tempPic2);
		// }
		// if (menuStatus == 2) {
		//
		// Bitmap tempPic3 = BitmapFactory.decodeResource(
		// getResources(), R.drawable.foot3_2);
		// mainMeMenu.setImageBitmap(tempPic3);
		// }
		// menuStatus = 1;
		// // 显示两个控件按钮
		// homeFrgHot.setVisibility(View.VISIBLE);
		// homeFrgNew.setVisibility(View.VISIBLE);
		// }
		// return false;
		// }
		// });

		// 对菜单按钮的初始化
		LinearmainHomeMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 显示首页的两按钮 隐藏TextView
				showOrHideBar(1);
				if (menuStatus != 1) {
					Bitmap tempPic1 = BitmapFactory.decodeResource(
							getResources(), R.drawable.foot2_1);
					mainHomeMenu.setImageBitmap(tempPic1);
					mPageVp.setCurrentItem(1);
					if (menuStatus == 0) {
						Bitmap tempPic2 = BitmapFactory.decodeResource(
								getResources(), R.drawable.foot1_2);
						mainPeepMenu.setImageBitmap(tempPic2);
					}
					if (menuStatus == 2) {

						Bitmap tempPic3 = BitmapFactory.decodeResource(
								getResources(), R.drawable.foot3_2);
						mainMeMenu.setImageBitmap(tempPic3);
					}
					menuStatus = 1;
					// 显示两个控件按钮
					homeFrgHot.setVisibility(View.VISIBLE);
					homeFrgNew.setVisibility(View.VISIBLE);
				}
			}
		});
		mainHomeMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 显示首页的两按钮 隐藏TextView
				showOrHideBar(1);
				if (menuStatus != 1) {
					Bitmap tempPic1 = BitmapFactory.decodeResource(
							getResources(), R.drawable.foot2_1);
					mainHomeMenu.setImageBitmap(tempPic1);
					mPageVp.setCurrentItem(1);
					if (menuStatus == 0) {
						Bitmap tempPic2 = BitmapFactory.decodeResource(
								getResources(), R.drawable.foot1_2);
						mainPeepMenu.setImageBitmap(tempPic2);
					}
					if (menuStatus == 2) {

						Bitmap tempPic3 = BitmapFactory.decodeResource(
								getResources(), R.drawable.foot3_2);
						mainMeMenu.setImageBitmap(tempPic3);
					}
					menuStatus = 1;
					// 显示两个控件按钮
					homeFrgHot.setVisibility(View.VISIBLE);
					homeFrgNew.setVisibility(View.VISIBLE);
				}
			}
		});

		// LinearmainPeepMenu.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// // TODO Auto-generated method stub
		// showOrHideBar(0);
		// if (menuStatus != 0) {
		//
		// Bitmap tempPic1 = BitmapFactory.decodeResource(
		// getResources(), R.drawable.foot1_1);
		// mainPeepMenu.setImageBitmap(tempPic1);
		// mPageVp.setCurrentItem(0);
		// if (menuStatus == 1) {
		// Bitmap tempPic2 = BitmapFactory.decodeResource(
		// getResources(), R.drawable.foot2_2);
		// mainHomeMenu.setImageBitmap(tempPic2);
		// }
		// if (menuStatus == 2) {
		// Bitmap tempPic3 = BitmapFactory.decodeResource(
		// getResources(), R.drawable.foot3_2);
		// mainMeMenu.setImageBitmap(tempPic3);
		// }
		// menuStatus = 0;
		// homeFrgHot.setVisibility(View.INVISIBLE);
		// homeFrgNew.setVisibility(View.INVISIBLE);
		// }
		// return false;
		// }
		// });

		LinearmainPeepMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showOrHideBar(0);
				if (menuStatus != 0) {

					Bitmap tempPic1 = BitmapFactory.decodeResource(
							getResources(), R.drawable.foot1_1);
					mainPeepMenu.setImageBitmap(tempPic1);
					mPageVp.setCurrentItem(0);
					if (menuStatus == 1) {
						Bitmap tempPic2 = BitmapFactory.decodeResource(
								getResources(), R.drawable.foot2_2);
						mainHomeMenu.setImageBitmap(tempPic2);
					}
					if (menuStatus == 2) {
						Bitmap tempPic3 = BitmapFactory.decodeResource(
								getResources(), R.drawable.foot3_2);
						mainMeMenu.setImageBitmap(tempPic3);
					}
					menuStatus = 0;
					homeFrgHot.setVisibility(View.INVISIBLE);
					homeFrgNew.setVisibility(View.INVISIBLE);
				}
			}
		});

		mainPeepMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showOrHideBar(0);
				if (menuStatus != 0) {

					Bitmap tempPic1 = BitmapFactory.decodeResource(
							getResources(), R.drawable.foot1_1);
					mainPeepMenu.setImageBitmap(tempPic1);
					mPageVp.setCurrentItem(0);
					if (menuStatus == 1) {
						Bitmap tempPic2 = BitmapFactory.decodeResource(
								getResources(), R.drawable.foot2_2);
						mainHomeMenu.setImageBitmap(tempPic2);
					}
					if (menuStatus == 2) {
						Bitmap tempPic3 = BitmapFactory.decodeResource(
								getResources(), R.drawable.foot3_2);
						mainMeMenu.setImageBitmap(tempPic3);
					}
					menuStatus = 0;
					homeFrgHot.setVisibility(View.INVISIBLE);
					homeFrgNew.setVisibility(View.INVISIBLE);
				}
			}
		});

		// LinearmainMeMenu.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// // TODO Auto-generated method stub
		// showOrHideBar(2);
		// if (menuStatus != 2) {
		// Bitmap tempPic1 = BitmapFactory.decodeResource(
		// getResources(), R.drawable.foot3_1);
		// mainMeMenu.setImageBitmap(tempPic1);
		// mPageVp.setCurrentItem(2);
		// if (menuStatus == 1) {
		// Bitmap tempPic2 = BitmapFactory.decodeResource(
		// getResources(), R.drawable.foot2_2);
		// mainHomeMenu.setImageBitmap(tempPic2);
		// }
		// if (menuStatus == 0) {
		// Bitmap tempPic3 = BitmapFactory.decodeResource(
		// getResources(), R.drawable.foot1_2);
		// mainPeepMenu.setImageBitmap(tempPic3);
		// }
		// menuStatus = 2;
		// homeFrgHot.setVisibility(View.INVISIBLE);
		// homeFrgNew.setVisibility(View.INVISIBLE);
		// }
		// return false;
		// }
		// });

		LinearmainMeMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showOrHideBar(2);
				if (menuStatus != 2) {
					Bitmap tempPic1 = BitmapFactory.decodeResource(
							getResources(), R.drawable.foot3_1);
					mainMeMenu.setImageBitmap(tempPic1);
					mPageVp.setCurrentItem(2);
					if (menuStatus == 1) {
						Bitmap tempPic2 = BitmapFactory.decodeResource(
								getResources(), R.drawable.foot2_2);
						mainHomeMenu.setImageBitmap(tempPic2);
					}
					if (menuStatus == 0) {
						Bitmap tempPic3 = BitmapFactory.decodeResource(
								getResources(), R.drawable.foot1_2);
						mainPeepMenu.setImageBitmap(tempPic3);
					}
					menuStatus = 2;
					homeFrgHot.setVisibility(View.INVISIBLE);
					homeFrgNew.setVisibility(View.INVISIBLE);
				}
			}

		});

		mainMeMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showOrHideBar(2);
				if (menuStatus != 2) {
					Bitmap tempPic1 = BitmapFactory.decodeResource(
							getResources(), R.drawable.foot3_1);
					mainMeMenu.setImageBitmap(tempPic1);
					mPageVp.setCurrentItem(2);
					if (menuStatus == 1) {
						Bitmap tempPic2 = BitmapFactory.decodeResource(
								getResources(), R.drawable.foot2_2);
						mainHomeMenu.setImageBitmap(tempPic2);
					}
					if (menuStatus == 0) {
						Bitmap tempPic3 = BitmapFactory.decodeResource(
								getResources(), R.drawable.foot1_2);
						mainPeepMenu.setImageBitmap(tempPic3);
					}
					menuStatus = 2;
					homeFrgHot.setVisibility(View.INVISIBLE);
					homeFrgNew.setVisibility(View.INVISIBLE);
				}
			}

		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		// 后台异步执行初始化操作
		JPushInterface.init(getApplicationContext());
		JPushInterface.setLatestNotificationNumber(getApplicationContext(), 3);
		this.defineNotificationStyle();
		// 百度地图的初始化，。在应用启动的时候初始化
		SDKInitializer.initialize(getApplicationContext());
		checkIsSupportedByVersion();
		defineHWBadgeNum();
		// 实现滑动页面效果
		initView();
		defineHomeBt();
		initEvent();
		// 设置检查更新机制
		checkSoftwareUpdate();
	}
	private void initView(){
		findById();
		initPager();
		initToolbar();
		initBottomNavigation();
		initTabLineWidth(); // 初始化导航线
		initMenuItem();
	}
	private void checkSoftwareUpdate(){
		UmengUpdateAgent.setUpdateAutoPopup(false); // 不自动弹出窗口
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				// TODO Auto-generated method stub
				switch (updateStatus) {
					case UpdateStatus.Yes:
						// 有更新
						UmengUpdateAgent.showUpdateDialog(MainActivity.this, updateInfo);
						break;
					case UpdateStatus.No:
						// 没有更新
						daelUpdateNoResult();
						break;
					case UpdateStatus.NoneWifi: // none wifi
						if (mtellupdateresult) {
							Toast.makeText(MainActivity.this,
									"没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT)
									.show();
						}
						break;
					case UpdateStatus.Timeout: // time out
						Toast.makeText(MainActivity.this, "超时", Toast.LENGTH_SHORT)
								.show();
						break;

				}
			}

			private void daelUpdateNoResult() {
				// TODO Auto-generated method stub
				if (mtellupdateresult) {
					// Toast.makeText(MainActivity.this, "已经是最新版本",
					// Toast.LENGTH_SHORT).show();
					showToast("已经是最新版本");
					Message delayMsg = m_Handler
							.obtainMessage(AIRPLAY_MESSAGE_HIDE_TOAST);
					m_Handler.sendMessageDelayed(delayMsg, 500);
				}
			}
		});

		m_Handler = new MainHandler();
		// 每次启动时自动检查一遍更新
		UmengUpdateAgent.update(getApplicationContext());
	}
	private void initEvent(){
		mTabSubmitbtn.setOnClickListener(new OnClickListener() {

			@Override
			// 跳转到发表页
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				// intent.setClass(MainActivity.this, PublishActivity.class); //
				// 2015年9月27日：统一发表activity
				intent.setClass(MainActivity.this, PublishTopicActivity.class);
				intent.putExtra("PublishMode",
						PublishTopicActivity.PUBLISH_FOR_HOMETIP);
				MainActivity.this.startActivity(intent);
			}

		});

		clickArea.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				// intent.setClass(MainActivity.this, PublishActivity.class); //
				// 2015年9月27日：统一发表activity
				intent.setClass(MainActivity.this, PublishTopicActivity.class);
				intent.putExtra("PublishMode",
						PublishTopicActivity.PUBLISH_FOR_HOMETIP);
				MainActivity.this.startActivity(intent);
			}
		});
	}

	private void initToolbar(){
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
	}
	public Toolbar getToolbar(){
		return toolbar;
	}

	private void initBottomNavigation(){
		bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
		bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.foot1_2, "探索"))
				.addItem(new BottomNavigationItem(R.drawable.foot2_2, "主页"))
				.addItem(new BottomNavigationItem(R.drawable.foot3_2, "我")).initialise();
		bottomNavigationBar.selectTab(1);
		bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {

			@Override
			public void onTabSelected(int i) {
				mPageVp.setCurrentItem(i);
			}

			@Override
			public void onTabUnselected(int i) {

			}

			@Override
			public void onTabReselected(int i) {

			}
		});
	}

	/*
	 * 显示Toast
	 */
	public void showToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	// 终止显示Toast文本提示
	public void cancelToast() {
		if (mToast != null) {
			mToast.cancel();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if (mLocationManagerProxy != null) {
			mLocationManagerProxy.destroy();
		}
		super.onPause();
		JPushInterface.onPause(this);
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// 显示或影藏红点状态
		showOrHideStatus();
		initGaodeLocation(); // 初始化高德定位系统（每分钟定位一次，500米定位一次）
		SharedPreferences preferences = getSharedPreferences("user_Params",
				MODE_PRIVATE);
		UserConfigParams.device_id = preferences.getString("device_ID", "");
		// 桌面角标为0
		UserConfigParams.badgeNum = 0;
		super.onResume();
		MobclickAgent.onResume(this);
		JPushInterface.onResume(this);
	}

	/*
	 * 初始化高德地图的相关操作
	 */
	private void initGaodeLocation() {
		// TODO Auto-generated method stub
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);

		// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
		// 注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
		// 在定位结束后，在合适的生命周期调用destroy()方法
		// 其中如果间隔时间为-1，则定位只定一次
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, 5000, 20, this);

		// mLocationManagerProxy.setGpsEnable(false);
	}

	private void initTabLineWidth() {
		// TODO Auto-generated method stub
		DisplayMetrics dpMetrics = new DisplayMetrics();
		getWindow().getWindowManager().getDefaultDisplay()
				.getMetrics(dpMetrics); // 获得屏幕尺寸
		screenWidth = dpMetrics.widthPixels;

		// 水平滑条注释掉
		// LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
		// .getLayoutParams(); // 导航线
		// lp.width = screenWidth / 3;
		// mTabLineIv.setLayoutParams(lp);
	}



	private void findById() {
		// TODO Auto-generated method stub
		// mTabLineIv = (ImageView) this.findViewById(R.id.id_tab_line_iv);
		mPageVp = (ViewPager) this.findViewById(R.id.id_page_vp);
		// 左上角icon单击按钮
		// mHomePublishBtn = (ImageButton)this.findViewById(R.id.submit_new);
		// Topic
		// mTabTopicHomeTv = (TextView) this.findViewById(R.id.TabTopTitle);
		// 发表按钮
		mTabSubmitbtn = (ImageButton) this.findViewById(R.id.submit_new);

		mLayout = (LinearLayout) this.findViewById(R.id.maintabtoplayout);
		mainHomeMenu = (ImageButton) this.findViewById(R.id.main_home_menu);
		mainPeepMenu = (ImageButton) this.findViewById(R.id.main_peep_menu);
		mainMeMenu = (ImageButton) this.findViewById(R.id.main_me_menu);

		peepStatusView = (ImageView) this.findViewById(R.id.peep_status);
		meStatusView = (ImageView) this.findViewById(R.id.me_status);
		//
		LinearmainHomeMenu = (LinearLayout) this
				.findViewById(R.id.main_home_menu_t);
		LinearmainPeepMenu = (LinearLayout) this
				.findViewById(R.id.main_peep_menu_t);
		LinearmainMeMenu = (LinearLayout) this
				.findViewById(R.id.main_me_menu_t);

		clickArea = (LinearLayout) this.findViewById(R.id.clickArea);
		homeFrgNew = (Button) this.findViewById(R.id.home_frg_new);
		homeFrgHot = (Button) this.findViewById(R.id.home_frg_hot);
		mainTitleshow = (TextView) this.findViewById(R.id.biu_main_title);
		homeFrgChange = (LinearLayout) this.findViewById(R.id.home_frg_change);
	}

	private void initPager() {
		// TODO Auto-generated method stub
		// 创建两个Fragment对象
		if (mfirstStartInit) {
			mHomeFg = new HomeFragment();
			mMeFg = MeFragment.getInstance(meStatusView);
			// mMoonboxFg = new MoonBoxFragment();
			mPeepFg = PeepFragment.getInstance(peepStatusView);
			// 创建热帖碎片
			hotHomeFragment = new HotHomeFragment();
			// 将创建的两个Fragment对象添加到List
			mhomeFragmentList.add(mPeepFg);
			mhomeFragmentList.add(mHomeFg);
			// mhomeFragmentList.add(mMoonboxFg);
			mhomeFragmentList.add(mMeFg);

			// mhomeFragmentList.add(hotHomeFragment);

			// 创建适配器
			mFragmentAdapter = new HomeFragmentAdapter(
					this.getSupportFragmentManager(), mhomeFragmentList);
			mPageVp.setAdapter(mFragmentAdapter);
			mPageVp.setCurrentItem(1, false);
			mPageVp.setOffscreenPageLimit(3);
			mPageVp.setOnPageChangeListener(new OnPageChangeListener() {
				/**
				 * @param state
				 *            :滑动中的状态。0：什么都没做；1：正在滑动；2：滑动完毕
				 */
				@Override
				public void onPageScrollStateChanged(int state) {

				}

				/**
				 * 
				 * @param position
				 *            ：要滑动的目标页面
				 * @param offset
				 *            ：当前页面【偏移的百分比
				 * @param pffsetPixels
				 *            ：当前页面偏移的像素位置
				 * @remark 滑动操作是ViewPaper本身提供的，这里的操作主要是修改导航条，使其对应的修改。
				 */
				@Override
				public void onPageScrolled(int position, float offset,
						int pffsetPixels) {

				}

				/*
				 * 页面滑动结束后，会触发此消息
				 * 
				 * @see android.support.v4.view.ViewPager.OnPageChangeListener#
				 * onPageSelected(int)
				 */
				@Override
				public void onPageSelected(int position) {
					// TODO Auto-generated method stub
					/*switch (position) {
					case 1:
						// 下面的菜单条也要跟着
						showOrHideBar(1);
						if (menuStatus != 1) {
							Bitmap tempPic1 = BitmapFactory.decodeResource(
									getResources(), R.drawable.foot2_1);
							mainHomeMenu.setImageBitmap(tempPic1);
							mPageVp.setCurrentItem(1);
							if (menuStatus == 0) {
								Bitmap tempPic2 = BitmapFactory.decodeResource(
										getResources(), R.drawable.foot1_2);
								mainPeepMenu.setImageBitmap(tempPic2);
							}
							if (menuStatus == 2) {

								Bitmap tempPic3 = BitmapFactory.decodeResource(
										getResources(), R.drawable.foot3_2);
								mainMeMenu.setImageBitmap(tempPic3);
							}
							menuStatus = 1;
							homeFrgHot.setVisibility(View.VISIBLE);
							homeFrgNew.setVisibility(View.VISIBLE);
						}

						// mTabTopicHomeTv.setText(R.string.title_home);
						mTabSubmitbtn.setVisibility(View.VISIBLE);
						mLayout.setBackgroundColor(getResources().getColor(
								android.R.color.transparent));
						// mIsMoonBoxPub = false;
						// 设置首页imagebutton按钮的显示图片
						Bitmap tempBitmap1 = BitmapFactory.decodeResource(
								getResources(), R.drawable.release_cion);
						mTabSubmitbtn.setImageBitmap(tempBitmap1);
						mLayout.setBackgroundColor(getResources().getColor(
								R.color.umeng_fb_gray));
						// 右上角发表新贴按钮
						mTabSubmitbtn.setOnClickListener(new OnClickListener() {

							@Override
							// 跳转到发表页
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent();
								// intent.setClass(MainActivity.this,
								// PublishActivity.class); //
								// 2015年9月27日：统一发表activity
								intent.setClass(MainActivity.this,
										PublishTopicActivity.class);
								intent.putExtra(
										"PublishMode",
										PublishTopicActivity.PUBLISH_FOR_HOMETIP);
								MainActivity.this.startActivity(intent);
							}

						});

						clickArea.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent();
								intent.setClass(MainActivity.this,
										PublishTopicActivity.class);
								intent.putExtra(
										"PublishMode",
										PublishTopicActivity.PUBLISH_FOR_HOMETIP);
								MainActivity.this.startActivity(intent);
							}
						});
						break;
					case 0:
						// 改变菜单按钮
						showOrHideBar(0);
						if (menuStatus != 0) {

							Bitmap tempPic1 = BitmapFactory.decodeResource(
									getResources(), R.drawable.foot1_1);
							mainPeepMenu.setImageBitmap(tempPic1);
							mPageVp.setCurrentItem(0);
							if (menuStatus == 1) {
								Bitmap tempPic2 = BitmapFactory.decodeResource(
										getResources(), R.drawable.foot2_2);
								mainHomeMenu.setImageBitmap(tempPic2);
							}
							if (menuStatus == 2) {
								Bitmap tempPic3 = BitmapFactory.decodeResource(
										getResources(), R.drawable.foot3_2);
								mainMeMenu.setImageBitmap(tempPic3);
							}
							menuStatus = 0;
							homeFrgHot.setVisibility(View.INVISIBLE);
							homeFrgNew.setVisibility(View.INVISIBLE);
						}
						// 此处显示的是话题碎片页面，添加地图后将显示ImageButton控件，并重定义该控件
						// mTabTopicHomeTv.setText(R.string.title_peep);
						// 此处imageButton重定义，改为跳转到地图页面
						mTabSubmitbtn.setVisibility(View.VISIBLE);
						// mTabSubmitbtn.setBackgroundResource(R.drawable.search);
						Bitmap tempBitmap2 = BitmapFactory.decodeResource(
								getResources(), R.drawable.search);
						mTabSubmitbtn.setImageBitmap(tempBitmap2);
						mLayout.setBackgroundColor(getResources().getColor(
								R.color.umeng_fb_gray));

						// 右上角的图片按钮变为转到地图页面
						mTabSubmitbtn.setOnClickListener(new OnClickListener() {

							@Override
							// 跳转到发表页
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent();
								// intent.setClass(MainActivity.this,
								// PublishActivity.class); //
								// 2015年9月27日：统一发表activity
								intent.setClass(MainActivity.this,
										BiumapActivity.class);
								MainActivity.this.startActivity(intent);
							}

						});

						clickArea.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent();
								// intent.setClass(MainActivity.this,
								// PublishActivity.class); //
								// 2015年9月27日：统一发表activity
								intent.setClass(MainActivity.this,
										BiumapActivity.class);
								MainActivity.this.startActivity(intent);
							}
						});
						break;
					// mIsMoonBoxPub = true;
					// MoonBoxFragment monfg =
					// (MoonBoxFragment)mhomeFragmentList.get(1);
					// monfg.setis
					case 2:
						showOrHideBar(2);
						// 改变菜单按钮
						if (menuStatus != 2) {
							Bitmap tempPic1 = BitmapFactory.decodeResource(
									getResources(), R.drawable.foot3_1);
							mainMeMenu.setImageBitmap(tempPic1);
							mPageVp.setCurrentItem(2);
							if (menuStatus == 0) {
								Bitmap tempPic2 = BitmapFactory.decodeResource(
										getResources(), R.drawable.foot1_2);
								mainPeepMenu.setImageBitmap(tempPic2);
							}
							if (menuStatus == 1) {
								Bitmap tempPic3 = BitmapFactory.decodeResource(
										getResources(), R.drawable.foot2_2);
								mainHomeMenu.setImageBitmap(tempPic3);
							}
							menuStatus = 2;
							homeFrgHot.setVisibility(View.INVISIBLE);
							homeFrgNew.setVisibility(View.INVISIBLE);
						}
						// mTabTopicHomeTv.setText(R.string.title_me);
						mTabSubmitbtn.setVisibility(View.GONE);
						mtellupdateresult = true;
						mLayout.setBackgroundColor(getResources().getColor(
								android.R.color.transparent));
						// mIsMoonBoxPub = false;
						// mTabContactsTv.setTextColor(Color.BLUE);
						break;
					}*/
					currentIndex = position;
					bottomNavigationBar.selectTab(position);
				}
			});
			mfirstStartInit = false;
		} else {
			// 不是第一次则只更新经纬度
			// mHomeFg.SetLatandLng(mainLat.toString(), mainLng.toString());
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		// TODO Auto-generated method stub
		if (amapLocation != null
				&& amapLocation.getAMapException().getErrorCode() == 0) {
			// 获取位置信息
			mainLat = amapLocation.getLatitude();

			mainLng = amapLocation.getLongitude();
			UserConfigParams.latitude = mainLat.toString();
			UserConfigParams.longitude = mainLng.toString();
			UserConfigParams.setLocationGetted(true);
			// 将用户当前的位置信息反馈给服务器端
			String postUrl = "http://api.bbbiu.com:1234/userpoint";
			PostTempPosition pstTempPostiton = new PostTempPosition(m_Handler,
					mainLat, mainLng, postUrl);
			Thread pstTempPosThread = new Thread(pstTempPostiton);
			pstTempPosThread.start();

		} else {
			// Toast.makeText(this, "无法定位当前所在位置，请检查网络连接！",
			// Toast.LENGTH_LONG).show();
			Toast.makeText(this,
					amapLocation.getAMapException().getErrorMessage(),
					Toast.LENGTH_SHORT).show();
			mLocationManagerProxy.destroy();
			// Log.e("定位错误", amapLocation.getAMapException().getErrorMessage());
		}
	}

	/**
	 * 自定义Handler，处理Mainactivity的事件
	 * 
	 * @author grf
	 * 
	 */
	class MainHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				break;
			case AIRPLAY_MESSAGE_HIDE_TOAST: {
				cancelToast();
				break;
			}
			}
			super.handleMessage(msg);
		}

	}

	/**
	 * 重载按键按下效果，屏蔽返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private int homeBtStatus = 0;

	/**
	 * 
	 */
	private void defineHomeBt() {
		homeFrgNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (homeBtStatus != 0) {
					// 点击显示新帖子
					// if (!showOrNotSubmit) {
					// }
					mFragmentAdapter.changeIndex();
					mTabSubmitbtn.setVisibility(View.VISIBLE);

					homeFrgNew.setBackgroundResource(R.drawable.switching2);
					// 字体为biu蓝
					homeFrgNew.setTextColor(getResources().getColor(
							R.color.biu_main_color));
					homeFrgHot.setBackgroundResource(R.drawable.switching4);
					homeFrgHot.setTextColor(getResources().getColor(
							R.color.biu_font_white));
					homeBtStatus = 0;
					// 将首页改变为Home页面
					mhomeFragmentList.set(1, mHomeFg);
//					mFragmentAdapter = new HomeFragmentAdapter(
//							getSupportFragmentManager(), mhomeFragmentList);
					mFragmentAdapter.notifyDataSetChanged();
					mPageVp.setCurrentItem(1, false);
				}

			}
		});

		homeFrgHot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mTabSubmitbtn.setVisibility(View.INVISIBLE);
				if (homeBtStatus != 1) {
					mFragmentAdapter.changeIndex();
					Log.i("Hot", "changed..................");
					homeFrgNew.setBackgroundResource(R.drawable.switching1);
					homeFrgHot.setTextColor(getResources().getColor(
							R.color.biu_main_color));
					homeFrgHot.setBackgroundResource(R.drawable.switching3);
					homeFrgNew.setTextColor(getResources().getColor(
							R.color.biu_font_white));
					homeBtStatus = 1;
					mhomeFragmentList.set(1, hotHomeFragment);
//					mFragmentAdapter = new HomeFragmentAdapter(
//							getSupportFragmentManager(), mhomeFragmentList);
					mFragmentAdapter.notifyDataSetChanged();
					mPageVp.setCurrentItem(1, false);

				}
			}
		});

	}

	// 华为手机桌面角标设置
	private void defineHWBadgeNum() {
		if (!isSupportedBade) {
			Log.i("badgedemo", "not supported badge!");
			return;
		}
		try {
			Bundle bunlde = new Bundle();
			bunlde.putString("package", "grf.biu");
			bunlde.putString("class", "com.biu.biu.main.MainActivity");
			// bunlde.putInt("badgenumber", UserConfigParams.badgeNum);
			bunlde.putInt("badgenumber", 50);
			ContentResolver t = this.getContentResolver();
			Bundle result = t
					.call(Uri
							.parse("content://com.huawei.android.launcher.settings/badge/"),
							"change_launcher_badge", "", bunlde);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	boolean isSupportedBade = false;

	public void checkIsSupportedByVersion() {
		try {
			PackageManager manager = getPackageManager();
			PackageInfo info = manager.getPackageInfo(
					"com.huawei.android.launcher", 0);
			if (info.versionCode >= 63029) {
				isSupportedBade = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
