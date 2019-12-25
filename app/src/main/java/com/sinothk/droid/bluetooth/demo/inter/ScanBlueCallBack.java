package com.sinothk.droid.bluetooth.demo.inter;

import android.bluetooth.BluetoothDevice;

public interface ScanBlueCallBack {
    void onScanStart();

    void onScanFinish();

    void onScanning(BluetoothDevice device);
}
