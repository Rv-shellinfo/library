package com.shellinfo.common.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import timber.log.Timber
import java.security.MessageDigest

object DeviceUtils {

    fun getDeviceHashKey(context: Context): String {
        var keyHash = ""
        try {
            val packageInfo = context.getPackageInfo()
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }
            for (signature in signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT)

                Timber.d("Key hash value >>> $keyHash")

                return keyHash.trim()
            }
        } catch (e: Exception) {
            Timber.e("Key hash value Error>>> $e")
            return keyHash
        }
        return keyHash
    }

    private fun Context.getPackageInfo(): PackageInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0)
        }
    }
}