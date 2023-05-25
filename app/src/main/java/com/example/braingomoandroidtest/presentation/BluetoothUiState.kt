package com.example.braingomoandroidtest.presentation

import com.example.braingomoandroidtest.domain.signal.BluetoothDevice
import com.example.braingomoandroidtest.domain.signal.BluetoothData

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
    val dataList: List<BluetoothData> = emptyList()
)
