package com.shellinfo.common.data.local.data.emv_rupay.binary.osa_bin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HistoryBinOsa(

    var acquirerID: Byte ?=null,             // 1 byte
    var operatorID: ByteArray?=null,         // 2 bytes
    var terminalID: ByteArray?=null,          // 3 bytes
    var trxDateTime: ByteArray?=null,       // 3 bytes
    var trxSeqNum: ByteArray?=null,         // 2 bytes
    var previousTrips: ByteArray?=null,            // 2 bytes
    var tripLimits: Byte?=null,           // 1 byte
    var tripCounts: Byte?=null,           // 1 byte
    var cardBalance3: Byte?=null,           // // 4 bits for cardBalance3, 4 bits for trxStatus
    var trxStatus: Byte?=null,              // 4 bits
    var productType: Byte?=null,              // 1 byte
    var rfu: Byte?=null                     // 1 byte
)
