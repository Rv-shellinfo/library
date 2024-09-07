package com.shellinfo.common.data.remote.response.model.entry_validation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EntryValidationRequest(
    @Json(name ="binNumber") var binNumber: String? =null,
    @Json(name ="lastStationId") var lastStationId: String? =null,
    @Json(name ="fromStationId") var lastTransactionDateTime: String? =null,
    @Json(name ="equipmentId") var equipmentId: String? =null,
    @Json(name ="fromStationId") var equipmentGroupId: String? =null,
)
