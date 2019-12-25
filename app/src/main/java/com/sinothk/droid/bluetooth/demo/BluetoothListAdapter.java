package com.sinothk.droid.bluetooth.demo;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sinothk.droid.bluetooth.demo.inter.OnEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BluetoothListAdapter extends RecyclerView.Adapter<BluetoothListAdapter.BluetoothViewHolder> {

    Context mContext;
    ArrayList<BluetoothDevice> dataList;
    OnEventListener eventListener;

    BluetoothListAdapter(Context context) {
        mContext = context;
        dataList = new ArrayList<>();
    }

    void setDataList(ArrayList<BluetoothDevice> list) {
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    @NotNull
    @Override
    public BluetoothViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BluetoothViewHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_main_bluetooth_search_list_item, null, false));
    }

    @Override
    public void onBindViewHolder(BluetoothViewHolder holder, int position) {
        final BluetoothDevice bluetoothEntity = dataList.get(position);

        holder.nameTv.setText(bluetoothEntity.getName());
        holder.addressTv.setText(bluetoothEntity.getAddress());

        holder.rootItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventListener.callback(0, bluetoothEntity);
            }
        });

        holder.rootItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                eventListener.callback(1, bluetoothEntity);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class BluetoothViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rootItem;
        TextView nameTv, addressTv;

        public BluetoothViewHolder(View view) {
            super(view);
            rootItem = view.findViewById(R.id.rootItem);
            nameTv = view.findViewById(R.id.nameTv);
            addressTv = view.findViewById(R.id.addressTv);
        }
    }

    public void setEventListener(OnEventListener eventListener) {
        this.eventListener = eventListener;
    }
}
