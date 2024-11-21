package com.shellinfo.common.data.remote.response.model.entry_trx

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EntryTrxRequest(
    @Json(name ="transactionId") var transactionId: String,
    @Json(name ="transactionType") var transactionType: Int,
    @Json(name ="lineId") var lineId: String,
    @Json(name ="stationId") var stationId: String,
    @Json(name ="equipmentGroupId") var equipmentGroupId: String,
    @Json(name ="equipmentId") var equipmentId: String,
    @Json(name ="aquirerId") var aquirerId: String,
    @Json(name ="operatorId") var operatorId: String,
    @Json(name ="terminalId") var terminalId: String,
    @Json(name ="cardType") var cardType: Int,
    @Json(name ="panSha") var panSha: String,
    @Json(name ="productType") var productType: Int,
    @Json(name ="cardBin") var cardBin: String,
    @Json(name ="peakNonPeakTypeId") var peakNonPeakTypeId: Int,
    @Json(name ="businessDate") var businessDate: String,
    @Json(name ="transactionDateTime") var transactionDateTime: String,
    @Json(name ="futureColumnOne") var futureColumnOne: String,
    @Json(name ="futureColumnTwo") var futureColumnTwo: String,
    @Json(name ="futureColumnThree") var futureColumnThree: String,
    @Json(name ="futureColumnFour") var futureColumnFour: String
)
