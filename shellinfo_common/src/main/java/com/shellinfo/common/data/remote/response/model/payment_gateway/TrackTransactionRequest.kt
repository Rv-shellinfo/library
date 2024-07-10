package com.shellinfo.common.data.remote.response.model.payment_gateway

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackTransactionRequest(

    @Json(name ="authorization") var authorization: String?=null,
    @Json(name ="fareQuoteId") var fareQuoteId: String,
    @Json(name ="custId") var custId: String,
    @Json(name ="custName") var custName: String,
    @Json(name ="email") var email: String,
    @Json(name ="mobileNo") var mobileNo: String,
    @Json(name ="orderDate") var orderDate: String,
    @Json(name ="gatewayName") var gatewayName: String,
    @Json(name ="txnAmount") var txnAmount: String,
    @Json(name ="tSavariOrderId") var tSavariOrderId: String,
    @Json(name ="appversion") var appversion: String,
    @Json(name ="noOfTickets") var noOfTickets: String,
)
