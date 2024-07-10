package com.shellinfo.common.data.remote.response.model.server

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServerDateTimeRequest(
    @Json(name ="authorization") var authorization: String?=null
)
