package com.sinothk.droid.bluetooth.demo.base;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.sinothk.droid.bluetooth.demo.inter.PinBlueCallBack;
import com.sinothk.droid.bluetooth.demo.inter.ScanBlueCallBack;

import java.lang.reflect.Method;

public abstract class BluetoothBaseActivity extends AppCompatActivity implements PinBlueCallBack, ScanBlueCallBack {

    private String TAG = "BluetoothBaseActivity";
    BroadcastReceiver scanBlueReceiver;
    BroadcastReceiver pinBlueReceiver;

    protected void onDestroy() {
        super.onDestroy();
        //解除注册
        if (scanBlueReceiver != null) {
            unregisterReceiver(scanBlueReceiver);
        }
        if (pinBlueReceiver != null) {
            unregisterReceiver(pinBlueReceiver);
        }
    }

    protected void initData() {
        scanBlueReceiver = new ScanBlueReceiver(this);
        pinBlueReceiver = new PinBlueBroadcastReceiver(this);

        //注册广播接收信号
        IntentFilter filter1 = new IntentFilter(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter filter2 = new IntentFilter(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //用BroadcastReceiver 来取得结果
        registerReceiver(scanBlueReceiver, filter1);
        registerReceiver(scanBlueReceiver, filter2);
        registerReceiver(scanBlueReceiver, filter3);

        //
        IntentFilter filter4 = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
        IntentFilter filter5 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(pinBlueReceiver, filter4);
        registerReceiver(pinBlueReceiver, filter5);
    }


    private class ScanBlueReceiver extends BroadcastReceiver {

        private ScanBlueCallBack callBack;

        public ScanBlueReceiver(ScanBlueCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action:" + action);
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.d(TAG, "开始扫描...");
                    callBack.onScanStart();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.d(TAG, "结束扫描...");
                    callBack.onScanFinish();
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    Log.d(TAG, "发现设备...");
                    callBack.onScanning(device);

                    assert device != null;
                    Log.e("bluetoothReceiver", "设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n");

                    break;
            }
        }
    }

    /**
     * 配对广播接收类
     * Created by zqf on 2018/7/7.
     */
    private class PinBlueBroadcastReceiver extends BroadcastReceiver {

        private String pin = "0000";  //此处为你要连接的蓝牙设备的初始密钥，一般为1234或0000
        //        private static final String TAG = PinBlueReceiver.class.getName();
        private PinBlueCallBack callBack;

        public PinBlueBroadcastReceiver(PinBlueCallBack callBack) {
            this.callBack = callBack;
        }

        //广播接收器，当远程蓝牙设备被发现时，回调函数onReceiver()会被执行
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action:" + action);
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device == null) {
                return;
            }

            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                try {
                    callBack.onBondRequest();

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
                        callBack.onBondCancel(device);
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        Log.d(TAG, "配对中");
                        callBack.onBonding(device);
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.d(TAG, "配对成功");
                        callBack.onBondSuccess(device);
                        break;
                }
            }
        }
    }
}
