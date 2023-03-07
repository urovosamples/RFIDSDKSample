package com.ubx.rfid_demo.ui.main;

import android.os.Bundle;


import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ubx.rfid_demo.BaseApplication;
import com.ubx.rfid_demo.MainActivity;
import com.ubx.rfid_demo.R;
import com.ubx.rfid_demo.pojo.ManageFormInfo;
import com.ubx.rfid_demo.pojo.TagManage;
import com.ubx.rfid_demo.pojo.TagScan;

import com.ubx.usdk.rfid.aidl.IRfidCallback;
import com.ubx.usdk.rfid.aidl.RfidDate;
import com.ubx.usdk.rfid.util.CMDCode;
import com.ubx.usdk.rfid.util.ErrorCode;
import com.ubx.usdk.util.SoundTool;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TagManageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TagManageFragment extends Fragment {

    public static final String TAG = "usdk-"+TagManageFragment.class.getSimpleName();
    private ManageFormInfo formInfo;

    /**
     * (0x00:RESERVED, 0x01:EPC, 0x02:TID, 0x03:USER)
     */
    private int btMemBank;
    private ManageListAdapterRv manageListAdapterRv;
    private Callback callback;
    private static MainActivity mActivity;
    private ArrayAdapter epcArrayAdapter;
    private List<TagManage> data;
    private HashMap<String, TagManage> map = new HashMap<>();

    private RecyclerView manageListRv;
    private Spinner manageBankSpinner,manageEpcDatasSpinner;
    private TextView tvChoiceEpcTid;

    private String mEpc ="";
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TagManageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TagManageFragment newInstance(MainActivity activity) {
        mActivity = activity;
        TagManageFragment fragment = new TagManageFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tag_manage, container, false);
    }

    private EditText manageWriteEdit,manageCntEdit,manageAddressEdit,managePasswordEdit;
    private Button manageReadBtn,manageWriteBtn;

    @Override
    public void onViewCreated(  View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        manageListRv = view.findViewById(R.id.manage_list_rv);
        manageBankSpinner = view.findViewById(R.id.manage_bank_spinner);
        manageEpcDatasSpinner = view.findViewById(R.id.manage_epc_datas_spinner );

        manageCntEdit = view.findViewById(R.id.manage_cnt_edit);
                manageAddressEdit= view.findViewById(R.id.manage_address_edit);
        managePasswordEdit= view.findViewById(R.id.manage_password_edit);



        tvChoiceEpcTid = view.findViewById(R.id.tv_choice_epc_tid);
        manageWriteEdit = view.findViewById(R.id.manage_write_edit);

        manageReadBtn  = view.findViewById(R.id.manage_read_btn);
        manageWriteBtn= view.findViewById(R.id.manage_write_btn);

         manageListRv.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
         manageListRv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        manageListAdapterRv = new ManageListAdapterRv(null, getActivity());
         manageListRv.setAdapter(manageListAdapterRv);

        initEvents();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initEvents() {
        manageBankSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        btMemBank = 0x00;
                        break;
                    case 1:
                        btMemBank = 0x01;
                        break;
                    case 2:
                        btMemBank = 0x02;
                        break;
                    case 3:
                        btMemBank = 0x03;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
         manageBankSpinner.setSelection(1, true);






        epcArrayAdapter =new ArrayAdapter(mActivity,android.R.layout.simple_spinner_dropdown_item,mActivity.tagScanSpinner){
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(getActivity(),R.layout.spinner_epc_tid_item,null);//获得Spinner布局View
                if(convertView!=null)
                {
                    TextView tvEpc =  convertView.findViewById(R.id.sp_epc_item);
                    TextView tvTid =  convertView.findViewById(R.id.sp_tid_item);
                    try
                    {
                        String epc = mActivity.tagScanSpinner.get(position).getEpc();
                        String tid = mActivity.tagScanSpinner.get(position).getTid();
                        tvEpc.setText("EPC:"+epc);
                        tvTid.setText("TID:"+tid);
                    }catch (Exception e){}

                }
                return convertView;
            }
        };
//给Spinner set适配器
         manageEpcDatasSpinner.setAdapter(epcArrayAdapter);
         manageEpcDatasSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override//重写Item被选择的事件
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                List<TagScan> datas = mActivity.tagScanSpinner;
                if (datas!=null && datas.size()>0){
                    TagScan tagScan = datas.get(position);
                    String tid =  tagScan.getTid().replace(" ", "");
                    if (!TextUtils.isEmpty(tid)){

                    }else {

                    }

                    String epc =  tagScan.getEpc().replace(" ", "");
                    mEpc = epc;


//                    byte[] bytes = hexStringToBytes(epc);
//                    manageWriteEdit.setText(epc);
//                    Log.d(TAG, "initEvents: 选中标签：epc or tid："+epc);
//                     tvChoiceEpcTid.setText(epc);
//                    mActivity.mRfidManager.setAccessEpcMatch( (byte) bytes.length, bytes);



                }


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


         tvChoiceEpcTid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 manageEpcDatasSpinner.performClick();
            }
        });





