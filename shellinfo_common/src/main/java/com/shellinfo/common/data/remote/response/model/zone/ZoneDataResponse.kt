package com.shellinfo.common.data.remote.response.model.zone

import com.shellinfo.common.data.remote.response.model.stations_new.StationData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ZoneDataResponse(
    @Json(name ="zones") val zones: List<ZoneData>
)
