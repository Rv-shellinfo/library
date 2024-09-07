package com.shellinfo.common.data.local.data.ipc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HealthData(
    val firmwareVersion:Long,
    val isNfcEnabled:Boolean,
    val capkVersion:String,
    val emvTerminalVersion:String,
    val emvEntryPointVersion:String,
    val emvProcessingVersion:String,
    val rtcVoltage:Int
):Parcelable