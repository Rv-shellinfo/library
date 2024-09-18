package com.shellinfo.common.data.local.data.emv_rupay.binary.csa_bin

import com.shellinfo.common.data.local.data.emv_rupay.HistoryQueue
import com.shellinfo.common.data.local.data.emv_rupay.raw.GeneralData
import com.shellinfo.common.data.local.data.emv_rupay.raw.HistoryData
import com.shellinfo.common.data.local.data.emv_rupay.raw.ValidationData

data class CsaBin(
    var generalInfo: GeneralBin,            // 2 bytes
    var validationData: ValidationBin,   // 19 bytes
    var history: HistoryQueue<HistoryBin>,        // 4 elements, each 17 bytes
    var rfu: ByteArray ,                   // 7 bytes
)
