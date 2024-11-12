package com.shellinfo.common.data.remote.response.model.pass

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PassTypeData(
    @Json(name ="passId") val passId: Int,
    @Json(name ="passName") val passName: String,
    @Json(name ="passCode") val passCode: String,
    @Json(name ="passPriority") val passPriority: Int,
    @Json(name ="passDurationDays") val passDurationDays: Int,
    @Json(name ="passLimitDefault") val passLimitDefault: Int,
    @Json(name ="dailyLimitDefault") val dailyLimitDefault: Int,
    @Json(name ="isDailyLimitActive") val isDailyLimitActive: String,
    @Json(name ="isPassLimitActive") val isPassLimitActive: String,
    @Json(name ="isZoneActive") val isZoneActive: String,
    @Json(name ="version") val version: String
)
