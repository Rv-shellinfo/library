package com.shellinfo.common.data.remote.response.model.gate_fare

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GateFareRequest(
    @Json(name ="fromStationId") var fromStationId: String?="",
    @Json(name ="toStationId") var toStationId: String?="",
    @Json(name ="entryDateTime") var entryDateTime: String?="",
    @Json(name ="exitDateTime") var exitDateTime: String?="",
    @Json(name ="modeId") var modeId: Int?=0,
    @Json(name ="equipmentId") var equipmentId: String?="",
    @Json(name ="equipmentGroupId") var equipmentGroupId: String?=""
)
