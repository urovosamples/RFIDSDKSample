package com.rfid.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.ubx.usdk.RFIDSDKManager;
import com.ubx.usdk.listener.InitListener;

public class SplashActivity extends AppCompatActivity {

    private static String TAG = SplashActivity.class.getSimpleName();
    private TextView tvShow;
    private ProgressBar progress;
    private Button BtConnect  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        tvShow = (TextView) findViewById(R.id.textview_show);
        progress = findViewById(R.id.progress);
        BtConnect = findViewById(R.id.BtConnect);
        BtConnect.setVisibility(View.GONE);
        BtConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initRFID();
            }
        });


        setStatusBarColor(this, getResources().getColor(R.color.white_title_status));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initRFID();
    }


    private void initRFID() {
        progress.setVisibility(View.VISIBLE);
        tvShow.setVisibility(View.INVISIBLE);



        initRfid();

        if (RFIDSDKManager.getInstance().getRfidManager() != null && RFIDSDKManager.getInstance().getRfidManager().isConnected()) {
            toMain();
        }

    }


    public void initRfid() {
        RFIDSDKManager.getInstance().init(SplashActivity.this.getApplicationContext(), new InitListener() {
            @Override
            public void onStatus(boolean status) {
                Log.i(TAG, "initRfid()  status : " + status);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        if (status) {
                            toMain();
                        } else {
                            tvShow.setVisibility(View.VISIBLE);
                            tvShow.setText(getString(R.string.openport_failed));
                            tvShow.setTextColor(getResources().getColor(R.color.red));
                            progress.setVisibility(View.INVISIBLE);
                            BtConnect.setVisibility(View.VISIBLE);
                        }


                        Toast.makeText(SplashActivity.this," " + (status ? getResources().getString(R.string.rfid_successful_initialization) : getResources().getString(R.string.rfid_initialization_failure)),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * Return to exit program twice
     * 两次返回退出程序
     */
    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long secondTime = System.currentTimeMillis();
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (secondTime - firstTime < 2000) {
                System.exit(0);
            } else {
                Toast.makeText(this, getString(R.string.press_again_exit_app), Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void toMain() {
        Intent intent2 = new Intent().setClass(SplashActivity.this, MainActivity.class);
        startActivity(intent2);
        finish();
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity
     * @param color
     */
    private void setStatusBarColor(Activity activity, int color) {
        Window window = activity.getWindow();
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
