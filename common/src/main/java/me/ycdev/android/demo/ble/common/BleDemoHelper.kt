package me.ycdev.android.demo.ble.common

import android.app.Activity
import androidx.annotation.NonNull
import me.ycdev.android.ble.common.client.BleScanner
import me.ycdev.android.lib.common.perms.PermissionCallback
import me.ycdev.android.lib.common.perms.PermissionRequestParams
import me.ycdev.android.lib.common.perms.PermissionUtils
import timber.log.Timber

object BleDemoHelper {
    private const val TAG = "BleDemoHelper"

    fun requestPermsForBleScan(
        @NonNull activity: Activity,
        requestCode: Int,
        @NonNull callback: PermissionCallback
    ) {
        Timber.tag(TAG).d("Request perms for BLE scan")
        val params = PermissionRequestParams()
        params.requestCode = requestCode
        params.permissions =
            BleScanner.BLE_SCAN_PERMS
        params.rationaleContent = activity.getString(R.string.ble_scan_perm_rationale)
        params.callback = callback
        PermissionUtils.requestPermissions(activity, params)
    }
}
