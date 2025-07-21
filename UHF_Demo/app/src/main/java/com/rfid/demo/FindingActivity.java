package com.rfid.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rfid.demo.utils.CircleProgress;
import com.ubx.usdk.RFIDSDKManager;
import com.ubx.usdk.bean.Tag6C;
import com.ubx.usdk.util.SoundTool;


public class FindingActivity extends Activity implements View.OnClickListener {
    private CircleProgress mCircleProgress;
    TextView epcid;
    Button btFinding;
    private volatile boolean mWorking = true;
    private volatile Thread mThread=null;
    Handler handler;
    int rssi=0;
    public boolean keyPress =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finding);
        mCircleProgress = (CircleProgress) findViewById(R.id.circle_progress);
        epcid =(TextView)findViewById(R.id.epc_id);
        btFinding = (Button)findViewById(R.id.btfind);
        btFinding.setOnClickListener(this);
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                try{
                    switch (msg.what) {
                        case 0:
                            String rssistr = msg.obj+"";
                            int rssi = (int)Integer.valueOf(rssistr);
                            mCircleProgress.setValue((float)rssi);
                            break;
                        default:
                            break;
                    }
                }catch(Exception ex)
                {ex.toString();}
            }
        };
    }


    @Override
    protected void onResume() {
        epcid.setText(ScanActivity.epc);
        mCircleProgress.setValue(0.00f);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if(v == btFinding)
        {
            if (TextUtils.isEmpty(ScanActivity.epc)){
                Toast.makeText(this, R.string.please_choice_epc, Toast.LENGTH_SHORT).show();
                return;
            }

            readTag();

        }
    }

    private void readTag()
    {
        if(btFinding.getText().toString().equals(getString(R.string.finding)))
        {
            if(mThread==null)
            {
                mWorking = true;
                btFinding.setText(R.string.btstop);


//                Tag6C mtag = RFIDSDKManager.getInstance().getRfidManager().findEpc(ScanActivity.epc);

                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(mWorking)
                        {
                            Tag6C mtag = RFIDSDKManager.getInstance().getRfidManager().findEpc(ScanActivity.epc);
                            if(mtag!=null)
                            {
                                rssi = mtag.rssi;
                                SoundTool.getInstance().playSound(1);
                                Message msg = handler.obtainMessage();
                                msg.what = 0;
                                msg.obj = rssi + "";
                                handler.sendMessage(msg);
                                num = 0;
                            }
                            else
                            {

//                            if (num >=3) {
                                num = 0;
                                if (rssi > 0)
//                                    rssi -= 2;
                                    rssi -= 5;
                                if (rssi < 0) rssi = 0;
                                Message msg = handler.obtainMessage();
                                msg = handler.obtainMessage();
                                msg.what = 0;
                                msg.obj = rssi + "";
                                handler.sendMessage(msg);
//                            }
                                num++;
                            }
                        }
                    }
                });
                mThread.start();
            }
        }
        else
        {
            if(mThread!=null)
            {
                mWorking=false;
                try {
                    mThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mThread=null;

            }
            Message msg = handler.obtainMessage();
            msg = handler.obtainMessage();
            msg.what = 0;
            msg.obj = 0 + "";
            handler.sendMessage(msg);
            btFinding.setText(R.string.finding);
        }
    }
    private int num = 0;

    /**
     *按键扫描RFID
     **/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==523 && !keyPress)
        {
            keyPress = true;
            readTag();
        }
//        else if (keyCode == KeyEvent.KEYCODE_BACK) {
//            finish();
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     *按键扫描RFID
     **/
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode==523)
        {
            keyPress =false;
        }
        return super.onKeyUp(keyCode, event);
    }


}