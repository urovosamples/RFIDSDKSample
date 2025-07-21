package com.rfid.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.rfid.demo.utils.FileImport;
import com.rfid.demo.utils.Util;
import com.ubx.usdk.RFIDSDKManager;
import com.ubx.usdk.rfid.aidl.IRfidCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ScanActivity extends Activity implements OnClickListener, OnItemClickListener{

    private String TAG = ScanActivity.class.getSimpleName();
    private int inventoryFlag = 1;
    Handler handler;
    private ArrayList<HashMap<String, String>> tagList;
    SimpleAdapter adapter;
    Button BtClear;
    TextView tv_count;
    TextView tv_time;
    TextView tv_alltag;
    TextView tv_speed;
    TextView tv_fm;
    RadioGroup RgInventory;
    RadioButton RbInventorySingle;
    RadioButton RbInventoryLoop;
    Button Btimport;
    Button BtInventory ;
    ListView LvTags;
    String items[] = null;
    boolean chk[] = null;

    private TextView tv_tips;

    public static String epc;
    public static String tid;
    //private Button btnFilter;//过滤
    private HashMap<String, String> map;
    PopupWindow popFilter;
    public boolean isStopThread = false;
    ScanCallback callback = new ScanCallback();

    private static final int MSG_UPDATE_LISTVIEW = 0;
    private static final int MSG_UPDATE_TIME = 1;
    private static final int MSG_UPDATE_SPEED = 2;
    private static final int MSG_UPDATE_STOP = 3;
    private static final int MSG_UPDATE_LIST = 4;
    private static final int MSG_UPDATE_TV = 5;
    private Timer timer;
    public long beginTime;
    public long CardNumber;
    public static List<String> mlist = new ArrayList<String>();
    public long lastTime = 0;
    public int lastCount = 0;

    public static List<String> ledlist = new ArrayList<String>();
    public static int timeout = 300;//300 seconds

    private Context mContext;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        try {

            setContentView(R.layout.activity_scan);

            tv_tips = findViewById(R.id.tv_tips);
            tagList = new ArrayList<HashMap<String, String>>();
            BtClear = (Button) findViewById(R.id.BtClear);
            Btimport = (Button) findViewById(R.id.BtImport);
            tv_count = (TextView) findViewById(R.id.tv_count);
            tv_time = (TextView) findViewById(R.id.tv_times);
            tv_alltag = (TextView) findViewById(R.id.tv_alltag);
            tv_speed = (TextView) findViewById(R.id.tv_tagspeed);
            tv_fm = (TextView) findViewById(R.id.tv_fm);
            RgInventory = (RadioGroup) findViewById(R.id.RgInventory);
            String tr = "";
            RbInventorySingle = (RadioButton) findViewById(R.id.RbInventorySingle);
            RbInventoryLoop = (RadioButton) findViewById(R.id.RbInventoryLoop);

            BtInventory = (Button) findViewById(R.id.BtInventory);
            LvTags = (ListView) findViewById(R.id.LvTags);
            LvTags.setOnCreateContextMenuListener(lvjzwOnCreateContextMenuListener);

            adapter = new SimpleAdapter(this, tagList, R.layout.listtag_items,
                    new String[]{"tagUii", "tagLen", "tagCount", "tagRssi"},
                    new int[]{R.id.TvTagUii, R.id.TvTagLen, R.id.TvTagCount,
                            R.id.TvTagRssi}) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView TvTagUii = view.findViewById(R.id.TvTagUii);
                    TextView TvTagLen = view.findViewById(R.id.TvTagLen);
                    TextView TvTagCount = view.findViewById(R.id.TvTagCount);
                    TextView TvTagRssi = view.findViewById(R.id.TvTagRssi);
                    if (tagList.get(position).get("tagRssi").equals("0")) {
                        TvTagUii.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                        TvTagLen.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                        TvTagCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                        TvTagRssi.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                    } else {
                        TvTagUii.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                        TvTagLen.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                        TvTagCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                        TvTagRssi.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                    }
                    return super.getView(position, convertView, parent);
                }
            };

            BtClear.setOnClickListener(this);
            Btimport.setOnClickListener(this);
            RgInventory.setOnCheckedChangeListener(new RgInventoryCheckedListener());
            BtInventory.setOnClickListener(this);

            RFIDSDKManager.getInstance().getRfidManager().getModuleType();

            if (RFIDSDKManager.getInstance().getRfidManager() != null) {
                RFIDSDKManager.getInstance().getRfidManager().registerCallback(callback);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String fm = RFIDSDKManager.getInstance().getRfidManager().getFirmwareVersion();
                        tv_fm.setText(Util.toVer(fm));
                    }
                }, 1000);

            }

            LvTags.setAdapter(adapter);
            clearData();
            Log.i("MY", "UHFReadTagFragment.EtCountOfTags=" + tv_count.getText());
            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    try {
                        switch (msg.what) {
                            case MSG_UPDATE_LISTVIEW:
                                String result = msg.obj + "";
                                String[] strs = result.split(",");
                                if (strs.length == 2) {
                                    addEPCToList(strs[0], strs[1]);
                                } else {
                                    addEPCToList(strs[0] + "," + strs[1], strs[2]);
                                }

                                break;
                            case MSG_UPDATE_TIME:
                                String ReadTime = msg.obj + "";
                                long CurTime = Integer.valueOf(ReadTime);
                                long hour = CurTime / (60 * 60 * 1000);
                                long min = (CurTime / 1000 - (hour * 60 * 60)) / 60;
                                long sec = (CurTime / 1000 - hour * 60 * 60 - min * 60);
                                String strHour = String.valueOf(hour);
                                if (strHour.length() < 2) strHour = "0" + strHour;
                                String strmin = String.valueOf(min);
                                if (strmin.length() < 2) strmin = "0" + strmin;
                                String strsec = String.valueOf(sec);
                                if (strsec.length() < 2) strsec = "0" + strsec;
                                tv_time.setText(strHour + ":" + strmin + ":" + strsec);
                                break;
                            case MSG_UPDATE_SPEED:
                                String readSpeed = msg.obj + "";
                                tv_speed.setText(readSpeed);
                                break;
                            case MSG_UPDATE_STOP:
                                updateList();
                                if (timer != null) {
                                    timer.cancel();
                                    timer = null;
                                    BtInventory.setText(getString(R.string.btStoping));
                                }
                                setViewEnabled(true);
                                BtInventory.setText(getString(R.string.btInventory));

                                if (ledlist.size() == 0) {
                                    items = new String[tagList.size()];
                                    chk = new boolean[tagList.size()];
                                    for (int i = 0; i < tagList.size(); i++) {
                                        items[i] = tagList.get(i).get("tagEpc");
                                        chk[i] = false;
                                    }
                                }

                                break;
                            case MSG_UPDATE_LIST:
                                updateList();
                                break;
                            case MSG_UPDATE_TV:
                                updateTv();
                                break;
                            default:
                                break;
                        }
                    } catch (Exception ex) {
                        ex.toString();
                    }
                }
            };
        } catch (Exception e) {

        }

        RFIDSDKManager.getInstance().getRfidManager().setBeepEnable(true);
    }
    public class RgInventoryCheckedListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == RbInventorySingle.getId()) {
                inventoryFlag = 0;
            }  else if (checkedId == RbInventoryLoop.getId()) {
                inventoryFlag = 1;
            }
        }
    }

    private void setViewEnabled(boolean enabled) {
        RbInventorySingle.setEnabled(enabled);
        RbInventoryLoop.setEnabled(enabled);
        //   btnFilter.setEnabled(enabled);
        BtClear.setEnabled(enabled);
        if (enabled) {
            BtInventory.setEnabled(enabled);
        }
    }


    private HashMap<String,Integer> integerHashMap = new HashMap<>();
    public int checkIsExist(String strEPC) {
        int existFlag = -1;
        if (strEPC == null || strEPC.length() == 0) {
            return existFlag;
        }

        if (integerHashMap.containsKey(strEPC)){
            return integerHashMap.get(strEPC);
        }
        return existFlag;
    }

    private void clearData() {
        tv_count.setText("0");
        tv_time.setText("00:00:00");
        tv_alltag.setText("0");
        tv_speed.setText("0");
        tagList.clear();
        integerHashMap.clear();
        mlist.clear();
        CardNumber = 0;
        items = null;
        chk = null;
        ledlist.clear();
        Log.i("MY", "tagList.size " + tagList.size());
        adapter.notifyDataSetChanged();

    }

    /**
     * 添加EPC到列表中
     *
     * @param
     */
    private void addEPCToList(String rfid, String rssi) {
//        long startLong = System.currentTimeMillis();
        if (!TextUtils.isEmpty(rfid)) {
            map = new HashMap<String, String>();
            String epc = "";
            String[] data = rfid.split(",");
            map.put("tagEpc", data[0]);
            if (data.length == 1) {
                epc = data[0];
            } else {
                epc = "EPC:" + data[0] + "\r\nMem:" + data[1];
            }

            int index = checkIsExist(epc);


            map.put("tagUii", epc);

            if (data.length > 1) {
                map.put("tagTid", data[1]);
            }else {
                map.put("tagTid", "");
            }
            map.put("tagCount", String.valueOf(1));
            map.put("tagRssi", rssi);
            CardNumber++;
            if (index == -1) {
                tagList.add(map);
                LvTags.setAdapter(adapter);
                tv_count.setText("" + adapter.getCount());
                mlist.add(data[0]);
                integerHashMap.put(epc,mlist.size()-1);
                updateList();
//                handler.sendEmptyMessage(MSG_UPDATE_TV);
            } else {
                int tagcount = Integer.parseInt( tagList.get(index).get("tagCount"), 10) + 1;

                map.put("tagCount", String.valueOf(tagcount));

                tagList.set(index, map);

//                long currUpdateListTime = System.currentTimeMillis();
//                if ((currUpdateListTime - lastUpdateListTime)>=200) {
//                    Log.i("MY", "notifyDataSetChanged();" + tagList.size());
//                    lastUpdateListTime = currUpdateListTime;
//                    updateList();
////                handler.sendEmptyMessage( MSG_UPDATE_LIST );
//                }

                updateList();

            }
        }
    }
    private void updateTv(){
//        LvTags.setAdapter(adapter);
        tv_count.setText("" + adapter.getCount());
    }
    private void updateList(){
        tv_alltag.setText(String.valueOf(CardNumber));
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        isStopThread = false;
    }


    @Override
    public void onClick(View arg0) {
        try {
            if (arg0 == BtInventory) {
                readTag();
            } else if (arg0 == BtClear) {
                clearData();
            }   else if (arg0 == Btimport) {
                if (tagList.size() == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.msgNodata), Toast.LENGTH_SHORT).show();
                    return;
                }


                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R ||
                        Environment.isExternalStorageManager()) {
                } else {
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle(R.string.tip)//设置标题
                            .setMessage(R.string.app_need_read_all_file)
                            .setPositiveButton(R.string.strok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                    startActivityForResult(intent, 101);
                                }
                            }).create();
                    dialog.show();

                    return;
                }

                boolean re = FileImport.daochu("", tagList);
                if (re) {
                    Toast.makeText(getApplicationContext(), getString(R.string.msgImportsuc), Toast.LENGTH_SHORT).show();
                    //clearData();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.msgImportfailed), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            stopInventory();
        }
    }
    private void readTag() {
        epc = "";
        tid = "";
        Log.d(TAG, "readTag()   "+getString(R.string.btInventory)+"  "+inventoryFlag);
        if (BtInventory.getText().equals(getString(R.string.btInventory)))// 识别标签
        {
            switch (inventoryFlag) {
                case 0:// Single
                {
                    RFIDSDKManager.getInstance().getRfidManager().inventorySingle();
                }
                break;
                case 1: {
//                    int result = RFIDSDKManager.getInstance().getRfidManager().startInventory();
                    int result = RFIDSDKManager.getInstance().getRfidManager().startInventoryWithTimeout(timeout);
                    if (result == 0) {
                        String content = "";
                        if (timeout == 0){
                            content = getResources().getString(R.string.tips_inv);
                        }else {
                            content =String.format( getResources().getString(R.string.tips_inv_timeout),timeout);
                        }
                        tv_tips.setText(content);
                        lastTime = System.currentTimeMillis();
                        lastCount = 0;
                        BtInventory.setText(getString(R.string.title_stop_Inventory));
                        setViewEnabled(false);
                        if (timer == null) {
                            beginTime = System.currentTimeMillis();
                            timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    long ReadTime = System.currentTimeMillis() - beginTime;
                                    Message msg = handler.obtainMessage();
                                    msg.what = MSG_UPDATE_TIME;
                                    msg.obj = String.valueOf(ReadTime);
                                    handler.sendMessage(msg);
                                }
                            }, 0, 200);
                        }
                    }
                }
                break;
                default:
                    break;
            }
        } else {// 停止识别
            Log.d(TAG, "readTag()   stopInventory()" );
            stopInventory();
        }
    }

    private void stopInventory() {
           RFIDSDKManager.getInstance().getRfidManager().stopInventory();
            tv_tips.setText("");
    }



    private long lastSoundTime = 0l;
    class ScanCallback implements IRfidCallback {
        @Override
        public void onInventoryTag(String EPC, final String Data, final int rssi) {
            String epc = "";
            String mem = "";
            if (EPC != null)
                epc = EPC.toUpperCase();
            if (Data != null)
                mem = Data.toUpperCase();

            Message msg = handler.obtainMessage();

//            RFIDSDKManager.getInstance().getRfidManager().startBeep();

            msg.what = MSG_UPDATE_LISTVIEW;
            if (mem.length() == 0)
                msg.obj = epc + "," + rssi;
            else
                msg.obj = epc + "," + mem + "," + rssi;
            handler.sendMessage(msg);
            lastCount++;

            if (System.currentTimeMillis() - lastTime >= 1000) {
                msg = handler.obtainMessage();
                msg.what = MSG_UPDATE_SPEED;
                msg.obj = ((lastCount * 1000) / (System.currentTimeMillis() - lastTime)) + "";
                handler.sendMessage(msg);
                lastTime = System.currentTimeMillis();
                lastCount = 0;
            }
        }

        /**
         * 盘存结束回调(Inventory Command Operate End)
         */
        @Override
        public void onInventoryTagEnd() {

            Log.d(TAG, "onInventoryTagEnd()");
            Message msg = handler.obtainMessage();
            msg.what = MSG_UPDATE_STOP;
            msg.obj = "";
            handler.sendMessage(msg);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        epc = tagList.get(position).get("tagEpc");
        tid = tagList.get(position).get("tagTid");
    }


    /**
     * 长按弹出下拉菜单 ,此事件中可弹出下拉框
     */
    View.OnCreateContextMenuListener lvjzwOnCreateContextMenuListener = new View.OnCreateContextMenuListener() {

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(getString(R.string.strtagoperate));//标题
            menu.add(0, 1, 0, getString(R.string.strreadandwrite));//下拉菜单
            menu.add(0, 2, 1, getString(R.string.strfindtag));//下拉菜单
        }
    };

    /**
     * 长按菜单响应函数 并获取选中的listview行内容
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        ContextMenu.ContextMenuInfo info = item.getMenuInfo();
        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) info;
        int position = contextMenuInfo.position;
        epc = tagList.get(position).get("tagEpc");
        tid = tagList.get(position).get("tagTid");
       if (item.getItemId() == 1) {
           MainActivity.myTabHost.setCurrentTab(2);
       } else {
           MainActivity.myTabHost.setCurrentTab(1);
       }
        return false;
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

    /**
     * 按键扫描RFID
     **/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( ( keyCode == 515 || keyCode == 523) && event.getRepeatCount()==0  ) {
            if (BtInventory.getText().equals(getString(R.string.btInventory))) {// 识别标签
                readTag();
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 按键扫描RFID
     **/
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if  ( ( keyCode == 515 || keyCode == 523) && event.getRepeatCount()==0)  {
            if (!BtInventory.getText().equals(getString(R.string.btInventory))) {// 识别标签
                readTag();
            }
        }
        return super.onKeyUp(keyCode, event);
    }


}
