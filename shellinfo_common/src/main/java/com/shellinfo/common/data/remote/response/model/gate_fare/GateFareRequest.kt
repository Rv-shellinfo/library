package com.shellinfo.common.data.remote.response.model.gate_fare

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GateFareRequest(
    @Json(name ="fromStationId") var fromStationId: String?="",
    @Json(name ="toStationId") var toStationId: String?="",
    @Json(name ="entryDateTime") var entryDateTime: String?="",
    @Json(name ="exitDateTime") var exitDateTime: String?="",
    @Json(name ="modeId") var modeId: Int?=0,
    @Json(name ="equipmentId") var equipmentId: String?="",
    @Json(name ="equipmentGroupId") var equipmentGroupId: String?="",
    @Json(name ="terminalId") var terminalId: String?="",
    @Json(name ="productType") var productType: String?="",
    @Json(name ="passType") var passType: String?="",
    @Json(name ="passStartDate") var passStartDate: String?="",
    @Json(name ="passExpiryDate") var passExpiryDate: String?="",
    @Json(name ="passStationOneId") var passStationOneId: String?="",
    @Json(name ="passStationTwoId") var passStationTwoId: String?="",
    @Json(name ="noOfTripRemaining") var noOfTripRemaining: String?="",
)
