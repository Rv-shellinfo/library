package com.shellinfo.common.data.remote.response.model.zone

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ZoneData(
    @Json(name ="zoneId") val zoneId: Int,
    @Json(name ="zoneAmount") val zoneAmount: Int,
)
