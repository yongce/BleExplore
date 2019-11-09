package me.ycdev.android.demo.ble.common.device

import android.bluetooth.le.ScanFilter
import android.os.ParcelUuid
import me.ycdev.android.ble.common.sig.TimeServiceProfile

class TimeService : BleDevice {
    override fun id(): String = ID

    override fun buildBleScanFilter(): List<ScanFilter> {
        val filters = ArrayList<ScanFilter>()
        filters.add(
            ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(TimeServiceProfile.TIME_SERVICE))
                .build()
        )
        return filters
    }

    companion object {
        const val ID = "TimeService";
    }
}