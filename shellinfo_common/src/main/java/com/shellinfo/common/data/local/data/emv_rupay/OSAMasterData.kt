package com.shellinfo.common.data.local.data.emv_rupay

import com.shellinfo.common.data.local.data.emv_rupay.binary.osa_bin.OsaBin
import com.shellinfo.common.data.local.data.emv_rupay.display.osa_display.OSADataDisplay
import com.shellinfo.common.data.local.data.ipc.BF200Data
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OSAMasterData(
    var osaBinData:OsaBin?=null,
    var osaUpdatedBinData:OsaBin?=null,
    var osaDisplayData:OSADataDisplay?=null,
    var bf200Data: BF200Data? =null,
    var rupayMessage: RupayMessage? =RupayMessage()
)
