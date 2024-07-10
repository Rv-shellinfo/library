package com.shellinfo.common.data.remote.response.model.ticket

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TicketResponse(

    @Json(name ="returnCode") val returnCode: String,
    @Json(name ="returnMsg") val returnMsg: String,
    @Json(name ="ltmrhlPurchaseId") val ltmrhlPurchaseId: String,
    @Json(name ="tickets") val tickets: List<Ticket>
)
