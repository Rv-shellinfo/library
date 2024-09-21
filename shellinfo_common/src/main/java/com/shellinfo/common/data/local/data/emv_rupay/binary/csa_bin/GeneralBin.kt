package com.shellinfo.common.data.local.data.emv_rupay.binary.csa_bin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeneralBin(
    val versionNumber: Byte,
    val languageInfo: Byte, // 5 bits for languageInfo, 3 bits for rfu
)
