package com.shellinfo.common.data.local.data.ipc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommandData(
    val isNFCEnable:Boolean?,
    val ledColor:String?,
    val isPlayBuzzer:Boolean?,
):Parcelable