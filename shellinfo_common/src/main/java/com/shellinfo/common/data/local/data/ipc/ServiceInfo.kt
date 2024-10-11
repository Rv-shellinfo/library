package com.shellinfo.common.data.local.data.ipc

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServiceInfo(
    val commonServiceId:Int,
    val operatorServiceId:Int
)
