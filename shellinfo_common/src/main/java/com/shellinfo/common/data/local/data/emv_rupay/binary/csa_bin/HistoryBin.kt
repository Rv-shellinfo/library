package com.shellinfo.common.data.local.data.emv_rupay.binary.csa_bin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HistoryBin(
    var acquirerID: Byte ?=null,             // 1 byte
    var operatorID: ByteArray?=null,         // 2 bytes
    var terminalID: ByteArray?=null,          // 3 bytes
    var trxDateTime: ByteArray?=null,       // 3 bytes
    var trxSeqNum: ByteArray?=null,         // 2 bytes
    var trxAmt: ByteArray?=null,            // 2 bytes
    var cardBalance1: Byte?=null,           // 1 byte
    var cardBalance2: Byte?=null,           // 1 byte
    var cardBalance3: Byte?=null,           // // 4 bits for cardBalance3, 4 bits for trxStatus
    var trxStatus: Byte?=null,              // 4 bits
    var rfu: Byte?=null                     // 8 bits
)

