package com.sinothk.droid.bluetooth.demo;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinothk.droid.bluetooth.DroidBluetooth;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class BluetoothSearchDemoActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button searchBluetoothBtn, closeSearchBluetoothBtn;

    ArrayList<BluetoothDevice> list = new ArrayList<>();
    BluetoothListAdapter adapter;

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
            public void callback(BluetoothDevice bluetoothEntity) {
                try {
                    connect(bluetoothEntity);
                } catch (NoSuchMethodException | IOException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void connect(BluetoothDevice device) throws NoSuchMethodException, IOException, InvocationTargetException, IllegalAccessException {
        DroidBluetooth.cancelDiscover();
        final BluetoothSocket socket = (BluetoothSocket) device.getClass().getDeclaredMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);

        assert socket != null;
        socket.connect();//连接
    }

    protected void onDestroy() {
        super.onDestroy();//解除注册
        unregisterReceiver(bluetoothReceiver);
    }

    protected void initData() {
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);//注册广播接收信号
        registerReceiver(bluetoothReceiver, intentFilter);//用BroadcastReceiver 来取得结果
    }


    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                assert device != null;
                Log.e("bluetoothReceiver", "设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n");

//                deviceName.add("设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n");//将搜索到的蓝牙名称和地址添加到列表。
//                arrayList.add(device.getAddress());//将搜索到的蓝牙地址添加到列表。
//                adapter.notifyDataSetChanged();//更新

                list.add(device);
                adapter.setDataList(list);
            }
        }
    };
}
