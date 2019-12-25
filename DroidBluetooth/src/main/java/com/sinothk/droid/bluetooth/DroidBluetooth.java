package com.sinothk.droid.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

public class DroidBluetooth {

    private static BluetoothAdapter blueadapter;
    private static boolean support;

    /**
     * 初始化
     *
     * @param baseContext
     */
    public static void init(Context baseContext) {
        //获取蓝牙适配器
        blueadapter = BluetoothAdapter.getDefaultAdapter();
        //表示手机支持蓝牙
        support = blueadapter != null;
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
        if (!blueadapter.isEnabled()) {//如果没打开，则打开蓝牙
            blueadapter.enable();
        }
    }

    /**
     * 关闭蓝牙
     */
    public static void close() {
        //判断本机蓝牙是否打开
        if (blueadapter.isEnabled()) {//如果没打开，则打开蓝牙
            blueadapter.disable();
        }
    }

    /**
     * 判断蓝牙是否打开
     *
     * @return
     */
    public static boolean isOpen() {
        return blueadapter.isEnabled();
    }

}
