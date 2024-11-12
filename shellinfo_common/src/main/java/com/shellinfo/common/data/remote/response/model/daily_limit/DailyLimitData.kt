package com.shellinfo.common.data.remote.response.model.daily_limit

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DailyLimitData(

    @Json(name ="dayLimitId") val dayLimitId: Int,
    @Json(name ="dayLimitValue") val dayLimitValue: Int,
)
