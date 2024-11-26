package com.shellinfo.common.data.local.data.emv_rupay.binary.osa_bin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HistoryBinOsa(

    var acquirerID: Byte ?=null,             // 1 byte
    var operatorID: ByteArray?=null,         // 2 bytes
    var terminalID: ByteArray?=null,          // 3 bytes
    var trxDateTime: ByteArray?=ByteArray(3),       // 3 bytes
    var trxSeqNum: ByteArray?=ByteArray(2),         // 2 bytes
    var passLimit: ByteArray?=ByteArray(2),            // 2 bytes
    var tripCount: Byte?=null,           // 1 byte
    var dailyLimit: Byte?=null,           // 1 byte
    var trxStatus: Byte?=null,              // 1 byte
    var productType: Byte?=null,              // 1 byte
    var rfu: Byte?=null                     // 1 byte
)
