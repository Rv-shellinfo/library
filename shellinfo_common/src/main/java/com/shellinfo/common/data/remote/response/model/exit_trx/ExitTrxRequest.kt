package com.shellinfo.common.data.remote.response.model.exit_trx

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExitTrxRequest(

    @Json(name ="transactionId") var transactionId: String,
    @Json(name ="transactionType") var transactionType: Int,
    @Json(name ="txnSequenceNo") var txnSequenceNo: Int,
    @Json(name ="lineId") var lineId: String,
    @Json(name ="stationId") var stationId: String,
    @Json(name ="equipmentGroupId") var equipmentGroupId: String,
    @Json(name ="equipmentId") var equipmentId: String,
    @Json(name ="aquirerId") var aquirerId: String,
    @Json(name ="operatorId") var operatorId: String,
    @Json(name ="operatorNameId") var operatorNameId: Int,
    @Json(name ="terminalId") var terminalId: String,
    @Json(name ="entryAquirerId") var entryAquirerId: String,
    @Json(name ="entryOperatorId") var entryOperatorId: String,
    @Json(name ="entryTerminalId") var entryTerminalId: String,
    @Json(name ="entryDateTime") var entryDateTime: String,
    @Json(name ="cardType") var cardType: Int,
    @Json(name ="panSha") var panSha: String,
    @Json(name ="productType") var productType: Int,
    @Json(name ="transactionDateTime") var transactionDateTime: String,
    @Json(name ="businessDate") var businessDate: String,
    @Json(name ="amount") var amount: Double,
    @Json(name ="cardBalance") var cardBalance: Double,
    @Json(name ="passStartDate") var passStartDate: String,
    @Json(name ="passEndDate") var passEndDate: String,
    @Json(name ="passStationOne") var passStationOne: String,
    @Json(name ="passStationTwo") var passStationTwo: String,
    @Json(name ="passBalance") var passBalance: String,
    @Json(name ="bankTid") var bankTid: String,
    @Json(name ="bankMid") var bankMid: String,
    @Json(name ="cardBin") var cardBin: String,
    @Json(name ="peakNonPeakTypeId") var peakNonPeakTypeId: Int
)
