package com.shellinfo.common.data.remote.response.model.exit_trx

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExitTrxResponse(
    @Json(name ="returnCode") val returnCode:String,
    @Json(name ="returnMessage") val returnMessage:String,
)
