package com.shellinfo.common.data.remote.response.model.stations_new

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StationData(
    @Json(name = "stationName") var stationName: String = "",
    @Json(name = "stationShortName") var stationShortName: String? = "",
    @Json(name = "stationUniqueid") var stationUniqueid: String? = "",
    @Json(name = "stationCcIp") var stationCcIp: String? = "",
    @Json(name = "stationScIp") var stationScIp: String? = "",
    @Json(name = "lineId") var lineId: Int? = 0,
    @Json(name = "lineName") var lineName: String? = "",
    @Json(name = "lineColorCode") var lineColorCode: String? = "",
    @Json(name = "latitude") var latitude: Double? = 0.0,
    @Json(name = "longitude") var longitude: Double? = 0.0,
    @Json(name = "xPosition") var xPosition: Double? = 0.0,
    @Json(name = "yPosition") var yPosition: Double? = 0.0,
    @Json(name = "isJunction") var isJunction: String? = "",
    @Json(name = "validFromDate") var validFromDate: String? = "",
    @Json(name = "validToDate") var validToDate: String? = "",
    @Json(name = "status") var status: String? = ""
)
