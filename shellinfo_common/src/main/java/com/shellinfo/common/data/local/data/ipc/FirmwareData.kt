package com.shellinfo.common.data.local.data.ipc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirmwareData(
    val version:Long,
    val path:String,
    val fileName:String
): Parcelable