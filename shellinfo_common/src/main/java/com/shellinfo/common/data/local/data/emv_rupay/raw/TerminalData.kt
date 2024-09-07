package com.shellinfo.common.data.local.data.emv_rupay.raw

/**
 * Terminal data set by terminal
 * @property acquirerId: 2 Bytes (16 bits)
 * @property operatorId: 2 Bytes (16 bits)
 * @property terminalId: 2 Bytes (16 bits)
 */
data class TerminalData(
    var acquirerId:String = "",
    var operatorId:String = "",
    var terminalId:String = "",
)
