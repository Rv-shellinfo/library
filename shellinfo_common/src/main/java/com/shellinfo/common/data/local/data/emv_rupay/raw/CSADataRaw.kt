package com.shellinfo.common.data.local.data.emv_rupay.raw

import com.shellinfo.common.data.local.data.emv_rupay.HistoryQueue


/**
 * This is actual CSA data in which the values are HexaDecimal
 *
 * @property serviceData:(0 to 64) Service related data
 * @property generalData:(64 to 68) General data
 * @property validationData :(68 to 106) Validation Data (need to modify on every transaction)
 * @property historyData : H1(106 to 140) History Data 1
 *                       : H2(140 to 174) History Data 2
 *                       : H3(174 to 208) History Data 3
 *                       : H4(208 to 242) History Data 4
 * @property rfu: (242 to 256)
 */
data class CSADataRaw(
    var serviceData: ServiceData,
    var generalData: GeneralData,
    var validationData: ValidationData,
    var historyData: HistoryQueue<HistoryData>,
    var rfu:String =""
)
