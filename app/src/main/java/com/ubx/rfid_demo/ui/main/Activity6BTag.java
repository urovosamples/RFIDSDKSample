package com.ubx.rfid_demo.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.ubx.rfid_demo.BaseApplication;
import com.ubx.rfid_demo.R;
import com.ubx.usdk.USDKManager;
import com.ubx.usdk.rfid.RfidManager;
import com.ubx.usdk.rfid.aidl.ITag6BCallback;
import com.ubx.usdk.rfid.util.CMDCode;
import com.ubx.usdk.rfid.util.ErrorCode;
import com.ubx.usdk.util.ConvertUtils;
import com.ubx.usdk.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Activity6BTag extends AppCompatActivity implements OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private final String TAG = Activity6BTag.class.getSimpleName();

    Button rButton;
    Button wButton;
    Button lockButton;
    Button checkButtn;
    EditText c_wordPtr;
    EditText c_len;

    EditText content;
    EditText readContent;
    private ArrayAdapter<String> spada_epc;
    Spinner spepc;
    TextView tvResult;
    byte[] TranData = null;
    Handler handler;
    Thread mThread = null;

    private RfidManager mRfidManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_6b);
        initView();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    switch (msg.what) {
                        case 0x01:
                            String arg = msg.obj + "";
                            tvResult.setText(arg);
                            break;
                        case 0x02:

                            break;
                        case 0x03:
                            break;
                        case 0x04:
                            break;
                        case MSG_UPDATE_LISTVIEW:
                            String result = msg.obj + "";
                            String[] strs = result.split(",");
                            if (strs.length == 2) {
                                addEPCToList(strs[0]);
                            } else {
                                addEPCToList(strs[0] + "," + strs[1]);
                            }

                            break;
                        case MSG_UPDATE_STOP:
                            setViewEnabled(true);
                            BtInventory.setText(getString(R.string.btInventory));
                            break;
                        default:
                            break;
                    }
                } catch (Exception ex) {
                    ex.toString();
                }
            }
        };
        initonCreate();
    }

    private void initView() {
        spepc = (Spinner) findViewById(R.id.b_spinner);
        tvResult = (TextView) findViewById(R.id.b_result);

        rButton = (Button) findViewById(R.id.button_read_6b);
        wButton = (Button) findViewById(R.id.button_write_6b);
        lockButton = (Button) findViewById(R.id.button_lock);
        checkButtn = (Button) findViewById(R.id.button_checklok);

        rButton.setOnClickListener(this);
        wButton.setOnClickListener(this);
        lockButton.setOnClickListener(this);
        checkButtn.setOnClickListener(this);

        c_wordPtr = (EditText) findViewById(R.id.et_wordptr);
        c_wordPtr.setText("8");
        c_len = (EditText) findViewById(R.id.et_length);
        c_len.setText("6");

        content = (EditText) findViewById(R.id.et_content_6b);
        readContent = (EditText) findViewById(R.id.et_read_6b);
//        initRfid();
    }
    private void initRfid() {
        // 在异步回调中拿到RFID实例
        USDKManager.getInstance().init(BaseApplication.getContext(),new USDKManager.InitListener() {
            @Override
            public void onStatus(USDKManager.STATUS status) {
                if ( status == USDKManager.STATUS.SUCCESS) {
                    Log.d(TAG, "initRfidService: 状态成功");
                    mRfidManager =   USDKManager.getInstance().getRfidManager();
//                mRfidManager.registerCallback(callback);
                    // 设置波特率
                    if (mRfidManager.connectCom("/dev/ttyHSL0", 115200)) {
                        final String mf = mRfidManager.getModuleFirmware();
                        Log.d(TAG, "initRfidService:   mf："+mf);
                        mRfidManager.tag6BCallback(iTag6BCallback);
                        mRfidManager.getFirmwareVersion();

//                         mRfidManager.setImpinjFastTid( mRfidManager.getReadId(),false,false);//设置是否盘存EPC+TID
//                    int outputPower = mRfidManager.getOutputPower(mRfidManager.getReadId());
//                    Log.d(TAG, "initRfidService: outputPower = " + outputPower);
                    }
                }else {
                    Log.d(TAG, "initRfidService: 状态失败。");
                }
            }
        });


    }
    @Override
    protected void onResume() {
        getRFIDManager();
        updateUISpinner();
        super.onResume();
        isStopThread = false;
    }

    private void getRFIDManager() {
        if (mRfidManager == null) {
            mRfidManager = USDKManager.getInstance().getRfidManager();
            mRfidManager.tag6BCallback(iTag6BCallback);
        }
    }

    private void updateUISpinner() {
        // TODO Auto-generated method stub
        if (mlist != null) {
            int epcCount = mlist.size();
            String[] epcdata = new String[epcCount];

            for (int i = 0; i < mlist.size(); i++) {
                String temp = mlist.get(i);
                epcdata[i] = temp;
            }

            //  if(epcCount>0)
            {
                spada_epc = new ArrayAdapter<String>(Activity6BTag.this,
                        android.R.layout.simple_spinner_item, epcdata);
                spada_epc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spepc.setAdapter(spada_epc);
                if (epcCount > 0)
                    spepc.setSelection(0, false);
            }
        }
    }

    @Override
    public void onClick(View view) {
        getRFIDManager();
        if (mRfidManager == null) {
            toast("rfid 未初始化.");
            return;
        }

        if (view == rButton) {
            String strEPC = "";
            if (spepc.getSelectedItem() != null)
                strEPC = spepc.getSelectedItem().toString();

            byte Num = (byte) (int) Integer.valueOf(c_len.getText().toString());
            int WordPtr = (int) Integer.valueOf(c_wordPtr.getText().toString());
            byte[] ID = ConvertUtils.hexStringToBytes(strEPC);
            byte[] data = new byte[Num & 255];
            int result = mRfidManager.iso180006BReadTag( ID, (byte) WordPtr, Num);

            if (result != 0) {
                toast("指令执行失败");
            }
        } else if (view == wButton) {
            String strEPC = "";
            if (spepc.getSelectedItem() != null)
                strEPC = spepc.getSelectedItem().toString();
            byte WordPtr = (byte) (int) Integer.valueOf(c_wordPtr.getText().toString());
            byte[] ID = ConvertUtils.hexStringToBytes(strEPC);
            String strData = content.getText().toString();
            byte Num = (byte) (strData.length() / 2);
            byte[] data = ConvertUtils.hexStringToBytes(strData);
            int result = mRfidManager.iso180006BWriteTag( ID, WordPtr, Num, data);
            if (result != 0) {
                toast("指令执行失败");
            }
        } else if (view == lockButton) {
            String strEPC = "";
            if (spepc.getSelectedItem() != null)
                strEPC = spepc.getSelectedItem().toString();
            byte WordPtr = (byte) (int) Integer.valueOf(c_wordPtr.getText().toString());
            byte[] ID = ConvertUtils.hexStringToBytes(strEPC);
            int result = mRfidManager.iso180006BLockTag( ID, WordPtr);
            if (result != 0) {
                toast("指令执行失败");
            }
        } else if (view == checkButtn) {
            int result = -1;
            String strEPC = "";
            if (spepc.getSelectedItem() != null)
                strEPC = spepc.getSelectedItem().toString();
            byte WordPtr = (byte) (int) Integer.valueOf(c_wordPtr.getText().toString());
            byte[] ID = ConvertUtils.hexStringToBytes(strEPC);
            result = mRfidManager.iso180006BQueryLockTag( ID, (byte) WordPtr);
            if (result != 0) {
                toast("检测失败");
            }

        } else if (view == BtInventory) {
            readTag();
        } else if (view == BtClear) {
            clearData();
        }
    }

    private ITag6BCallback iTag6BCallback = new ITag6BCallback() {
        @Override
        public void onRead6BTag(String strData) {
            toast("读取成功：" + strData);
            readContent.setText("" + strData);
        }

        @Override
        public void onWrite6BTag(byte nWriteLen) {


        }

        @Override
        public void onLock6BTag(byte nStatus) {
            int result = nStatus;
            if (result != 0) {
                toast("锁定失败");
            } else {
                toast("锁定成功");
            }
        }

        @Override
        public void onLockQuery6BTag(byte nStatus) {
            int result = nStatus;
            if (nStatus == 0)
                toast("未锁定");
            else if (nStatus == 1)
                toast("已锁定");

        }

        @Override
        public void onExeCMDStatus(byte cmd, byte status) {
            LogUtils.v(TAG, "onExeCMDStatus()    cmd:" + cmd + "     status:" + status);
            if (cmd == CMDCode.ISO18000_6B_WRITE_TAG) {
                if (status == ErrorCode.SUCCESS) {
                    toast("写成功");
                } else {
                    toast("写失败");
                }
            }
        }

        @Override
        public void onInventory6BTag(String strUID) {
            LogUtils.v(TAG, "onInventory6BTag()    strUID:" + strUID);
            String epc = strUID;
            String rssi = "0";
            Message msg = handler.obtainMessage();
            msg.what = MSG_UPDATE_LISTVIEW;
            msg.obj = epc + "," + rssi;
            handler.sendMessage(msg);
        }
    };

    private void toast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Activity6BTag.this, "" + msg, Toast.LENGTH_LONG).show();
            }
        });
    }


    private int inventoryFlag = 1;
    private ArrayList<HashMap<String, String>> tagList;
    SimpleAdapter adapter;
    Button BtClear;
    TextView tv_count;
    TextView tv_alltag;
    Button BtInventory;
    ListView LvTags;
    private LinearLayout llContinuous;
    private HashMap<String, String> map;
    public boolean isStopThread = false;
    private static final int MSG_UPDATE_LISTVIEW = 10;
    private static final int MSG_UPDATE_STOP = 13;
    public long CardNumber;
    public static List<String> mlist = new ArrayList<String>();

    private void initonCreate() {
        tagList = new ArrayList<HashMap<String, String>>();
        BtClear = (Button) findViewById(R.id.BtClear);
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_alltag = (TextView) findViewById(R.id.tv_alltag);
        String tr = "";

        BtInventory = (Button) findViewById(R.id.BtInventory);
        LvTags = (ListView) findViewById(R.id.LvTags);

        llContinuous = (LinearLayout) findViewById(R.id.llContinuous);

        adapter = new SimpleAdapter(this, tagList, R.layout.listtag_items,
                new String[]{"tagUii", "tagLen", "tagCount", "tagRssi"},
                new int[]{R.id.TvTagUii, R.id.TvTagLen, R.id.TvTagCount});

        BtClear.setOnClickListener(this);
        BtInventory.setOnClickListener(this);


        LvTags.setAdapter(adapter);
        clearData();
        Log.i("MY", "UHFReadTagFragment.EtCountOfTags=" + tv_count.getText());
    }


    private void setViewEnabled(boolean enabled) {
        //   btnFilter.setEnabled(enabled);
        BtClear.setEnabled(enabled);
    }


    public int checkIsExist(String strEPC) {
        int existFlag = -1;
        if (strEPC == null || strEPC.length() == 0) {
            return existFlag;
        }
        String tempStr = "";
        for (int i = 0; i < tagList.size(); i++) {
            HashMap<String, String> temp = new HashMap<String, String>();
            temp = tagList.get(i);
            tempStr = temp.get("tagUii");
            if (strEPC.equals(tempStr)) {
                existFlag = i;
                break;
            }
        }
        return existFlag;
    }

    private void clearData() {
        tv_count.setText("0");
        tv_alltag.setText("0");
        tagList.clear();
        mlist.clear();
        CardNumber = 0;
        adapter.notifyDataSetChanged();
    }

    /**
     * 添加EPC到列表中
     *
     * @param
     */
    private void addEPCToList(String rfid) {
        if (!TextUtils.isEmpty(rfid)) {
            String epc = "";
            String[] data = rfid.split(",");
            if (data.length == 1) {
                epc = data[0];
            } else {
                epc = "EPC:" + data[0] + "\r\nMem:" + data[1];
            }

            int index = checkIsExist(epc);
            map = new HashMap<String, String>();

            map.put("tagUii", epc);
            map.put("tagCount", String.valueOf(1));
            CardNumber++;
            if (index == -1) {
                tagList.add(map);
                LvTags.setAdapter(adapter);
                tv_count.setText("" + adapter.getCount());
                mlist.add(data[0]);
            } else {
                int tagcount = Integer.parseInt(
                        tagList.get(index).get("tagCount"), 10) + 1;

                map.put("tagCount", String.valueOf(tagcount));

                tagList.set(index, map);

            }
            tv_alltag.setText(String.valueOf(CardNumber));
            adapter.notifyDataSetChanged();

        }
    }


    private void readTag() {
        LogUtils.v("rfid", " readTag() ");
        if (BtInventory.getText().equals(getString(R.string.btInventory)))// 识别标签
        {
            int result = mRfidManager.iso180006BInventory();
            if (result == 0) {
                BtInventory.setText(getString(R.string.btn_stop_Inventory));
                setViewEnabled(false);
            }
        } else {// 停止识别

            stopInventory();
        }
    }

    private void stopInventory() {
        if (mRfidManager != null) {
            mRfidManager.stopInventory();
        }
        setViewEnabled(true);
        BtInventory.setText(getString(R.string.btInventory));
        updateUISpinner();
    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopInventory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }


}
