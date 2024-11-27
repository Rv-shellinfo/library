package com.shellinfo.common.data.local.data.emv_rupay.display.osa_display

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TxnHistoryOsa(
    var terminalId:String,
    var txnSeqNumber:String,
    var txnDate:String,
    var txnTime:String,
    var passLimit:String,
    var dailyLimit:String,
    var tripCounts:String,
    var txnType:String,
    var passType:String,
    var stationName:String,
    var stationId:String,
)
