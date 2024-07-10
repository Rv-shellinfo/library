package com.shellinfo.common.data.remote.response.model.fare

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FareRequest(
    @Json(name ="fromStationId") val fromStationId: String,
    @Json(name ="toStationId") val toStationId: String,
    @Json(name ="zoneNumberOrStored_ValueAmount") val zoneNumberOrStored_ValueAmount: Int? = 0,
    @Json(name ="ticketTypeId") val ticketTypeId: Int,
    @Json(name ="merchantId") val merchantId: String,
    @Json(name ="travelDatetime") val travelDatetime: String?="",
    @Json(name ="authorization") var authorization: String?=null


)