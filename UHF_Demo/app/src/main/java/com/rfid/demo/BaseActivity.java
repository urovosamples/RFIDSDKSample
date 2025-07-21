package com.rfid.demo;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;


public class BaseActivity extends AppCompatActivity {

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
//			getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
//					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


			setStatusBarColor(this,getResources().getColor(R.color.white_title_status));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				Window window = getWindow();
				window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
			}
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
