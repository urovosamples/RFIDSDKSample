package com.rfid.demo;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;

import com.ubx.usdk.RFIDSDKManager;

public class MainActivity extends TabActivity   {
	
	public static TabHost myTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		myTabHost = getTabHost();
		Intent intent0 = new Intent(this, ScanActivity.class);
		Intent intent1 = new Intent(this,ReadWriteActivity.class);
		Intent intent2 = new Intent(this, SettingActivity.class);
		Intent intent3 = new Intent(this,MaskActivity.class);
		Intent intent4 = new Intent(this, FindingActivity.class);


		TabHost.TabSpec tabSpec0 = myTabHost.newTabSpec(getString(R.string.tab_scan)).setIndicator(getString(R.string.tab_scan)).setContent(intent0);
		TabHost.TabSpec tabSpec1 = myTabHost.newTabSpec(getString(R.string.tab_rw)).setIndicator(getString(R.string.tab_rw)).setContent(intent1);
		TabHost.TabSpec tabSpec2 = myTabHost.newTabSpec(getString(R.string.tab_param)).setIndicator(getString(R.string.tab_param)).setContent(intent2);
		TabHost.TabSpec tabSpec3 = myTabHost.newTabSpec(getString(R.string.tab_mask)).setIndicator(getString(R.string.tab_mask)).setContent(intent3);
		TabHost.TabSpec tabSpec4 = myTabHost.newTabSpec(getString(R.string.finding)).setIndicator(getString(R.string.finding)).setContent(intent4);

		myTabHost.addTab(tabSpec0);
		myTabHost.addTab(tabSpec4);
		myTabHost.addTab(tabSpec1);
		myTabHost.addTab(tabSpec3);
		myTabHost.addTab(tabSpec2);

		myTabHost.setCurrentTab(0);

		myTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String s) {
//				changeTabBottomBg();
			}
		});

//		changeTabBottomBg();



		setStatusBarColor(this,getResources().getColor(R.color.white_title_status));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			Window window = getWindow();
			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		}
    }
	public int xorString(String input) {
		int xorValue = 0;
		for (int i = 0; i < input.length(); i++) {
			xorValue ^= input.charAt(i);
		}
		return xorValue;
	}

	private void changeTabBottomBg(){
		int index = getTabHost().getCurrentTab();
		//调用tabhost中的getTabWidget()方法得到TabWidget
		TabWidget tabWidget = getTabHost().getTabWidget();
		for (int i = 0; i < tabWidget.getChildCount(); i++) {
			TextView tView = (TextView) tabWidget.getChildAt(i).findViewById(
					android.R.id.title);
			//修改背景
			tabWidget.getChildAt(i).setBackgroundResource( R.drawable.custom_tab_indicator);
//			tView.setTextSize(16);
			//字体
//			tView.setTypeface(Typeface.DEFAULT);
			if (myTabHost.getCurrentTab() == i) {
				tView.setTextColor(getResources().getColorStateList(R.color.white));
			} else {
				tView.setTextColor(getResources().getColorStateList(android.R.color.black));
			}
		}
	}


	//点击某一个选项卡切换该选项卡背景的方法
	private void changeTabBackGround(){
		//得到当前选中选项卡的索引
		int index = getTabHost().getCurrentTab();
		//调用tabhost中的getTabWidget()方法得到TabWidget
		TabWidget tabWidget = getTabHost().getTabWidget();
		//得到选项卡的数量
		int count = tabWidget.getChildCount();
		//循环判断，只有点中的索引值改变背景颜色，其他的则恢复未选中的颜色
		for(int i=0;i<count;i++){
			View view = tabWidget.getChildAt(i);
			if(index == i){
				view.setBackgroundResource(R.color.main_color);
			}else{
				view.setBackgroundResource(R.color.transparent_background_tab);
			}
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	@Override
	protected void onResume() {
		super.onResume();
			//关闭手柄按键处触发扫描头出光，长距设备，开启手柄按钮控制扫描头出光（Long distance device, open the handle button to control the scanning head light）
			RFIDSDKManager.getInstance().enableScanHead(false);
	}



	@Override
	protected void onPause() {
		super.onPause();
			//开启手柄按键处触发扫描头出光，长距设备，开启手柄按钮控制扫描头出光（Long distance device, open the handle button to control the scanning head light）
			RFIDSDKManager.getInstance().enableScanHead(true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}
	/**
	 * Return to exit program twice
	 * 两次返回退出程序
	 */
	private long firstTime = 0;
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		long secondTime = System.currentTimeMillis();
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK &&  event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0){
			if (secondTime - firstTime < 2000) {
				RFIDSDKManager.getInstance().release();
				finish();
//				System.exit(0);
			} else {
				Toast.makeText(this, getString(R.string.press_again_exit_app), Toast.LENGTH_SHORT).show();
				firstTime = System.currentTimeMillis();
			}
			return true;
		}

		return super.dispatchKeyEvent(event);
	}


	/**
	 * 设置状态栏颜色
	 * @param activity
	 * @param color
	 */
	private void setStatusBarColor(Activity activity, int color){
		Window window=activity.getWindow();
		//取消状态栏透明
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		//添加Flag把状态栏设为可绘制模式
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		//设置状态栏颜色
		window.setStatusBarColor(color);
		//设置系统状态栏处于可见状态
		window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
		//让view不根据系统窗口来调整自己的布局
		ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
		View mChildView = mContentView.getChildAt(0);
		if (mChildView != null) {
			ViewCompat.setFitsSystemWindows(mChildView, false);
			ViewCompat.requestApplyInsets(mChildView);
		}
	}
}
