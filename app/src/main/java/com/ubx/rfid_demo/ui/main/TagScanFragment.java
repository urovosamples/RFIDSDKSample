package com.ubx.rfid_demo.ui.main;


import android.bluetooth.le.ScanCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ubx.rfid_demo.BaseApplication;
import com.ubx.rfid_demo.MainActivity;
import com.ubx.rfid_demo.R;
import com.ubx.rfid_demo.pojo.TagScan;

import com.ubx.usdk.USDKManager;
import com.ubx.usdk.rfid.aidl.IRfidCallback;
import com.ubx.usdk.rfid.aidl.RfidDate;
import com.ubx.usdk.rfid.util.CMDCode;
import com.ubx.usdk.rfid.util.ErrorCode;
import com.ubx.usdk.util.QueryMode;
import com.ubx.usdk.util.SoundTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TagScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TagScanFragment extends Fragment {

    public static final String TAG = "usdk-"+TagScanFragment.class.getSimpleName();
    private List<TagScan> data;
    private HashMap<String, TagScan> mapData;
    private ScanCallback callback  ;
    private ScanListAdapterRv scanListAdapterRv;
    private static  MainActivity mActivity;
    private int tagTotal = 0;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ScanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TagScanFragment newInstance(MainActivity activity) {
        mActivity = activity;
        TagScanFragment fragment = new TagScanFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tag_scan, container, false);
    }

    private Button scanStartBtn,btnStartL,btnConnect,btnDisConnect;
    private CheckBox checkBox;
    private RecyclerView scanListRv;
    private TextView scanCountText,scanTotalText,textFirmware;

    @Override
    public void onViewCreated( View view,   Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);





        checkBox = view.findViewById(R.id.checkBox);
        scanStartBtn = view.findViewById(R.id.scan_start_btn);
        scanListRv = view.findViewById(R.id.scan_list_rv);

        scanCountText= view.findViewById(R.id.scan_count_text);
        scanTotalText= view.findViewById(R.id.scan_total_text);
        textFirmware = view.findViewById(R.id.text_firmware);

        scanStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {

                    if (mActivity.RFID_INIT_STATUS) {



                        if (scanStartBtn.getText().equals(getString(R.string.btInventory))) {
                            setCallback();
                            scanStartBtn.setText(getString(R.string.btn_stop_Inventory));
                            setScanStatus(true);
                        } else {
                            scanStartBtn.setText(getString(R.string.btInventory));
                            setScanStatus(false);
                        }
                    }else {
                        Log.d(TAG, "scanStartBtn  RFID未初始化 "  );
                        Toast.makeText(getActivity(),"RFID Not initialized",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mapData = new HashMap<>();


        scanListRv.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        scanListRv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        scanListAdapterRv = new ScanListAdapterRv(null, getActivity());
        scanListRv.setAdapter(scanListAdapterRv);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mActivity.mRfidManager !=null  ) {
                    if (b) {//开启读取 TID
                        mActivity.mRfidManager.setQueryMode(QueryMode.EPC_TID);
                    } else {//关闭读取TID
                        mActivity.mRfidManager.setQueryMode(QueryMode.EPC);
                    }
                }
            }
        });

    }


    private final int MSG_UPDATE_UI = 0;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_UPDATE_UI:
                    scanListAdapterRv.notifyDataSetChanged();
                    handlerUpdateUI();
                    break;
            }

        }
    };




    @Override
    public void onStart() {
        super.onStart();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mActivity.mRfidManager!=null) {
                    Log.v(TAG,"--- getFirmwareVersion()   ----");
                    mActivity.RFID_INIT_STATUS = true;
                    String firmware =  mActivity.mRfidManager.getFirmwareVersion();
                    textFirmware.setText(getString(R.string.firmware)+firmware);
                }else {
                    Log.v(TAG,"onStart()  --- getFirmwareVersion()   ----  mActivity.mRfidManager == null");
                }
            }
        }, 5000);
    }

    private void setScanStatus(boolean isScan) {

        if (isScan) {
            tagTotal = 0;
            if (mapData!=null){
                mapData.clear();
            }
            if (mActivity.mDataParents != null){
                mActivity.mDataParents.clear();
            }
            if (mActivity.tagScanSpinner != null){
                mActivity.tagScanSpinner.clear();
            }
            if (data!=null) {
                data.clear();
                scanListAdapterRv.setData(data);
            }

            Log.v(TAG,"--- startInventory()   ----");
            handlerUpdateUI();
            mActivity.mRfidManager.startInventory( (byte) 0);//少量标签盘点建议使用：0；盘点标签超过 100-200建议使用：1.
        } else {
            Log.v(TAG,"--- stopInventory()   ----");
            mActivity.mRfidManager.stopInventory();
            handlerStopUI();
        }
    }

    private void handlerUpdateUI(){
        if (mHandler!=null){
            mHandler.sendEmptyMessageDelayed(MSG_UPDATE_UI,1000);
        }
    }
    private void handlerStopUI(){
        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
        }
    }


    private long time = 0l;

    /**
     * 单个读取EPC 或tid
     */
    private void readTagOnce(){
        //读取TID的起始地址。
        //读取TID长度，如果长度为0，则读取EPC号
        String  data =  mActivity.mRfidManager.readTagOnce( (byte) 0, (byte) 0);
        //data 读到的标签值
    }

    /**
     * 设置掩码（标签过滤盘存）
     */
    private void setTagMask(){
        mActivity.mRfidManager.setTagMask(2,24,16,"7020");
    }


    /**
     * 通过TID写入标签数据
     * @param TID   选中的TID
     * @param Mem   标签区域：0-密码区，前2个字是销毁密码，后2个字是访问密码      1-EPC区   2-TID区    3-用户区
     * @param WordPtr  写入的起始字地址
     * @param pwd   密码
     * @param  datas  待写入数据
     */
    private void writeTagByTid(String TID,byte Mem,byte WordPtr,byte[] pwd,String datas){
//                String TID = "E280110C20007642903D094D";
//                 byte[] pwd = hexStringToBytes("00000000");
//                 String datas = "1111111111111111";
        int ret =  mActivity.mRfidManager.writeTagByTid(TID,(byte) 1,(byte) 2, pwd,datas);
        if (ret == -6){
            Toast.makeText(mActivity, getContext().getString(R.string.gj_no_support), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 随机对一张标签写入EPC
     * @param epc   待写入的EPC值 16进制字符串
     * @param password  标签访问密码
     */
    private void writeEpcString(String epc,String password){
        mActivity.mRfidManager.writeEpcString( epc, password);
    }




    class ScanCallback implements IRfidCallback  {



        @Override
        public void onInventoryTag(String EPC, final String TID, final String strRSSI) {

            notiyDatas(EPC,TID,strRSSI);

        }

        /**
         * 盘存结束回调(Inventory Command Operate End)
         */
        @Override
        public void onInventoryTagEnd()  {
            Log.d(TAG, "onInventoryTag()");
        }
    }


    private void notiyDatas(final String s2, final String TID, final String strRSSI){
        String mapContainStr = null;
        if (!TextUtils.isEmpty(TID)){
            mapContainStr = TID;
        }else {
            mapContainStr = s2;
        }
        final String mapContainStrFinal = mapContainStr;
        Log.d(TAG, "onInventoryTag: EPC: " + s2);
        SoundTool.getInstance(BaseApplication.getContext()).playBeep(1);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                {
                    if (mapData.containsKey(mapContainStrFinal)) {
                        TagScan tagScan = mapData.get(mapContainStrFinal);
                        tagScan.setCount(mapData.get(mapContainStrFinal).getCount() + 1);
//                    tagScan.setTid(TID);
                        tagScan.setRssi(strRSSI);
                        mapData.put(mapContainStrFinal, tagScan);
                    } else {
                        mActivity.mDataParents.add(s2);

                        TagScan tagScan = new TagScan(s2, TID,strRSSI, 1);
                        mapData.put(mapContainStrFinal, tagScan);
                        mActivity.tagScanSpinner.add(tagScan);
                    }

                    scanTotalText.setText(++tagTotal + "");

                    long nowTime = System.currentTimeMillis();
                    if ((nowTime - time)>1000){
                        time = nowTime;
                        data = new ArrayList<>(mapData.values());
                        Log.d(TAG, "onInventoryTag: data = " + Arrays.toString(data.toArray()));
                        scanListAdapterRv.setData(data);
                        scanCountText.setText(mapData.keySet().size() + "");

                    }


                }
            }
        });
    }




    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            setCallback();
        }
    }



    public void setCallback(){
        if (mActivity.mRfidManager!=null) {

            if (callback == null){
                callback = new ScanCallback();
            }
            mActivity.mRfidManager.registerCallback(callback);
        }
    }
    /**
     * 将Hex String转换为Byte数组
     *
     * @param hexString the hex string
     * @return the byte [ ]
     */
    public static byte[] hexStringToBytes(String hexString) {
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() >> 1];
        int index = 0;
        for (int i = 0; i < hexString.length(); i++) {
            if (index > hexString.length() - 1) {
                return byteArray;
            }
            byte highDit = (byte) (Character.digit(hexString.charAt(index), 16) & 0xFF);
            byte lowDit = (byte) (Character.digit(hexString.charAt(index + 1), 16) & 0xFF);
            byteArray[i] = (byte) (highDit << 4 | lowDit);
            index += 2;
        }
        return byteArray;
    }
}