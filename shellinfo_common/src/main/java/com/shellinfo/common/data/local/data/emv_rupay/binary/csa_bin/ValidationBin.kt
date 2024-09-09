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
    private var trxStatusAndRfu: Byte // 4 bits for trxStatus, 4 bits for RFU
){
//    init {
//        require(operatorID.size == 2) { "operatorID must be 2 bytes" }
//        require(terminalID.size == 3) { "terminalID must be 3 bytes" }
//        require(trxDateTime.size == 3) { "trxDateTime must be 3 bytes" }
//        require(fareAmt.size == 2) { "fareAmt must be 2 bytes" }
//        require(routeNo.size == 2) { "routeNo must be 2 bytes" }
//        require(serviceProviderData.size == 3) { "serviceProviderData must be 3 bytes" }
//    }

    // Getter for the 4 most significant bits (transaction status)
    fun getTrxStatus(): Int {
        return (trxStatusAndRfu.toInt() shr 4) and 0x0F // Extract upper 4 bits (trxStatus)
    }

    // Setter for the 4 most significant bits (transaction status)
    fun setTrxStatus(trxStatus: Int) {
        trxStatusAndRfu = ((trxStatusAndRfu.toInt() and 0x0F) or ((trxStatus and 0x0F) shl 4)).toByte()
    }

    // Getter for the 4 least significant bits (RFU)
    fun getRfu(): Int {
        return trxStatusAndRfu.toInt() and 0x0F // Extract lower 4 bits (RFU)
    }

    // Setter for the 4 least significant bits (RFU)
    fun setRfu(rfu: Int) {
        trxStatusAndRfu = ((trxStatusAndRfu.toInt() and 0xF0) or (rfu and 0x0F)).toByte()
    }
}
