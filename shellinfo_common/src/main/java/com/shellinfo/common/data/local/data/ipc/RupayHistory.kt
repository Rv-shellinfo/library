package com.shellinfo.common.data.local.data.ipc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RupayHistory(
    var acquirerID:String,
    var operatorID:String,
    var terminalID:String,
    var trxDateTime:String,
    var trxSeqNum:String,
    var trxAmt:String,
    var cardBalance1:String,
    var cardBalance2:String,
    var cardBalance3:String,
    var trxStatus:String,
    var rfu:String,
):Parcelable
