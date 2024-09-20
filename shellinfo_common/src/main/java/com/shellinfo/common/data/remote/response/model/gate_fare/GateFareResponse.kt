package com.shellinfo.common.data.remote.response.model.gate_fare

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GateFareResponse(
    @Json(name ="returnCode") var returnCode: Int =-1,
    @Json(name ="returnMessage") var returnMessage: String?=null,
    @Json(name ="fare") var fare: Int=-1,
)
