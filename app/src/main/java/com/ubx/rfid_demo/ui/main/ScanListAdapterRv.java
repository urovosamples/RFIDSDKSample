package com.ubx.rfid_demo.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.ubx.rfid_demo.R;
import com.ubx.rfid_demo.pojo.TagScan;

import java.util.List;

public class ScanListAdapterRv extends RecyclerView.Adapter<ScanListAdapterRv.ViewHolder> {

    private List<TagScan> data;
    private Context context;
    public OnClickListener onClickListener;

    public ScanListAdapterRv(List<TagScan> data, Context context) {
        this.data = data;
        this.context = context;
    }

    public void setData(List<TagScan> data) {
        this.data = data;
        if (this.data.size()>0) {
            notifyItemRangeChanged(0, data.size());
        }else {
            notifyDataSetChanged();
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(  ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(context).inflate(R.layout.tag_scan_item, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (onClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onSelectEpc(data.get(position), position);
                }
            });
        }
        holder.refreshView(position, data.get(position));
    }

    public interface OnClickListener {
        void onSelectEpc(TagScan data, int position);
    }

    @Override
    public int getItemCount() {
        return null != data ? data.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        TextView listEpcText;
        TextView listTidText ;
        TextView listTotalText;
        TextView listRssiText;
        public ViewHolder( View  view) {
            super(view.getRootView());
            this.mView = view;
            listEpcText = mView.findViewById(R.id.list_epc_text);
            listTidText = mView.findViewById(R.id.list_tid_text);
            listTotalText = mView.findViewById(R.id.list_total_text);
            listRssiText = mView.findViewById(R.id.list_rssi_text);
        }

        private void refreshView(int position, TagScan data) {
             listEpcText.setText("EPC:"+data.getEpc());
             listTidText.setText("TID:"+data.getTid());
            if (TextUtils.isEmpty(data.getTid())){
                 listTidText.setVisibility(View.GONE);
            }else {
                listTidText.setVisibility(View.VISIBLE);
            }
             listTotalText.setText(data.getCount() + "");
             listRssiText.setText(data.getRssi());

        }
    }
}
