package com.shellinfo.common.data.remote.response.model.fare

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FareResponse(
    @Json(name ="finalFare") var finalFare: String,
    @Json(name ="fareQuotIdforOneTicket") var fareQuotIdforOneTicket: String,
    @Json(name ="fareValidTime") var fareValidTime: String,
)