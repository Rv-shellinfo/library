package com.shellinfo.common.data.remote.response.model.trip_limit

import com.shellinfo.common.data.remote.response.model.pass.PassTypeData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TripLimitData(
    @Json(name ="tripLimitId") val tripLimitId: Int,
    @Json(name ="tripLimitValue") val tripLimitValue: Int,
)
