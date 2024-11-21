package com.shellinfo.common.data.remote.response.model.purchase_pass

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PurchasePassResponse(
    @Json(name ="returnCode") val returnCode:String,
    @Json(name ="returnMessage") val returnMessage:String,
)