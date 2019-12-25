package com.sinothk.droid.bluetooth.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.os.Bundle;

import com.sinothk.droid.bluetooth.DroidBluetooth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DroidBluetooth.init(getBaseContext());

        boolean isSupport = DroidBluetooth.isSupport();
        boolean isEnable = DroidBluetooth.isEnable();

    }
}
