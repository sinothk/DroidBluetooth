package com.sinothk.droid.bluetooth.demo;

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.sinothk.droid.bluetooth.DroidBluetooth
import kotlinx.android.synthetic.main.activity_main_bluetooth_demo.*


class BluetoothDemoMainActivity : BaseActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_bluetooth_demo)

        initData()

        initBtn.setOnClickListener(this)
        supportBtn.setOnClickListener(this)
        openBtn.setOnClickListener(this)
        isOpenBtn.setOnClickListener(this)
        closeBtn.setOnClickListener(this)

        //
        setVisibleBtn.setOnClickListener(this)
        searchBluetoothBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            initBtn -> {
                DroidBluetooth.init(baseContext)
            }
            supportBtn -> {
                val isSupport: Boolean = DroidBluetooth.isSupport()
                if (isSupport) {
                    showMsg("设备支持蓝牙功能")
                } else {
                    showMsg("设备没有蓝牙功能")
                }
            }

            openBtn -> {
                DroidBluetooth.open()
            }

            isOpenBtn -> {
                val isOpen: Boolean = DroidBluetooth.isOpen()
                if (isOpen) {
                    showMsg("蓝牙设备——已打开")
                } else {
                    showMsg("蓝牙设备——没打开")
                }
            }

            closeBtn -> {
                DroidBluetooth.close()
            }

            setVisibleBtn -> {
                DroidBluetooth.setVisible(this, 60)
            }

            searchBluetoothBtn -> {
                DroidBluetooth.searchBluetooth()
            }
        }
    }

    private fun showMsg(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}
