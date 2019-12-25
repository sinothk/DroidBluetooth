package com.sinothk.droid.bluetooth.demo;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import com.sinothk.droid.bluetooth.demo.inter.ConnectBlueCallBack;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * 连接线程
 * Created by zqf on 2018/7/7.
 */

public class ConnectBlueTask extends AsyncTask<BluetoothDevice, Integer, BluetoothSocket> {
    private static final String TAG = ConnectBlueTask.class.getName();
    private BluetoothDevice device;
    private ConnectBlueCallBack callBack;

    public ConnectBlueTask(ConnectBlueCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected BluetoothSocket doInBackground(BluetoothDevice... bluetoothDevices) {
        device = bluetoothDevices[0];
        BluetoothSocket socket = null;
//        try{
//
//            String uuid = "00001101-0000-1000-8000-00805F9B34FB";//.toString().replaceAll("-","");
//            Log.d(TAG,"开始连接socket, uuid:" + uuid);
//            socket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
//            if (socket != null && !socket.isConnected()){
//                socket.connect();
//            }
//        }catch (IOException e){
//            Log.e(TAG,"socket连接失败");
//            try {
//                socket.close();
//            } catch (IOException e1) {
//                e1.printStackTrace();
//                Log.e(TAG,"socket关闭失败");
//            }
//        }
//        return socket;
        try {
            socket = (BluetoothSocket) device.getClass().getDeclaredMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);

            if (socket != null && !socket.isConnected()) {
                socket.connect();
                return socket;
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "开始连接");
        if (callBack != null) callBack.onStartConnect();
    }

    @Override
    protected void onPostExecute(BluetoothSocket bluetoothSocket) {
        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
            Log.d(TAG, "连接成功");
            if (callBack != null) callBack.onConnectSuccess(device, bluetoothSocket);
        } else {
            Log.d(TAG, "连接失败");
            if (callBack != null) callBack.onConnectFail(device, "连接失败");
        }
    }
}
