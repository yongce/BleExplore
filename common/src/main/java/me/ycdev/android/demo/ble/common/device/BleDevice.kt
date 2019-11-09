package me.ycdev.android.demo.ble.common.device

import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanRecord
import android.bluetooth.le.ScanResult
import me.ycdev.android.lib.common.utils.EncodingUtils
import me.ycdev.android.lib.common.utils.EncodingUtils.encodeWithHex

interface BleDevice {
    fun id(): String
    fun buildBleScanFilter(): List<ScanFilter>?
    fun checkScanResult(scanResult: ScanResult, filter: String?): Boolean = true
    fun getReadableManufacturerData(id: Int, data: ByteArray) = encodeWithHex(data)
}