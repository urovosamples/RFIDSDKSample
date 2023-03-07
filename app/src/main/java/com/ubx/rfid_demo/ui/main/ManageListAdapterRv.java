package com.ubx.rfid_demo.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.ubx.rfid_demo.R;
import com.ubx.rfid_demo.pojo.TagManage;

import java.util.List;

public class ManageListAdapterRv extends RecyclerView.Adapter<ManageListAdapterRv.ViewHolder> {

    private List<TagManage> data;
    private Context context;
    private onItemSelectedListener onItemSelectedListener;
    private int currentItem = -1;
    private int temp = -1;

    public ManageListAdapterRv(List<TagManage> data, Context context) {
        this.data = data;
        this.context = context;
    }

    public List<TagManage> getData() {
        return data;
    }

    public void setData(List<TagManage> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public ManageListAdapterRv.onItemSelectedListener getOnItemSelectedListener() {
        return onItemSelectedListener;
    }

    public void setOnItemSelectedListener(ManageListAdapterRv.onItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    @Override
    public ManageListAdapterRv.ViewHolder onCreateViewHolder(  ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(context).inflate(R.layout.tag_manage_item, parent, false);

        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(final ManageListAdapterRv.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.refreshView(position, data.get(position));
        holder.itemView.setSelected(holder.getLayoutPosition() == currentItem);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.itemView.setSelected(true);
                temp = currentItem;
                currentItem = holder.getLayoutPosition();
                notifyItemChanged(temp);
                onItemSelectedListener.onItemSelected(holder.itemView, position, data.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return null != data ? data.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        TextView manageEpcText;
        TextView managePcText ;
        TextView manageDataText;
        TextView manageCrcText;
        public ViewHolder( View view) {
            super(view.getRootView());
            this.mView = view;
              manageEpcText = mView.findViewById(R.id.manage_epc_text);
              managePcText = mView.findViewById(R.id.manage_pc_text);
              manageDataText = mView.findViewById(R.id.manage_data_text);
              manageCrcText = mView.findViewById(R.id.manage_crc_text);
        }

        private void refreshView(int position, TagManage data) {
            Log.d("usdk", "refreshView: data = " + data + ", i = " + position);
             manageEpcText.setText(data.getEpc());
             managePcText.setText(data.getPc());
             manageDataText.setText(data.getData());
             manageCrcText.setText(data.getCrc());
        }
    }

    interface onItemSelectedListener {

        void onItemSelected(View v, int position, TagManage data);

    }
}
