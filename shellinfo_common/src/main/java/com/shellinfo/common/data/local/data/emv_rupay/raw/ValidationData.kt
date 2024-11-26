package com.shellinfo.common.data.local.data.emv_rupay.raw

import com.shellinfo.common.utils.IPCConstants

/**
 *  Validation Data index (68,106) , Represent as Transient Data
 *
 *
 *  @property errorCode - (68,70) error code specific to transaction defined by NPCI
 *  @see IPCConstants.NO_ERROR
 *  @see IPCConstants.AMT_NOT_SUFFICIENT
 *  @see IPCConstants.TORN_TRANSACTION
 *  @see IPCConstants.ENTRY_NOT_FOUND
 *  @see IPCConstants.EXIT_NOT_FOUND
 *  @see IPCConstants.CSA_PRESENT_ALL_PASS_INVALID
 *  @see IPCConstants.TIME_EXCEEDED
 *  @see IPCConstants.CARD_EXPIRED
 *
 *  @property productType - (70,72) It specify type of product defined by NPCI
 *  @see IPCConstants.PROD_TYPE_SINGLE_JOURNEY
 *  @see IPCConstants.PROD_TYPE_DISCOUNTED_FARE
 *  @see IPCConstants.PROD_TYPE_PASS
 *
 *  @property terminalInfo - (72,84) It specify Acquirer,Operator,Terminal ID (Combination)
 *
 *  @property txnDateTime - (84,90) It Represent date and time of the transaction *(with respect to card effective date)*
 *
 *  @property fareAmount - (90,94) It Represent Journey Fare Amount
 *
 *  @property routeNo - (94,98) It Represent Route Number (Line/Corridor) Defined by Operator
 *
 *  @property serviceProviderData - (98,104) This is Used by Operator (As per the requirement)
 *
 *  @property txnStatus - (104,105) Transaction Status
 *  @see IPCConstants.TXN_STATUS_EXIT
 *  @see IPCConstants.TXN_STATUS_ENTRY
 *  @see IPCConstants.TXN_STATUS_PENALTY
 *  @see IPCConstants.TXN_STATUS_ONE_TAP_TICKET
 *
 *  @property rfu -(105,106) Reserve For Future Use
 */
data class ValidationData(
    var errorCode:String = "",
    var productType:String = "",
    var terminalInfo: TerminalData? =null,
    var txnDateTime:String = "",
    var fareAmount:String = "",
    var routeNo:String = "",
    var serviceProviderData:String = "",
    var txnStatus:String = "",
    var rfu:String = "",
)
