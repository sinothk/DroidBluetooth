package com.sinothk.droid.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;


/**
 * https://www.cnblogs.com/lwkdbk/p/9939643.html
 */
public class DroidBluetooth {
    private static String TAG = "DroidBluetooth";
    private static BluetoothAdapter blueAdapter;
    private static boolean support;
    private static BluetoothSocket bluetoothSocket = null;

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
     * 判断蓝牙是否已匹配
     *
     * @return
     */
    public static boolean isBond(BluetoothDevice device) {
        return device.getBondState() == BluetoothDevice.BOND_BONDED;
    }


    /**
     * 获取已绑定的设备
     *
     * @return
     */
    public static Set<BluetoothDevice> getBondedDevices() {
        return blueAdapter.getBondedDevices();
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
    public static void cancelPinBule(BluetoothDevice device) {
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

    public static void connect(final BluetoothDevice device) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bluetoothSocket.connect();
                    Log.e("", "Connected");
                } catch (Exception e) {
                    Log.e("", e.getMessage());
                    try {
                        Log.e("", "trying fallback...");

                        bluetoothSocket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
                        bluetoothSocket.connect();

                        Log.e("", "Connected");
                    } catch (Exception e2) {
                        Log.e("", "Couldn't establish Bluetooth connection!");
                    }
                }
            }
        }).start();




//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
////            Log.d(TAG,"开始连接socket,uuid:" + ClassicsBluetooth.UUID);
//
//                    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//                    bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
//
////                    Method listenMethod = device.getClass().getMethod("listenUsingRfcommOn", new Class[]{int.class});
////                    BluetoothServerSocket returnValue = ( BluetoothServerSocket) listenMethod.invoke(blueAdapter, new Object[]{ 29});
//
////                    bluetoothSocket = (BluetoothSocket) device.getClass().getDeclaredMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
//
////                    if (bluetoothSocket != null && !bluetoothSocket.isConnected()) {
////                        bluetoothSocket.connect();
////                    }
//
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            try {
//                                bluetoothSocket.connect();
//
//                                if (isConnectBlue()) {
//                                    Log.e(TAG, "已连接");
//                                } else {
//                                    Log.e(TAG, "未连接");
//                                }
//
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                                Log.e(TAG, "连接异常");
//                            }
//                        }
//                    }.start();
//
//                } catch (IOException e) {
//                    Log.e(TAG, "socket连接失败");
//                    try {
//                        bluetoothSocket.close();
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                        Log.e(TAG, "socket关闭失败");
//                    }
//                }
////                catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
////                    e.printStackTrace();
////                    Log.e(TAG, "连接异常");
////                }
//            }
//        }).start();

//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    //获得一个socket，安卓4.2以前蓝牙使用此方法，获得socket，4.2后为下面的方法
//                    //不能进行配对
//                    // final BluetoothSocket socket = device.createRfcommSocketToServiceRecord
//                    // (UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
//
//                    //安卓系统4.2以后的蓝牙通信端口为 1 ，但是默认为 -1，所以只能通过反射修改，才能成功
//                    final BluetoothSocket bluetoothSocket = (BluetoothSocket) device.getClass()
//                            .getDeclaredMethod("createRfcommSocket", new Class[]{int.class})
//                            .invoke(device, 1);
//
//                    Thread.sleep(500);
//
//                    //这里建立蓝牙连接 socket.connect() 这句话必须单开一个子线程
//                    //至于原因 暂时不知道为什么
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            try {
//                                bluetoothSocket.connect();
//
//                                if (isConnectBlue()) {
//                                    Log.e(TAG, "已连接");
//                                } else {
//                                    Log.e(TAG, "未连接");
//                                }
//
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                                Log.e(TAG, "连接异常");
//                            }
//                        }
//                    }.start();
//
//                    //建立蓝牙连接
//
//                    //获得一个输出流
////                    outputStream = socket.getOutputStream();
////                    inputStream = socket.getInputStream();
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            Toast.makeText(MainActivity.this, ""连接成功"", Toast.LENGTH_SHORT).show();
////                        }
////                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
    }

    /**
     * 蓝牙是否连接
     *
     * @return
     */
    public static boolean isConnectBlue() {
        return bluetoothSocket != null && bluetoothSocket.isConnected();
    }


    /**
     * 断开连接
     *
     * @return
     */
    public boolean closeConnect() {
        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        bluetoothSocket = null;
        return true;
    }
}
