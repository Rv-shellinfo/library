package com.shellinfo.common.data.remote.response.model.entry_validation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EntryValidationRequest(
    @Json(name ="binNumber") var binNumber: String? ="",
    @Json(name ="cardNumberSha") var cardNumberSha: String? ="",
    @Json(name ="lastStationId") var lastStationId: String? ="",
    @Json(name ="lastTransactionDateTime") var lastTransactionDateTime: String? ="",
    @Json(name ="equipmentId") var equipmentId: String? ="",
    @Json(name ="equipmentGroupId") var equipmentGroupId: String? ="",
    @Json(name ="cdacTerminalId") var cdacTerminalId: String? ="",
    @Json(name ="dataOne") var dataOne: String? ="",
    @Json(name ="dataTwo") var dataTwo: String? ="",
    @Json(name ="dataThree") var dataThree: String? ="",
    @Json(name ="dataFour") var dataFour: String? ="",
    @Json(name ="dataFive") var dataFive: String? =""
)
