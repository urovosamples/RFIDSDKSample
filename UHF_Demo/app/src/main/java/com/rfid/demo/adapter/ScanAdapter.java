package com.rfid.demo.adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;


import com.rfid.demo.R;

import java.util.List;

public class ScanAdapter extends RecyclerAdapter<BluetoothDevice> {

    private int opened = -1;
    private OnClickItem mOnClickItem;


    public ScanAdapter(Context context,  List<BluetoothDevice> datas) {
        super(context, R.layout.item_scan, datas);
    }

    public void setOnClickItem(OnClickItem onClickItem){
        mOnClickItem = onClickItem;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void convert(RecyclerViewHolder hepler, BluetoothDevice device) {
        TextView rssi = hepler.getView(R.id.tv_rssi);
        TextView name = hepler.getView(R.id.tv_name);
        TextView address = hepler.getView(R.id.tv_address);



//        BleDevice device = rssiDevice.getDevice();
//        rssi.setText(String.format("%ddBm", device.ge()));
        if (TextUtils.isEmpty(device.getName())){
            name.setText("Unknown");
        }else {
            name.setText(device.getName());
        }
        address.setText(device.getAddress());

        int deviceType = device.getType();
        String type;
        switch (deviceType){
            case 1:
                type = "Device type: Classic";
                break;
            case 2:
                type = "Device type: LE only";
                break;
            case 3:
                type = "Device type: Classic and LE only";
                break;
            default:
                type = "Device type: UNKNOWN";
                break;
        }

        hepler.getView(R.id.tv_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickItem.item(hepler.getAdapterPosition());
            }
        });

        hepler.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(opened == hepler.getAdapterPosition()) {
                    //当点击的item已经被展开了, 就关闭.
                    opened = -1;
                    notifyItemChanged(hepler.getAdapterPosition());
                }else {
                    int oldOpened = opened;
                    opened = hepler.getAdapterPosition();
                    notifyItemChanged(oldOpened);
                    notifyItemChanged(opened);
                }
            }
        });

    }

    public interface OnClickItem{
        void item(int position);
    }

}
