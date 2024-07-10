package com.shellinfo.common.data.remote.response.model.payment_gateway

import com.shellinfo.common.data.remote.response.model.ticket.Ticket
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AppPaymentResponse(

    @Json(name ="returnCode") val returnCode: String ? = null,
    @Json(name ="returnMsg") val returnMsg: String ? = "Error",
    @Json(name ="ltmrhlPurchaseId") val ltmrhlPurchaseId: String ? = "",
    @Json(name ="tickets") val tickets: List<Ticket>? = null
)
