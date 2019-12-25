package com.sinothk.droid.bluetooth.demo;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected void onDestroy(){
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

                Log.e("bluetoothReceiver", "设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n");

//                deviceName.add("设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n");//将搜索到的蓝牙名称和地址添加到列表。
//                arrayList.add(device.getAddress());//将搜索到的蓝牙地址添加到列表。
//                adapter.notifyDataSetChanged();//更新
            }
        }
    };
}
