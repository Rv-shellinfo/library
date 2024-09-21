package com.shellinfo.common.data.local.data.emv_rupay.display

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TxnHistory(
    var txnSeqNumber:String,
    var txnDate:String,
    var txnTime:String,
    var txnAmount:String,
    var txnType:String,
    var stationName:String
)
