package com.shellinfo.common.data.remote.response.model.payment_gateway

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChecksumRequest(

    @Json(name ="orderid") var orderid: String,
    @Json(name ="amount") var amount: String,
)
