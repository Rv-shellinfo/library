package com.shellinfo.common.data.local.data.emv_rupay.raw

/**
 * Service Data Index(0 to 64)
 *
 * @property serviceIndex - (0,2)
 * @property serviceId - (2,6)
 * @property serviceControl - (6,10)
 * @property kcv - (10,16)
 * @property keyPRMacqKeyIndex - (16,18)
 * @property rfu - (18,26)
 * @property serviceLastUpdateAtc - (26,30)
 * @property serviceLastUpdateDateTime - (30,42)
 * @property serviceAtc - (42,46)
 * @property serviceBalance - (46 to 58) Global Balance
 * @property serviceCurrency - (58,62)
 * @property serviceDataLength - (62,64)
 */
data class ServiceData(
    var serviceIndex:String = "",
    var serviceId:String = "",
    var serviceControl:String = "",
    var kcv:String = "",
    var keyPRMacqKeyIndex:String = "",
    var rfu:String = "",
    var serviceLastUpdateAtc:String = "",
    var serviceLastUpdateDateTime:String = "",
    var serviceAtc:String = "",
    var serviceBalance:String = "",
    var serviceCurrency:String = "",
    var serviceDataLength:String = "",
)
