package com.shellinfo.common.data.remote.response.model.exit_validation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExitValidationResponse(
    @Json(name ="binNumber") var binNumber: String? =null
)
