package com.shellinfo.common.data.local.data.emv_rupay.display

/**
 * This class represent as CSA Data which needs to display on the screen
 * In this class properties the values already converted to String corresponding to their Hexa Decimal values
 * We just need to set this String on Display if we want
 */
data class CSADataDisplay(
    var cardBalance:Double,
    var cardBalanceFormat:String,
    var penaltyAmount:Double,
    var penaltyAmountFormat:String,
    var error:String,
    var errorCode:Int,
    var lastTxnDateTime:String,
    var lastTxnStatus:String,
    var lastEquipId:String,
    var txnStatus:Int,
    var cardEffectiveDate:String,
    var cardHistory:List<TxnHistory>
)
