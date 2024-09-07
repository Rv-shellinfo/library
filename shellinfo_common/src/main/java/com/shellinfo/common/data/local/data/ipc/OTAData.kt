package com.shellinfo.common.data.local.data.ipc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class OTAData(
    val version:Long,
    val path:String,
    val fileName:String
):Parcelable