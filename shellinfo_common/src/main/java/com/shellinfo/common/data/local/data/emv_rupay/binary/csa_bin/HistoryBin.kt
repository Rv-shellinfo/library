package com.shellinfo.common.data.local.data.emv_rupay.binary.csa_bin

data class HistoryBin(
    var acquirerID: Byte,             // 1 byte
    var operatorID: ByteArray,        // 2 bytes
    var terminalID: ByteArray,        // 3 bytes
    var trxDateTime: ByteArray,       // 3 bytes
    var trxSeqNum: ByteArray,         // 2 bytes
    var trxAmt: ByteArray,            // 2 bytes
    var cardBalance1: Byte,           // 1 byte
    var cardBalance2: Byte,           // 1 byte
    var cardBalance3: Byte,           // 4 bits
    var trxStatus: Byte,              // 4 bits
    var rfu: Byte                     // 8 bits
) {
    // Setting the lower 4 bits for cardBalance3
    fun setCardBalance3LowerNibble(value: Byte) {
        cardBalance3 = (cardBalance3.toInt() and 0xF0 or (value.toInt() and 0x0F)).toByte()
    }

    // Getting the lower 4 bits from cardBalance3
    fun getCardBalance3LowerNibble(): Byte {
        return (cardBalance3.toInt() and 0x0F).toByte()
    }

    // Setting the lower 4 bits for trxStatus
    fun setTrxStatusLowerNibble(value: Byte) {
        trxStatus = (trxStatus.toInt() and 0xF0 or (value.toInt() and 0x0F)).toByte()
    }

    // Getting the lower 4 bits from trxStatus
    fun getTrxStatusLowerNibble(): Byte {
        return (trxStatus.toInt() and 0x0F).toByte()
    }
}

