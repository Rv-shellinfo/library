package com.shellinfo.common.data.local.data.emv_rupay.binary.csa_bin

data class GeneralBin(
    var versionNumber: Byte =0,
    var languageInfoAndRfu: Byte=0
){
    // Getter for the 5-bit languageInfo
    fun getLanguageInfo(): Int {
        return languageInfoAndRfu.toInt() and 0x1F // Extract lower 5 bits (languageInfo)
    }

    // Setter for the 5-bit languageInfo
    fun setLanguageInfo(languageInfo: Int) {
        languageInfoAndRfu = ((languageInfoAndRfu.toInt() and 0xE0) or (languageInfo and 0x1F)).toByte()
    }

    // Getter for the 3-bit rfu
    fun getRfu(): Int {
        return (languageInfoAndRfu.toInt() shr 5) and 0x07 // Extract the upper 3 bits (rfu)
    }

    // Setter for the 3-bit rfu
    fun setRfu(rfu: Int) {
        languageInfoAndRfu = ((languageInfoAndRfu.toInt() and 0x1F) or ((rfu and 0x07) shl 5)).toByte()
    }
}
