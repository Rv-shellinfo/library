package com.shellinfo.common.data.local.data.ipc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class RupayCardDataWrite(
    val errorCode:Int,
    val trxStatus:Int,
    val productType:String,
    val acquirerID:String,
    val operatorID:String,
    val terminalID:String,
    val trxDateTime:String,
    val fareAmt:String,
    val routeNo:String,
    val serviceProviderData:String,
    val historyData:List<RupayHistory>
): Parcelable