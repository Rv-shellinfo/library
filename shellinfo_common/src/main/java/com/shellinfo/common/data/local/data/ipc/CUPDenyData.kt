package com.shellinfo.common.data.local.data.ipc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CUPDenyData(
    val cupDenyFilePath:String,
): Parcelable