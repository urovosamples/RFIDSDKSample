package com.rfid.demo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.rfid.demo.utils.CrashHandler;
import com.rfid.demo.utils.ToastUtils;
import com.ubx.usdk.util.UBXLog;


public class BaseApplication extends Application {
    private static Context mContext;
    private String TAG = BaseApplication.class.getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate() ");
        mContext = this;
        ToastUtils.init(mContext);
        UBXLog.initContext(mContext);

        //错误日志
        CrashHandler.getInstance().init(this);

    }



    public static Context getContext() {
        return mContext;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "onTerminate()");

    }

}
