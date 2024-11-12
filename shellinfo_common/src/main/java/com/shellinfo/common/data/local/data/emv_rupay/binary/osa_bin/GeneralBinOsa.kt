package com.shellinfo.common.data.local.data.emv_rupay.binary.osa_bin

import com.squareup.moshi.JsonClass

// Constants for bit manipulation
const val SERVICE_STATUS_BIT_POSITION = 5

@JsonClass(generateAdapter = true)
data class GeneralBinOsa(

    val versionNumber: Byte,
    var languageInfo: Byte, // 5 bits for languageInfo, 1 bit service status, 2 bits for rfu
){

    // Constants for bit manipulation
    companion object {
        private const val SERVICE_STATUS_BIT_POSITION = 5
    }

    // Function to get the service status bit (1 for active, 0 for inactive)
    fun getServiceStatus(): Boolean {
        return ((languageInfo.toInt() shr SERVICE_STATUS_BIT_POSITION) and 1) == 1
    }

    // Function to set the service status bit
    fun setServiceStatus(status: Boolean) {
        languageInfo = if (status) {
            // Set the 6th bit to 1
            (languageInfo.toInt() or (1 shl SERVICE_STATUS_BIT_POSITION)).toByte()
        } else {
            // Set the 6th bit to 0
            (languageInfo.toInt() and (1 shl SERVICE_STATUS_BIT_POSITION).inv()).toByte()
        }
    }
}
