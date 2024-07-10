package com.shellinfo.common.data.remote.response.model.stations

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Station(
    @Json(name ="stationId") var stationId: String,
    @Json(name ="name") var name: String?,
    @Json(name ="shortName") var shortName: String?,
    @Json(name ="corridorId") var corridorId: Int?,
    @Json(name ="corridorName") var corridorName: String?,
    @Json(name ="lattitude") var lattitude: Double?,
    @Json(name ="longitude") var longitude: Double?,
    @Json(name ="stationName") var stationName: String?,
    @Json(name ="isJunction") var isJunction: Boolean?,
    @Json(name ="routeColorCode") var routeColorCode: String?,
    @Json(name ="mstId") var mstId: String?,
    @Json(name ="status") var status: String?,
)