package com.shellinfo.common.data.remote.response.model.payment_gateway.order_status

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderStatusRequest(

    @Json(name ="merchantOrderId") val merchantOrderId: String,
    @Json(name ="authorization") var authorization: String ? =null,
    @Json(name ="amount") val amount: String,
    @Json(name ="mobno") val mobno: String
)
