package com.shellinfo.common.data.local.data.emv_rupay

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RupayMessage(
    var returnCode:Int = -1,
    var returnMessage:String ="",
    var isSuccess:Boolean=true
)