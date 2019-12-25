package com.sinothk.droid.bluetooth.demo.inter;

import android.bluetooth.BluetoothDevice;

public interface OnEventListener {
    void callback(int eventType, BluetoothDevice bluetoothDevice);
}
