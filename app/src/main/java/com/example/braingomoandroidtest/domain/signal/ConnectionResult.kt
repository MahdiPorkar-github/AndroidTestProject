package com.example.braingomoandroidtest.domain.signal

sealed interface ConnectionResult {
    object ConnectionEstablished: ConnectionResult
    data class TransferSucceeded(val data: BluetoothData): ConnectionResult
    data class Error(val message: String): ConnectionResult
}