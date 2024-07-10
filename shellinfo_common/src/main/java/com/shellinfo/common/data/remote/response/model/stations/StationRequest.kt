package com.shellinfo.common.data.remote.response.model.stations

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StationRequest(
    @Json(name ="authorization") var authorization: String,
)
