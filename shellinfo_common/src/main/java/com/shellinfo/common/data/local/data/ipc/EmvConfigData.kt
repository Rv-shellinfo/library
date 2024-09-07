package com.shellinfo.common.data.local.data.ipc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmvConfigData(
    val entryPointFilePath:String,
    val terminalFilePath:String,
    val capkFilePath:String,
    val processingFilePath:String,
    val emvInfoFilePath:String,
):Parcelable