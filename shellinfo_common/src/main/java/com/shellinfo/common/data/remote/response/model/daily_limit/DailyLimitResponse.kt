package com.shellinfo.common.data.remote.response.model.daily_limit

import com.shellinfo.common.data.remote.response.model.trip_limit.TripLimitData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DailyLimitResponse(
    @Json(name ="dayLimit") val dayLimit: List<DailyLimitData>
)
