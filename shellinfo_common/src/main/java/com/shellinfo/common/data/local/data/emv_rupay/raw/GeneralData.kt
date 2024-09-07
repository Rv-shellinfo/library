package com.shellinfo.common.data.local.data.emv_rupay.raw

/**
 * General Data  Index (64 to 68)
 *  @property version - Version Number (64 to 66)
 *  @property langRfu - Language and RFU (66 to 68)
 *  @constructor Creates an empty CSA General Data
 */
data class GeneralData(
    var version:String = "",
    var langRfu:String = "",
)

