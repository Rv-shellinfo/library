package com.shellinfo.common.data.local.data.emv_rupay.binary.osa_bin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeneralBinOsa(

    val versionNumber: Byte,
    val languageInfo: Byte, // 5 bits for languageInfo, 1 bit service status, 2 bits for rfu
)
