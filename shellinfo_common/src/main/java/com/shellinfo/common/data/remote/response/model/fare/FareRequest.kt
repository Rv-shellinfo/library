package com.shellinfo.common.data.remote.response.model.fare

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FareRequest(
    @Json(name ="fromStationId") var fromStationId: String?="",
    @Json(name ="toStationId") var toStationId: String?="",
    @Json(name ="zoneNumberOrStored_ValueAmount") val zoneNumberOrStored_ValueAmount: Int? = 0,
    @Json(name ="ticketTypeId") var ticketTypeId: Int?=0,
    @Json(name ="merchantId") var merchantId: String?="",
    @Json(name ="travelDatetime") var travelDatetime: String?="",
    @Json(name ="authorization") var authorization: String?=""


)