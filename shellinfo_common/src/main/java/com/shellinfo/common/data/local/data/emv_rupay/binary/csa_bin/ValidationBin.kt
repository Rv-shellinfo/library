package com.shellinfo.common.data.local.data.emv_rupay.binary.csa_bin

data class ValidationBin(
    var errorCode: Byte?,
    var productType: Byte,
    var acquirerID: Byte,
    var operatorID: ByteArray, // 2 bytes
    var terminalID: ByteArray, // 3 bytes
    var trxDateTime: ByteArray, // 3 bytes
    var fareAmt: ByteArray, // 2 bytes
    var routeNo: ByteArray, // 2 bytes
    var serviceProviderData: ByteArray, // 3 bytes
    var trxStatusAndRfu: Byte // 4 bits for trxStatus, 4 bits for RFU
)

