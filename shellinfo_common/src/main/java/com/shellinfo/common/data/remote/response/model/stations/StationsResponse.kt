package com.shellinfo.common.data.remote.response.model.stations

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StationsResponse(
    @Json(name ="returnCode") var returnCode: Int?,
    @Json(name ="returnMsg") var returnMsg: String?,
    @Json(name ="version") var version: String?,
    @Json(name ="stations") var stations: List<Station>?,


    @Json(name ="em") var em: String?,
    @Json(name ="s") var s: String?,
    @Json(name ="e") var e: String?,
    @Json(name ="r") var r: List<Station>?

    )