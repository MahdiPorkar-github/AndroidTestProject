package com.example.braingomoandroidtest.data.signal

import com.example.braingomoandroidtest.domain.signal.BluetoothData

fun String.toBluetoothMessage(isFromLocalUser: Boolean): BluetoothData {
    val data = this
    return BluetoothData(
        data = data,
        isFromLocalUser = isFromLocalUser
    )
}

fun BluetoothData.toByteArray(): ByteArray {
    return data.encodeToByteArray()
}