package com.ubx.rfid_demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ubx.rfid_demo.pojo.TagScan;
import com.ubx.rfid_demo.ui.main.Activity6BTag;
import com.ubx.rfid_demo.ui.main.SectionsPagerAdapter;

import com.ubx.rfid_demo.ui.main.TagManageFragment;
import com.ubx.rfid_demo.ui.main.TagScanFragment;
import com.ubx.rfid_demo.ui.main.SettingFragment;

import com.ubx.usdk.USDKManager;
import com.ubx.usdk.rfid.RfidManager;
import com.ubx.usdk.util.QueryMode;
import com.ubx.usdk.util.SoundTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "usdk";

    public  boolean RFID_INIT_STATUS = false;
    public RfidManager mRfidManager;
    public List<String> mDataParents;
    public List<TagScan> tagScanSpinner;
    private List<Fragment> fragments ;
    private ViewPager viewPager ;
    private TabLayout tabs;
    public int readerType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        mDataParents = new ArrayList<>();
        tagScanSpinner = new ArrayList<>();

        SoundTool.getInstance(BaseApplication.getContext());
        initRfid();
//        initRfidService();

         fragments = Arrays.asList(TagScanFragment.newInstance(MainActivity.this)
                , TagManageFragment.newInstance(MainActivity.this)
                , SettingFragment.newInstance(MainActivity.this));
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), fragments);
          viewPager =  findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
         tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initRfid() {
        // 在异步回调中拿到RFID实例
        USDKManager.getInstance().init(BaseApplication.getContext(),new USDKManager.InitListener() {
            @Override
            public void onStatus(USDKManager.STATUS status) {
                if ( status == USDKManager.STATUS.SUCCESS) {
                    Log.d(TAG, "initRfid()  success.");
                    mRfidManager =   USDKManager.getInstance().getRfidManager();
                    ((TagScanFragment)fragments.get(0)).setCallback();
                    mRfidManager.setOutputPower((byte) 30);

                    Log.d(TAG, "initRfid: getDeviceId() = " +mRfidManager.getDeviceId());

                    readerType =  mRfidManager.getReaderType();//80为短距，其他为长距


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,"module："+readerType,Toast.LENGTH_LONG).show();
                        }
                    });

                    Log.d(TAG, "initRfid: GetReaderType() = " +readerType );
                }else {
                    Log.d(TAG, "initRfid  fail.");
                }
            }
        });

    }

    /**
     * 设置查询模式
     * @param mode
     */
    private void setQueryMode(int mode){
        mRfidManager.setQueryMode(QueryMode.EPC_TID);
    }

    /**
     * 通过TID写标签
     */
    private void writeTagByTid(){
        //写入方法（不需要先选中）
        String tid = "24 length TID";
        String writeData = "need write EPC datas ";
        mRfidManager.writeTagByTid(tid,(byte) 0,(byte) 2,"00000000".getBytes(),writeData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        SoundTool.getInstance(BaseApplication.getContext()).release();
        RFID_INIT_STATUS = false;
        if (mRfidManager != null) {
            mRfidManager.disConnect();
            mRfidManager.release();

            Log.d(TAG, "onDestroyView: rfid close");
//            System.exit(0);
        }
    }

    /**
     * 设置盘存时间
     * @param interal 0-200 ms
     */
    private void setScanInteral(int interal){
        int setScanInterval =   mRfidManager.setScanInterval( interal);
        Log.v(TAG,"--- setScanInterval()   ----"+setScanInterval);
    }

    /**
     * 获取盘存时间
     */
    private void getScanInteral(){
        int getScanInterval =   mRfidManager.getScanInterval( );
        Log.v(TAG,"--- getScanInterval()   ----"+getScanInterval);
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);//弹出Menu前调用的方法
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_rate_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_6b:
                Intent intent = new Intent(MainActivity.this, Activity6BTag.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == 523 &&  event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0){
            //TODO 按下



            return true;
        }else if (event.getKeyCode() == 523 &&  event.getAction() == KeyEvent.ACTION_UP && event.getRepeatCount() == 0){
            //TODO 抬起



            return true;
        }
        return super.dispatchKeyEvent(event);
    }

}