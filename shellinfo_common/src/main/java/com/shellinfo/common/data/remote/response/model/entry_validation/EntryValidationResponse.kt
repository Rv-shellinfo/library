package com.shellinfo.common.data.remote.response.model.entry_validation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EntryValidationResponse(
    @Json(name ="errorCode") val errorCode: Int,
)
