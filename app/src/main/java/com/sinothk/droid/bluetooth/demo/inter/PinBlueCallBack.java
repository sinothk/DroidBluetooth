package com.sinothk.droid.bluetooth.demo.inter;

import android.bluetooth.BluetoothDevice;

public interface PinBlueCallBack {
    void onBonding(BluetoothDevice device);

    void onBondSuccess(BluetoothDevice device);

    void onBondFail(BluetoothDevice device);

    void onBondRequest();
}
