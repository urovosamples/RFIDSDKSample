package com.ubx.rfid_demo.ui.main;

import android.os.Bundle;


import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ubx.rfid_demo.MainActivity;
import com.ubx.rfid_demo.R;
import com.ubx.usdk.bean.CustomRegionBean;
import com.ubx.usdk.rfid.aidl.IRfidCallback;
import com.ubx.usdk.rfid.aidl.RfidDate;
import com.ubx.usdk.rfid.util.CMDCode;
import com.ubx.usdk.rfid.util.ErrorCode;
import com.ubx.usdk.rfid.util.RRErrorCode;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {
    public static final String TAG = "usdk-"+SettingFragment.class.getSimpleName();


    private Button btnGetPower,btnSetPower,btnGetDeviceID,btnCfFrency,btnGetFrency,btnCfMask,btnGetMask,btnSetRange,btnGetRange;
    private EditText etSetPower,edtDeviceID,edtStartFrency,edtFrencyStep,edtFrencyNumber,edtMaskArea,edtMaskStartAddr,edtMaskLen,edtMaskData,edtRange;
    private TextView tvGetPower;

    public static SettingFragment newInstance(MainActivity activity) {
        mActivity = activity;
        return new SettingFragment();
    }

    private Callback callback  = new Callback();
    private static MainActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(  View view,   Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnGetPower = view.findViewById(R.id.btn_getPower);
        btnSetPower = view.findViewById(R.id.btn_setPower);
        etSetPower = view.findViewById(R.id.et_setPower);
        tvGetPower = view.findViewById(R.id.tv_getPower);
        btnGetDeviceID=view.findViewById(R.id.btn_deviceID);
        edtDeviceID=view.findViewById(R.id.et_deviceID);

        edtStartFrency=view.findViewById(R.id.edt_startFrency);
        edtFrencyStep=view.findViewById(R.id.edt_frencyStep);
        edtFrencyNumber=view.findViewById(R.id.edt_frencyNumber);
        btnCfFrency=view.findViewById(R.id.btn_configFrency);
        btnGetFrency=view.findViewById(R.id.btn_getFrency);

        edtMaskArea=view.findViewById(R.id.edt_maskArea);
        edtMaskStartAddr=view.findViewById(R.id.edt_maskStartAddr);
        edtMaskLen=view.findViewById(R.id.edt_maskLength);
        edtMaskData=view.findViewById(R.id.edt_maskData);
        btnCfMask=view.findViewById(R.id.btn_configMask);
        btnGetMask=view.findViewById(R.id.btn_getMask);

        edtRange=view.findViewById(R.id.edt_rangeValue);
        btnGetRange=view.findViewById(R.id.btn_getRange);
        btnSetRange=view.findViewById(R.id.btn_setRange);
        initEvents();
    }

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
    private void initEvents() {
         btnGetPower.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 setCallback();
                 int outputPower = mActivity.mRfidManager.getOutputPower();
                 tvGetPower.setText(getString(R.string.current_power)+outputPower);

             }
         });

         btnSetPower.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 setCallback();
                 String str =  etSetPower.getText().toString().trim();
                 if (TextUtils.isEmpty(str)) {
                     Toast.makeText(getActivity(), getString(R.string.content_not_null), Toast.LENGTH_SHORT).show();
                     return;
                 }
                 int power = Integer.parseInt(str);
                 if (power<0 || power>33) {
                     Toast.makeText(getActivity(), getString(R.string.power_value_not_allow), Toast.LENGTH_SHORT).show();
                     return;
                 }

                  int ret =   mActivity.mRfidManager.setOutputPower( (byte) power);
                     if (ret !=0){
                         Toast.makeText(getActivity(), getString(R.string.set_power_fail), Toast.LENGTH_SHORT).show();
                     }


                 }
         });
         btnGetDeviceID.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(mActivity.mRfidManager!=null);{
                     String deviceId = mActivity.mRfidManager.getDeviceId();
                     edtDeviceID.setText(deviceId+"");
                 }
             }
         });


         btnCfFrency.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String StartFrency = edtStartFrency.getText().toString();
                 String step = edtFrencyStep.getText().toString();
                 String number = edtFrencyNumber.getText().toString();
                 if(StartFrency.equals("")||step.equals("")||number.equals(""))
                     return;
                 if(mActivity.mRfidManager==null)
                     return;
                 int i = mActivity.mRfidManager.setCustomRegion((byte) 0, 0xff, Integer.parseInt(step), Integer.parseInt(number), Integer.parseInt(StartFrency));
                 if(i==0){
                     Toast.makeText(getContext(), getContext().getString(R.string.set_success),Toast.LENGTH_SHORT).show();
                 }else{
                     Toast.makeText(getContext(), getContext().getString(R.string.set_fail),Toast.LENGTH_SHORT).show();
                 }
             }
         });
         btnGetFrency.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(mActivity.mRfidManager==null)
                     return;
                 CustomRegionBean customRegion = mActivity.mRfidManager.getCustomRegion();
                 if(customRegion!=null){
                     edtStartFrency.setText(customRegion.StartFre[0]+"");
                     edtFrencyNumber.setText(customRegion.FreNum[0]+"");
                     edtFrencyStep.setText(customRegion.FreSpace[0]+"");

                 }else{
                     Toast.makeText(getContext(), getContext().getString(R.string.get_fail),Toast.LENGTH_SHORT).show();
                 }
             }
         });
         btnCfMask.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String area = edtMaskArea.getText().toString().trim();
                 String startAddr = edtMaskStartAddr.getText().toString().trim();
                 String len = edtMaskLen.getText().toString().trim();
                 String data = edtMaskData.getText().toString().trim();
                 if(area.equals("")||startAddr.equals("")||len.equals("")||data.equals("")){
                     return;
                 }
                 if(mActivity.mRfidManager==null)
                     return;
                 mActivity.mRfidManager.addMask(Integer.parseInt(area),Integer.parseInt(startAddr),Integer.parseInt(len),data);

                 Toast.makeText(getContext(),getContext().getString(R.string.set_success),Toast.LENGTH_SHORT).show();

             }
         });
         btnGetMask.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(mActivity.mRfidManager!=null){
                     mActivity.mRfidManager.clearMask();
                 }

             }
         });

         btnSetRange.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String range = edtRange.getText().toString();
                 if(range.equals("")){
                     return;
                 }
                 if(mActivity.mRfidManager==null){
                     return;
                 }
                 int i = mActivity.mRfidManager.setRange(Integer.parseInt(range));
                 if(i==0){
                     Toast.makeText(getContext(),getContext().getString(R.string.set_success),Toast.LENGTH_SHORT).show();
                 }else{
                     Toast.makeText(getContext(),getContext().getString(R.string.set_fail),Toast.LENGTH_SHORT).show();
                 }
             }
         });
         btnGetRange.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(mActivity.mRfidManager==null){
                     return;
                 }

                 int range1 = mActivity.mRfidManager.getRange();
                 if(range1!=-1){
                     edtRange.setText(range1+"");
                 }else{
                     Toast.makeText(getContext(),getContext().getString(R.string.get_fail),Toast.LENGTH_SHORT).show();
                 }
             }
         });
    }

    private class Callback implements IRfidCallback {


        @Override
        public void onInventoryTag(String EPC, String TID, String strRSSI) {

        }

        @Override
        public void onInventoryTagEnd()  {

        }
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

    private void setCallback(){
        if (mActivity.RFID_INIT_STATUS) {
            if (mActivity.mRfidManager!=null) {
                if (callback == null) {
                    callback = new Callback();
                }
                    mActivity.mRfidManager.registerCallback(callback);
            }
        }
    }
}