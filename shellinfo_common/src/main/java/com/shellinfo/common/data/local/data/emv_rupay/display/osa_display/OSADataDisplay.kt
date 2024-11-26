package com.shellinfo.common.data.local.data.emv_rupay.display.osa_display

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OSADataDisplay(
    var error:String,
    var errorCode:Int,
    var lastTxnDateTime:String,
    var lastTxnStatus:String,
    var lastStationName:String,
    var lastStationId:String,
    var product:String,
    var txnStatus:Int,
    var cardEffectiveDate:String,
    var cardHistory:List<TxnHistoryOsa>,
    var cardPassesList:List<PassData>
)
