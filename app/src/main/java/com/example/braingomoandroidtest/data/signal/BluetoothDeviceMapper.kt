package com.example.braingomoandroidtest.data.signal

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.example.braingomoandroidtest.domain.signal.BluetoothDeviceDomain

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain {
    return BluetoothDeviceDomain(
        name = name,
        address = address
    )
}