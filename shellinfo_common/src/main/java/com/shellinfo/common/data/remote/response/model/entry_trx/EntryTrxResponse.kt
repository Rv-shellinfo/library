package com.shellinfo.common.data.remote.response.model.entry_trx

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EntryTrxResponse(
    @Json(name ="returnCode") val returnCode:String,
    @Json(name ="returnMessage") val returnMessage:String,
)
