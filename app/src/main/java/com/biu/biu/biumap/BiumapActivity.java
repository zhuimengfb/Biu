package com.biu.biu.biumap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMapTouchListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionResult.SuggestionInfo;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.biu.biu.R;
import com.biu.biu.userconfig.UserConfigParams;
import com.umeng.socialize.utils.Log;

import java.util.ArrayList;
import java.util.List;

public class BiumapActivity extends Activity {
	// 添加新的块状响应区域
	private LinearLayout goToPoiArea;

	private MapView biuMapView;
	// 中心位置// 声明一个中心位置的变量
	private LatLng mapCenter = new LatLng(0, 0);
	// 地图控制器
	private BaiduMap biuBaiduMap;
	// 显示中心位置的地标叠加层
	private OverlayOptions optionsCenter = null;
	// 地图中心的地标
	private Marker centerMarker = null;;
	private LinearLayout biuTopBar;
	private EditText putIn;
	private ListView showResult;
	private ImageButton mapToPeek;
	private Button backToNormal;
	// 定义结果显示的适配器
	private ShowResultAdapter showResultAdapter;
	// 定义动态查询的结果集
	private List<SuggestionInfo> showResultList = null;
	// 定义建议查询实例
	private SuggestionSearch biuSuggestionSearch;
	// 建议查询的监听
	private OnGetSuggestionResultListener biuListener;
	// 跳转按钮
	private ImageButton goToPoi;
	// 定义中心位置的地点名称
	private String placeName = "";
	private RelativeLayout putInPart;
	// 顶一个布尔型的变量，点击地图操作时确保是normao状态
	private boolean normalOrNot = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_biumap);
		putInPart = (RelativeLayout) this.findViewById(R.id.putinpart);
		biuMapView = (MapView) this.findViewById(R.id.biumapview);
		biuBaiduMap = biuMapView.getMap();
		LatLng clientCenter = new LatLng(
				Double.parseDouble(UserConfigParams.latitude),
				Double.parseDouble(UserConfigParams.longitude));
		MapStatus biuMapStatus = new MapStatus.Builder().target(clientCenter)
				.zoom(16).build();
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(biuMapStatus);
		biuBaiduMap.setMapStatus(mapStatusUpdate);
		// 获取地图显示页面的顶部top
		biuTopBar = (LinearLayout) this.findViewById(R.id.maptop);
		putIn = (EditText) this.findViewById(R.id.putIn);
		showResult = (ListView) this.findViewById(R.id.showResult);
		mapToPeek = (ImageButton) this.findViewById(R.id.mapToPeek);
		backToNormal = (Button) this.findViewById(R.id.backtonormal);
		goToPoi = (ImageButton) this.findViewById(R.id.gotopoi);
		goToPoiArea = (LinearLayout) this.findViewById(R.id.go_to_poi_area);
		mapToPeek.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				BiumapActivity.this.finish();
			}
		});
		biuBaiduMap.setOnMapTouchListener(new OnMapTouchListener() {

			@Override
			public void onTouch(MotionEvent arg0) {
				// TODO Auto-generated method stub
				if (!normalOrNot) {
					biuMapView.requestFocus();
					biuTopBar.setVisibility(LinearLayout.VISIBLE);
					backToNormal.setVisibility(View.GONE);
					showResult.setVisibility(View.GONE);
					toNormal();
					// 关闭输入法
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(
							BiumapActivity.this.getCurrentFocus()
									.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					normalOrNot = true;
				}
			}
		});
		biuBaiduMap
				.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {

					@Override
					public void onMapStatusChangeStart(MapStatus status) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onMapStatusChangeFinish(MapStatus status) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onMapStatusChange(MapStatus status) {
						// TODO Auto-generated method stub
						// 随着状态的变化，兴趣位置的地标也随着发生变化
						mapCenter = status.target;
						centerMarker.setPosition(mapCenter);
						// 重定义placename
						// 地图状态变化的时候确保是normal状态
						// normalOrNot = true;
						// if (!normalOrNot) {
						// biuMapView.requestFocus();
						// biuTopBar.setVisibility(LinearLayout.VISIBLE);
						// backToNormal.setVisibility(View.GONE);
						// showResult.setVisibility(View.GONE);
						// toNormal();
						// // 关闭输入法
						// InputMethodManager inputMethodManager =
						// (InputMethodManager)
						// getSystemService(Context.INPUT_METHOD_SERVICE);
						// inputMethodManager.hideSoftInputFromWindow(
						// BiumapActivity.this.getCurrentFocus()
						// .getWindowToken(),
						// InputMethodManager.HIDE_NOT_ALWAYS);
						// normalOrNot = true;
						// }

					}
				});
		biuBaiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {

			@Override
			public void onMapLoaded() {
				// TODO Auto-generated method stub
				mapCenter = biuBaiduMap.getMapStatus().target;
				BitmapDescriptor icon = BitmapDescriptorFactory
						.fromResource(R.drawable.center_tag);
				optionsCenter = new MarkerOptions().position(mapCenter).icon(
						icon);
				centerMarker = (Marker) biuBaiduMap.addOverlay(optionsCenter);
			}
		});
		initControllers();
		// 初始化putIn文本框
		initPutIn();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		biuMapView.onDestroy();
		biuSuggestionSearch.destroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		normalOrNot = true;
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		biuMapView.onResume();
		biuMapView.setFocusable(true);
		biuMapView.setFocusableInTouchMode(true);
		biuMapView.requestFocus();
		biuMapView.requestFocusFromTouch();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		biuMapView.onPause();
	}

	// }
	// 控制地图页面控件的显示和隐藏
	private void initControllers() {
		// 新点击响应区域
		goToPoiArea.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(BiumapActivity.this,
						PoiActivity.class);
				intent.putExtra("centerLat", mapCenter.latitude);
				intent.putExtra("centerLng", mapCenter.longitude);
				if (placeName != null && placeName != "") {
					intent.putExtra("placeName", placeName);
				} else {
					intent.putExtra("placeName", "unknown");
				}
				intent.putExtra("savedornot", false);
				// 跳转到兴趣点位置的2500米范围
				startActivity(intent);
			}
		});

		// 像goToPoi控件添加事件监听
		goToPoi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(BiumapActivity.this,
						PoiActivity.class);
				intent.putExtra("centerLat", mapCenter.latitude);
				intent.putExtra("centerLng", mapCenter.longitude);
				if (placeName != null && placeName != "") {
					intent.putExtra("placeName", placeName);
				} else {
					intent.putExtra("placeName", "unknown");
				}
				intent.putExtra("savedornot", false);
				// 跳转到兴趣点位置的2500米范围
				startActivity(intent);
			}
		});

		putIn.setFocusable(true);
		putIn.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					// 标识状态的布尔型变量为false
					normalOrNot = false;
					// 获得焦点的时候将顶部topBar隐藏起來
					biuTopBar.setVisibility(LinearLayout.GONE);

					showResult.setVisibility(View.VISIBLE);
					putInPart.setBackgroundColor(0xff26D5B6);
					LayoutParams rlp = new LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					rlp.setMargins(15, 15, 150, 15);
					// rlp.rightMargin = 180;
					// rlp.leftMargin = 15;
					// rlp.topMargin = 15;
					// rlp.bottomMargin = 15;
					rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
							RelativeLayout.TRUE);
					rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
							RelativeLayout.TRUE);
					putIn.setLayoutParams(rlp);
					backToNormal.setVisibility(View.VISIBLE);

				} else {
					// 失去焦点的时候恢复原来的状态
					biuTopBar.setVisibility(LinearLayout.VISIBLE);
					showResult.setVisibility(View.GONE);
				}

			}
		});
		// 点击地图窗口焦点设置在地图窗口
		biuMapView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				biuMapView.requestFocus();
			}
		});

		// 返回地图页面的正式情况即不是地图的最大化，并显示顶部的bar
		backToNormal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 地图窗口获得焦点
				biuMapView.requestFocus();
				biuTopBar.setVisibility(LinearLayout.VISIBLE);
				backToNormal.setVisibility(View.GONE);
				showResult.setVisibility(View.GONE);
				toNormal();
				// 关闭输入法
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(BiumapActivity.this
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);

			}
		});

	}

	// 定义和初始化EditText控件putIn
	private void initPutIn() {
		// 定义查询时候的适配器
		showResultList = new ArrayList<SuggestionInfo>();
		Log.i("BIUMAPADAPTER", "定义地图查询的适配器");
		// 初始化动态显示查询结果的适配器
		showResultAdapter = new ShowResultAdapter(this,
				R.layout.show_result_item, showResultList);
		// 将适配器赋值给ListView
		showResult.setAdapter(showResultAdapter);
		// 定义建议查询
		biuSuggestionSearch = SuggestionSearch.newInstance();
		// 实例化一个建议查询的监听
		biuListener = new OnGetSuggestionResultListener() {
			public void onGetSuggestionResult(SuggestionResult res) {
				if (res == null || res.getAllSuggestions() == null) {
					return;
					// 未找到相关结果
				}
				// 获取在线建议检索结果(处理和显示建议查询的结果)返回SuggestionInfo数组对象集
				List<SuggestionInfo> onceResult = res.getAllSuggestions();
				showResultList.clear();
				showResultList.addAll(onceResult);
				// 结果集发生变化则通知适配器改变ListView
				if (showResultList != null && showResultList.size() > 0) {
					for (SuggestionInfo a : showResultList) {
						System.out.println(a.key);
					}
					showResultAdapter.notifyDataSetChanged();
					Log.i("SUGGESTION_RESULT_NUM",
							Integer.toString(showResultList.size()));
				}

			}
		};
		// 添加建议查询的监听
		biuSuggestionSearch.setOnGetSuggestionResultListener(biuListener);
		putIn.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				Log.i("EDITTEXT——change", "onTextChanged");
				Log.i("EDITTEXT——change----s1", s.toString());
				// 定义每次变化时的动态查询的
				String content = s.toString();// 查询的内容
				// 发起建议查询的请求
				biuSuggestionSearch
						.requestSuggestion(new SuggestionSearchOption()
								.keyword(content).city(""));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				Log.i("EDITTEXT——change", "beforeTextChanged");
				Log.i("EDITTEXT——change----s2", s.toString());
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				Log.i("EDITTEXT——change", "afterTextChanged");
			}
		});
	}

	// 定义动态显示查询的结果
	private class ShowResultAdapter extends ArrayAdapter<SuggestionInfo> {
		private int resourceId;

		public ShowResultAdapter(Context context, int resourceId,
				List<SuggestionInfo> objects) {
			super(context, resourceId, objects);
			// TODO Auto-generated constructor stub
			this.resourceId = resourceId;
			android.util.Log.i("ADAPTER", "适配器的构造函数");
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final SuggestionInfo suggestionInfo = getItem(position);
			View view;
			ViewHolder viewHolder;
			if (convertView == null) {
				view = LayoutInflater.from(getContext()).inflate(resourceId,
						null);
				viewHolder = new ViewHolder();
				viewHolder.descriptionView = (TextView) view
						.findViewById(R.id.eachdescription);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}
			// 定义各个空间的属性或者赋值
			viewHolder.descriptionView.setText(suggestionInfo.key);
			// 添加view 的单击
			// 暂时省略
			Log.i("RESULT", "测试动态查询的结果显示");
			// 对每一个view添加一个click事件
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// 对应的经纬度坐标
					LatLng point = suggestionInfo.pt;
					// 根据坐标位置跳转到，(此处的活动暂且为空)
					// Intent intent = new Intent(BiumapActivity.this, null);
					// intent.putExtra("lat", point.latitude);
					// intent.putExtra("lng", point.latitude);
					// startActivity(intent);
					if (point != null) {
						android.util.Log.i("动态查询的结果响应点击事件", point.latitude
								+ "   " + point.longitude);
						// 重置页面控件的状态
						resetController(suggestionInfo.key, point);
					} else {
						Toast.makeText(BiumapActivity.this, "该项位置数据已丢失",
								Toast.LENGTH_SHORT);
					}

					// 让输入法消失
					// 关闭输入法
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(
							BiumapActivity.this.getCurrentFocus()
									.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			});
			return view;
		}

		class ViewHolder {
			TextView descriptionView;
		}
	}

	// 定义showResult列表中的每一个数据项类
	private void resetController(String placeName, LatLng point) {
		// 背景恢复为灰色
		this.toNormal();
		putIn.setText(placeName);
		showResultList.clear();
		showResultAdapter.notifyDataSetChanged();
		biuMapView.requestFocus();
		biuTopBar.setVisibility(LinearLayout.VISIBLE);
		backToNormal.setVisibility(View.GONE);
		// 点击一个结果项将试图的中心移动该处
		if (point != null) {
			mapCenter = new LatLng(point.latitude, point.longitude);
		} else {
			Toast.makeText(this, "该项位置数据已丢失", Toast.LENGTH_SHORT);
		}

		MapStatus biuMapStatus = new MapStatus.Builder().target(mapCenter)
				.zoom(16).build();
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(biuMapStatus);
		biuBaiduMap.setMapStatus(mapStatusUpdate);
		this.placeName = placeName;
	}

	private void toNormal() {
		putInPart.setBackgroundColor(getResources().getColor(
				R.color.umeng_fb_gray));
		LayoutParams rlp = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rlp.rightMargin = 10;
		rlp.leftMargin = 10;
		rlp.topMargin = 10;
		rlp.bottomMargin = 10;
		rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		putIn.setLayoutParams(rlp);

	}

}
