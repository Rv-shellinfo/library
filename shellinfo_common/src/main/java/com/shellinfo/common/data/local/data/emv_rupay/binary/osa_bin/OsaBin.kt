package com.shellinfo.common.data.local.data.emv_rupay.binary.osa_bin

import com.shellinfo.common.data.local.data.emv_rupay.HistoryQueue
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OsaBin(
    var generalInfo: GeneralBinOsa,
    var validationData: ValidationBinOsa,
    var history: HistoryQueue<HistoryBinOsa>,
    var passes: List<PassBin>,
    var rfu: ByteArray ,                   // 5 bytes
)
