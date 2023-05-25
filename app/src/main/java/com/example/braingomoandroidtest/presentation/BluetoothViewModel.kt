package com.example.braingomoandroidtest.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.braingomoandroidtest.domain.signal.BluetoothController
import com.example.braingomoandroidtest.domain.signal.BluetoothDeviceDomain
import com.example.braingomoandroidtest.domain.signal.ConnectionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
) : ViewModel() {

    private val _state = MutableStateFlow(BluetoothUiState())
    val state = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices,
            dataList = if (state.isConnected) state.dataList else emptyList()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    private var deviceConnectionJob: Job? = null

    init {

        bluetoothController.isConnected.onEach { isConnected ->
            _state.update { it.copy(isConnected = isConnected) }
        }.launchIn(viewModelScope)

        bluetoothController.errors.onEach { error ->
            _state.update {
                it.copy(
                    errorMessage = error
                )
            }
        }.launchIn(viewModelScope)


        startScan()

    }

    fun connectToDevice(device: BluetoothDeviceDomain) {
        Log.i("connectToDevice", "connectToDevice ${device.toString()}")
        _state.update {
            it.copy(
                isConnecting = true
            )
        }
        deviceConnectionJob = bluetoothController
            .connectToDevice(device)
            .listen()
    }

//    fun disconnectFromDevice() {
//        deviceConnectionJob?.cancel()
//        bluetoothController.closeConnection()
//        _state.update {
//            it.copy(
//                isConnecting = false,
//                isConnected = false
//            )
//        }
//    }

//    fun waitForIncomingConnections() {
//        _state.update { it.copy(isConnecting = true) }
//        deviceConnectionJob = bluetoothController
//            .startBluetoothServer()
//            .listen()
//    }

    fun sendData(data: String) {
        viewModelScope.launch {
            val bluetoothData = bluetoothController.trySendData(data)
            if (bluetoothData != null) {
                _state.update {
                    it.copy(
                        dataList = it.dataList + bluetoothData
                    )
                }
            }
        }
    }

    private fun startScan() {
        bluetoothController.startDiscovery()
        Log.i("startScan()", "start Scan")
    }

    fun stopScan() {
        bluetoothController.stopDiscovery()
        Log.i("stopScan()", "stop Scan")
    }


    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when (result) {
                ConnectionResult.ConnectionEstablished -> {
                    _state.update {
                        it.copy(
                            isConnected = true,
                            isConnecting = false,
                            errorMessage = null
                        )
                    }
                }
                is ConnectionResult.TransferSucceeded -> {
                    _state.update {
                        it.copy(
                            dataList = it.dataList + result.data
                        )
                    }
                }
                is ConnectionResult.Error -> {
                    _state.update {
                        it.copy(
                            isConnected = false,
                            isConnecting = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
            .catch { throwable ->
                bluetoothController.closeConnection()
                _state.update {
                    it.copy(
                        isConnected = false,
                        isConnecting = false,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.release()
    }
}