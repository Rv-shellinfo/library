package com.shellinfo.common.data.local.data.ipc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class AIDDenyData(
    val aidDenyFilePath:String,
): Parcelable
