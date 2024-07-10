package com.shellinfo.common.data.remote.response.model.payment_gateway

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackTransactionResponse(

    @Json(name ="returnCode") var returnCode: Int,
    @Json(name ="returnMessage") var returnMsg: String,
)
