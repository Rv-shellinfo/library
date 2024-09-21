package com.shellinfo.common.data.local.data.emv_rupay

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RupayError(
    var errorCode:Int = -1,
    var errorMessage:String =""
)