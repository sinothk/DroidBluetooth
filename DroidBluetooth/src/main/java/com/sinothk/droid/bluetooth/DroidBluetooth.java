package com.sinothk.droid.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;


/**
 * https://www.cnblogs.com/lwkdbk/p/9939643.html
 */
public class DroidBluetooth {

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
}
