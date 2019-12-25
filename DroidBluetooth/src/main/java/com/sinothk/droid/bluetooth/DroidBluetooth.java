package com.sinothk.droid.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.Method;


/**
 * https://www.cnblogs.com/lwkdbk/p/9939643.html
 */
public class DroidBluetooth {
    private static String TAG = "DroidBluetooth";
    private static BluetoothAdapter blueAdapter;
    private static boolean support;

    /**
     * 初始化
     *
     * @param baseContext
     */
    public static void init(Context baseContext) {
        //获取蓝牙适配器
        blueAdapter = BluetoothAdapter.getDefaultAdapter();
        //表示手机支持蓝牙
        support = blueAdapter != null;
    }

    /**
     * 判断设备是否支持蓝牙
     *
     * @return
     */
    public static boolean isSupport() {
        return support;
    }

    /**
     * 打开蓝牙
     */
    public static void open() {
        //判断本机蓝牙是否打开
        if (!blueAdapter.isEnabled()) {//如果没打开，则打开蓝牙
            blueAdapter.enable();
        }
    }

    /**
     * 关闭蓝牙
     */
    public static void close() {
        //判断本机蓝牙是否打开
        if (blueAdapter.isEnabled()) {//如果没打开，则打开蓝牙
            blueAdapter.disable();
        }
    }

    /**
     * 判断蓝牙是否打开
     *
     * @return
     */
    public static boolean isOpen() {
        return blueAdapter.isEnabled();
    }

    /**
     * 设置多少秒内可见
     *
     * @param mContext
     * @param timeSec
     */
    public static void setVisible(Context mContext, int timeSec) {
        if (blueAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            //不在可被搜索的范围
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, timeSec);//设置本机蓝牙在300秒内可见
            mContext.startActivity(discoverableIntent);
        }
    }

    public static void doDiscover() {
        if (!blueAdapter.isDiscovering()) {
            blueAdapter.startDiscovery();
        }
    }


    public static void cancelDiscover() {
        if (blueAdapter.isDiscovering()) {
            //判断蓝牙是否正在扫描，如果是调用取消扫描方法；如果不是，则开始扫描
            blueAdapter.cancelDiscovery();
        }
    }

    /**
     * 判断蓝牙是否打开
     *
     * @return
     */
    public static boolean isBond(BluetoothDevice device) {
        return device.getBondState() == BluetoothDevice.BOND_NONE;
    }

    /**
     * 配对（配对成功与失败通过广播返回）
     *
     * @param device
     */
    public static void pin(BluetoothDevice device) {
        if (device == null) {
            Log.e(TAG, "bond device null");
            return;
        }
        if (!isOpen()) {
            Log.e(TAG, "Bluetooth 没打开!");
            return;
        }
        //配对之前把扫描关闭
        if (blueAdapter.isDiscovering()) {
            blueAdapter.cancelDiscovery();
        }
        //判断设备是否配对，没有配对在配，配对了就不需要配了
        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            Log.d(TAG, "attemp to bond:" + device.getName());
            try {
                Method createBondMethod = device.getClass().getMethod("createBond");
                Boolean returnValue = (Boolean) createBondMethod.invoke(device);
                Log.e(TAG, "匹配结果：" + returnValue);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "匹配结果：异常");
            }
        }
    }


    /**
     * 取消配对（取消配对成功与失败通过广播返回 也就是配对失败）
     *
     * @param device
     */
    public void cancelPinBule(BluetoothDevice device) {
        if (device == null) {
            Log.d(TAG, "cancel bond device null");
            return;
        }
        if (!isOpen()) {
            Log.e(TAG, "Bluetooth not enable!");
            return;
        }
        //判断设备是否配对，没有配对就不用取消了
        if (device.getBondState() != BluetoothDevice.BOND_NONE) {
            Log.d(TAG, "attemp to cancel bond:" + device.getName());
            try {
                Method removeBondMethod = device.getClass().getMethod("removeBond");
                Boolean returnValue = (Boolean) removeBondMethod.invoke(device);
                returnValue.booleanValue();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e(TAG, "attemp to cancel bond fail!");
            }
        }
    }
}