//        //创建适配器，并设置给spinner2
//        epcArrayAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,mActivity.mDataParents);
//        binding.manageEpcDatasSpinner.setAdapter(epcArrayAdapter);
//        binding.manageEpcDatasSpinner.setSelection(0, true);
//        binding.manageEpcDatasSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//              List<String> datas = mActivity.mDataParents;
//              if (datas!=null && datas.size()>0){
//                String epc =  datas.get(position).replace(" ", "");
//                byte[] bytes = hexStringToBytes(epc);
//                binding.manageWriteEdit.setText(epc);
//                  Log.d(TAG, "initEvents: 选中标签：epc："+epc);
//               mActivity.mRfidManager.setAccessEpcMatch(mActivity.mRfidManager.getReadId(), (byte) bytes.length, bytes);
//              }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


        manageListAdapterRv.setOnItemSelectedListener(new ManageListAdapterRv.onItemSelectedListener() {
            @Override
            public void onItemSelected(View v, int position, TagManage data) {

                manageWriteEdit.setText(data.getData());
                byte[] bytes = hexStringToBytes(data.getEpc());
                mActivity.mRfidManager.setAccessEpcMatch( (byte) bytes.length, bytes);


            }
        });
         manageReadBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Log.d(TAG, "initEvents: 读标签");
                 map.clear();
                 setCallback();

                 byte cnt = Integer.valueOf(manageCntEdit.getText().toString()).byteValue();
                 byte address = Integer.valueOf(manageAddressEdit.getText().toString()).byteValue();
                 String strPwd = managePasswordEdit.getText().toString();
                 byte[] pwd = hexStringToBytes(strPwd);
                 Log.d(TAG, "initEvents: cnt = " + cnt + ", address = " + address +", strPwd = " + Arrays.toString(hexStringToBytes(strPwd))+", pwd" + Arrays.toString(pwd));
                 formInfo = new ManageFormInfo(cnt, address, (byte) btMemBank, pwd, null);
                 String dataRead = mActivity.mRfidManager.readTag(mEpc, (byte) btMemBank, address, cnt, pwd);

                 if (TextUtils.isEmpty(dataRead)){
                     toast(getString(R.string.read_tag_fail));
                 }else {
                     toast(getString(R.string.read_tag_success));
                     readTag(dataRead,"");
                 }


             }
         });
        manageWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "initEvents: 写标签");
                map.clear();
                setCallback();
                byte[] pwd = hexStringToBytes(managePasswordEdit.getText().toString());
                int add = Integer.parseInt(manageAddressEdit.getText().toString());
                int cnt = Integer.parseInt(manageCntEdit.getText().toString());
                String dataEd = manageWriteEdit.getText().toString();

                if (TextUtils.isEmpty(dataEd)){
                    Toast.makeText(mActivity,getString(R.string.write_epc_first),Toast.LENGTH_SHORT).show();
                    return;
                }
                String data = dataEd.replaceAll(" ","");

                if (data.length() % 4 != 0) {//TODO data 长度不够4的倍数，后面自动补0
                    int less = data.length() % 4;
                    for (int i = 0; i < 4 - less; i++) {
                        data = data + "0";
                    }
                }

                //                    mActivity.mRfidManager.writeTagByTid(mActivity.mRfidManager.getReadId(),)

                if (add == 2 && btMemBank == 1){//TODO 如果是EPC、又是从地址2开始写，那么需要修改PC,修改PC值即标签盘存时，返回的长度就是这次写入的长度，如果不需要修改标签读到的内容长度，那么用 else 逻辑即可

                    String epc = data;
                    String pc = getPC(epc);
                    String EPC_Actual = pc + epc ;//把PC值也加入到待写入的内容
                    int EPC_Actual_Length = EPC_Actual.length() / 4;//再次计算 PC+EPC的长度（实际要写入的长度）
                    byte cnt_Actual = (byte) EPC_Actual_Length;
                    byte add_Actual = (byte) 1;//要改变EPC内容长度，需要从地址1开始写入
                    byte[] btAryData = hexStringToBytes(EPC_Actual);//实际写入内容为PC+EPC值

                    Log.d(TAG,  "writeTag() --> pwd = " + Arrays.toString(pwd)
                            + "; btMemBank = " + btMemBank
                            + "; add_Actual = " + add_Actual
                            + "; cnt_Actual = " + cnt_Actual
                            + "; btAryData" + Arrays.toString(btAryData));
                    Log.d(TAG, "initEvents: 写标签   EPC_Actual:"+EPC_Actual);
                  int ret =  mActivity.mRfidManager.writeTag(mEpc, pwd,(byte) btMemBank, add_Actual,cnt_Actual, btAryData);
                    if (ret == 0){
                        toast(getString(R.string.write_tag_success));
                        write(EPC_Actual,"");
                    }else {
                        toast(getString(R.string.write_tag_fail)+ret);
                    }

                }else {//TODO 下面逻辑不会改变盘存时读到的EPC内容长度

                    byte[] btAryData = hexStringToBytes(data);
                    Log.d(TAG,  "writeTag() --> pwd = " + Arrays.toString(pwd)
                            + "; btMemBank = " + btMemBank
                            + "; add = " + add
                            + "; cnt = " + cnt
                            + "; btAryData" + Arrays.toString(btAryData));
                    int ret =   mActivity.mRfidManager.writeTag(mEpc, pwd, (byte) btMemBank, (byte) add, (byte) cnt, btAryData);
                    if (ret == 0){
                        toast(getString(R.string.write_t_s));
                        write(data,"");
                    }else {
                        toast(getString(R.string.write_t_f)+ret);
                    }
                }
            }
        });

    }
    private void readTag(final String epc, final String tid){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                {
                    if (!map.containsKey(epc)) {
                        TagManage tagManage = new TagManage(epc, "", tid, "", false);
                        map.put(epc, tagManage);
                    } else {
                        TagManage tagManage = map.get(epc);
                        tagManage.setData(tid);
                        map.put(epc, tagManage);
                    }
                    data = new ArrayList<>(map.values());
                    Log.d(TAG, "onOperationTag: data = " + Arrays.toString(data.toArray()));
                    manageListAdapterRv.setData(data);
                    SoundTool.getInstance(BaseApplication.getContext()).playBeep(1);

                }
            }
        });
    }

    private void write(final String epc, final String tid){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                {
                    if (!map.containsKey(epc)) {
                        TagManage tagManage = new TagManage(epc, "", tid, "", false);
                        map.put(epc, tagManage);
                    } else {
                        TagManage tagManage = map.get(epc);
                        tagManage.setData(tid);
                        map.put(epc, tagManage);
                    }
                    data = new ArrayList<>(map.values());
                    Log.d(TAG, "onOperationTag: data = " + Arrays.toString(data.toArray()));
                    manageListAdapterRv.setData(data);
                    SoundTool.getInstance(BaseApplication.getContext()).playBeep(1);

                }
            }
        });
    }

    /**
     * 获取 PC值
     * @param epc
     * @return
     */
   private String getPC(String epc){
        String pc ="0000";
        int len = epc.length()/4;
        int b = len << 11;
        String aHex = Integer.toHexString(b);
        if (aHex.length() == 3){
            pc = "0"+aHex;
        } else {
            pc = aHex;
        }
        return pc;
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




    class Callback implements IRfidCallback {

        @Override
        public void onInventoryTag(String EPC, String TID, String strRSSI) {

        }

        @Override
        public void onInventoryTagEnd()  {
        }

    }

    private void toast(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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
            epcArrayAdapter.notifyDataSetChanged();
            setCallback();
        }
    }
    private void setCallback(){
        if (mActivity.RFID_INIT_STATUS) {
            if (mActivity.mRfidManager!=null) {
                if (callback == null){
                    callback = new Callback();
                }
                    mActivity.mRfidManager.registerCallback(callback);
            }
        }
    }

}