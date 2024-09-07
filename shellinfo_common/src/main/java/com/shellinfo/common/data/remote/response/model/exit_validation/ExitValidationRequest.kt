package com.shellinfo.common.data.remote.response.model.exit_validation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExitValidationRequest(
    @Json(name ="binNumber") val binNumber: String,
)
