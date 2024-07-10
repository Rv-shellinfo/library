package com.shellinfo.common.data.remote.response.model.server

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServerDateTimeResponse(

    @Json(name ="returnCode") var returnCode: Int,
    @Json(name ="returnMsg") var returnMsg: String,
    @Json(name ="businessDate") var businessDate: String,
    @Json(name ="ticketSellingStartime") var ticketSellingStartime: String,
    @Json(name ="ticketSellingEndtime") var ticketSellingEndtime: String,
)
