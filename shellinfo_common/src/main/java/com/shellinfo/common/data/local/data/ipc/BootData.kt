package com.shellinfo.common.data.local.data.ipc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BootData(

    val isBootTimeSet:Boolean,
    val rebootHour:Int = 0,
    val rebootMinute:Int = 1

):Parcelable
