package com.shellinfo.common.data.local.data.emv_rupay.display.osa_display

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TxnHistoryOsa(
    var txnSeqNumber:String,
    var txnDate:String,
    var txnTime:String,
    var txnAmount:String,
    var txnType:String,
    var passType:String,
    var stationName:String
)
