package com.shellinfo.common.data.remote.response.model.pass

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetPassResponse(
    @Json(name ="passProductType") val passProductType: List<PassTypeData>
)
