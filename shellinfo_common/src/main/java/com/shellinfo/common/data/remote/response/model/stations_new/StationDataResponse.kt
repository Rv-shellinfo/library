package com.shellinfo.common.data.remote.response.model.stations_new

import com.shellinfo.common.data.remote.response.model.trip_limit.TripLimitData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StationDataResponse(
    @Json(name ="stations") val stations: List<StationData>
)
