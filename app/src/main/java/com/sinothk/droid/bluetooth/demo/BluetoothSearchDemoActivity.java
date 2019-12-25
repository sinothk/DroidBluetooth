package com.sinothk.droid.bluetooth.demo;

import android.bluetooth.BluetoothAdapter;
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
import java.lang.reflect.Method;
import java.util.ArrayList;

public class BluetoothSearchDemoActivity extends AppCompatActivity {
    private String TAG = "BluetoothSearchDemoActivity";
    RecyclerView recyclerView;
    Button searchBluetoothBtn, closeSearchBluetoothBtn;
    ConnectBlueTask connectBlueTask;

    ArrayList<BluetoothDevice> list = new ArrayList<>();
    BluetoothListAdapter adapter;

    protected void onDestroy() {
        super.onDestroy();//解除注册
        unregisterReceiver(bluetoothReceiver);
    }

    protected void initData() {
        //注册广播接收信号
        IntentFilter filter1 = new IntentFilter(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter filter2 = new IntentFilter(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //用BroadcastReceiver 来取得结果
        registerReceiver(bluetoothReceiver, filter1);
        registerReceiver(bluetoothReceiver, filter2);
        registerReceiver(bluetoothReceiver, filter3);


        IntentFilter filter4 = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
        IntentFilter filter5 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(pinBlueReceiver, filter4);
        registerReceiver(pinBlueReceiver, filter5);

        connectBlueTask = new ConnectBlueTask(new ConnectBlueCallBack() {
            @Override
            public void onStartConnect() {
                Log.e("ConnectBlueTask", "action:" + "开始连接");
            }

            @Override
            public void onConnectSuccess(BluetoothDevice bluetoothDevice, BluetoothSocket bluetoothSocket) {
                Log.e("ConnectBlueTask", "action:" + "连接成功");
            }

            @Override
            public void onConnectFail(BluetoothDevice bluetoothDevice, String msg) {
                Log.e("ConnectBlueTask", "action:" + "连接失败");
            }
        });
    }


    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//                assert device != null;
//                Log.e("bluetoothReceiver", "设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n");
//
////                deviceName.add("设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n");//将搜索到的蓝牙名称和地址添加到列表。
////                arrayList.add(device.getAddress());//将搜索到的蓝牙地址添加到列表。
////                adapter.notifyDataSetChanged();//更新
//
//                list.add(device);
//                adapter.setDataList(list);
//            }

            String action = intent.getAction();
            Log.d(TAG, "action:" + action);
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.d(TAG, "开始扫描...");
//                    callBack.onScanStarted();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.d(TAG, "结束扫描...");
//                    callBack.onScanFinished();
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    Log.d(TAG, "发现设备...");
//                    callBack.onScanning(device);

                    assert device != null;
                    Log.e("bluetoothReceiver", "设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n");

//                deviceName.add("设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n");//将搜索到的蓝牙名称和地址添加到列表。
//                arrayList.add(device.getAddress());//将搜索到的蓝牙地址添加到列表。
//                adapter.notifyDataSetChanged();//更新

                    list.add(device);
                    adapter.setDataList(list);

                    break;
            }
        }
    };


    /**
     * 配对广播接收类
     * Created by zqf on 2018/7/7.
     */

    private final BroadcastReceiver pinBlueReceiver = new BroadcastReceiver() {
        private String pin = "0000";  //此处为你要连接的蓝牙设备的初始密钥，一般为1234或0000
        //        private static final String TAG = PinBlueReceiver.class.getName();
        private PinBlueCallBack callBack;

        public void setPinBlueReceiver(PinBlueCallBack callBack) {
            this.callBack = callBack;
        }

        //广播接收器，当远程蓝牙设备被发现时，回调函数onReceiver()会被执行
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action:" + action);
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                try {
//                    callBack.onBondRequest();
                    //1.确认配对
//                ClsUtils.setPairingConfirmation(device.getClass(), device, true);
                    Method setPairingConfirmation = device.getClass().getDeclaredMethod("setPairingConfirmation", boolean.class);
                    setPairingConfirmation.invoke(device, true);
                    //2.终止有序广播
                    Log.d("order...", "isOrderedBroadcast:" + isOrderedBroadcast() + ",isInitialStickyBroadcast:" + isInitialStickyBroadcast());
                    abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                    //3.调用setPin方法进行配对...
//                boolean ret = ClsUtils.setPin(device.getClass(), device, pin);
                    Method removeBondMethod = device.getClass().getDeclaredMethod("setPin", new Class[]{byte[].class});
                    Boolean returnValue = (Boolean) removeBondMethod.invoke(device, new Object[]{pin.getBytes()});
                    if (returnValue != null && returnValue) {
                        Log.d(TAG, "配对成功1");
                    } else {
                        Log.d(TAG, "配对成功2");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "配对异常");
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_NONE:
                        Log.d(TAG, "取消配对");
//                        callBack.onBondFail(device);
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        Log.d(TAG, "配对中");
//                        callBack.onBonding(device);
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.d(TAG, "配对成功");
//                        callBack.onBondSuccess(device);
                        connectBlueTask.doInBackground(device);
                        break;
                }
            }
        }
    };

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
            public void callback(BluetoothDevice bluetoothDevice) {
                DroidBluetooth.cancelDiscover();
                DroidBluetooth.pin(bluetoothDevice);
            }
        });
    }
}
