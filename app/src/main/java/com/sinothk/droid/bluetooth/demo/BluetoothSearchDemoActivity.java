package com.sinothk.droid.bluetooth.demo;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.sinothk.droid.bluetooth.DroidBluetooth;
import com.sinothk.droid.bluetooth.demo.base.BluetoothBaseActivity;

import java.util.ArrayList;

public class BluetoothSearchDemoActivity extends BluetoothBaseActivity {

    private String TAG = "BluetoothSearchDemoActivity";
    RecyclerView recyclerView;
    Button searchBluetoothBtn, closeSearchBluetoothBtn;

    ArrayList<BluetoothDevice> list = new ArrayList<>();
    BluetoothListAdapter adapter;

    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bluetooth_search_list);

        initData();
        initView();
    }

    private void initView() {
        recyclerView = this.findViewById(R.id.recyclerView);
        searchBluetoothBtn = this.findViewById(R.id.searchBluetoothBtn);
        closeSearchBluetoothBtn = this.findViewById(R.id.closeSearchBluetoothBtn);

        searchBluetoothBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                adapter.setDataList(list);

                DroidBluetooth.doDiscover();
            }
        });

        closeSearchBluetoothBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DroidBluetooth.cancelDiscover();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new BluetoothListAdapter(this);
        recyclerView.setAdapter(adapter);

        adapter.setEventListener(new BluetoothListAdapter.EventListener() {
            @Override
            public void callback(BluetoothDevice bluetoothDevice) {
                DroidBluetooth.cancelDiscover();

                if (DroidBluetooth.isBond(bluetoothDevice)) {
                    DroidBluetooth.pin(bluetoothDevice);
                } else {
                    showMsg("已匹配");
                }
            }
        });
    }

    @Override
    public void onScanStart() {

    }

    @Override
    public void onScanFinish() {

    }

    @Override
    public void onScanning(BluetoothDevice device) {
        list.add(device);
        adapter.setDataList(list);
    }

    @Override
    public void onBondRequest() {

    }

    @Override
    public void onBonding(BluetoothDevice device) {
        showMsg("匹配中");
    }

    @Override
    public void onBondSuccess(BluetoothDevice device) {
        showMsg("匹配成功");
    }

    @Override
    public void onBondFail(BluetoothDevice device) {
        showMsg("匹配失败");
    }
}
