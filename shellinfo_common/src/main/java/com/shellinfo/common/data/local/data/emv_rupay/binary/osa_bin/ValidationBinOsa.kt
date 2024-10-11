package com.shellinfo.common.data.local.data.emv_rupay.binary.osa_bin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ValidationBinOsa(

    var errorCode: Byte?,
    var productType: Byte,
    var trxDateTime: ByteArray, // 3 bytes
    var stationCode: ByteArray, // 2 bytes
    var trxStatusAndRfu: Byte // 4 bits for trxStatus, 4 bits for RFU
)
