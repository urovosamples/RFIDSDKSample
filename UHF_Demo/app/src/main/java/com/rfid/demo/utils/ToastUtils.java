package com.rfid.demo.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    private static Context mContext;
    public static void init(Context context){
        mContext = context;

    }

    public static void show(String msg){
        Toast.makeText(mContext,""+msg,Toast.LENGTH_SHORT).show();
    }


}
