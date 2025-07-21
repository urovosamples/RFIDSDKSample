package com.rfid.demo.utils;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.widget.TextView;

import com.rfid.demo.BaseApplication;
import com.rfid.demo.R;
import com.ubx.usdk.io.utils.LogHelper;

public class MsgShow {

	public  static void writelogSuc(String log,TextView tvResult){
		writelog( log, tvResult,true);
	}
	public  static void writelogErr(String log,TextView tvResult){
		writelog( log, tvResult,false);
	}
	public   static void writelog(String log, TextView tvResult, boolean isSuc)
	{
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");// HH:mm:ss
            Date date = new Date(System.currentTimeMillis());
            String textlog = simpleDateFormat.format(date)+" "+log;
            tvResult.setText(textlog);
            if (isSuc){
                tvResult.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.main_color));
            }else {
                tvResult.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.red));
            }
        } catch ( Exception e) {
			LogHelper.info("writelog() Exception : "+e.getMessage());
        }
    }

}
