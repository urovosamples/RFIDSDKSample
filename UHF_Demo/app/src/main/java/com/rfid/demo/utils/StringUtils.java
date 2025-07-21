package com.rfid.demo.utils;

import android.text.TextUtils;

public class StringUtils {
    public static boolean isEmpty(String str) {
        return !TextUtils.isEmpty(str);
    }
}
