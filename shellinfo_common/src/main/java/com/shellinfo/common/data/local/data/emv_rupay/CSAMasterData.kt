package com.shellinfo.common.data.local.data.emv_rupay

import com.shellinfo.common.data.local.data.emv_rupay.binary.csa_bin.CsaBin
import com.shellinfo.common.data.local.data.emv_rupay.display.CSADataDisplay
import com.shellinfo.common.data.local.data.emv_rupay.raw.CSADataRaw
import com.shellinfo.common.data.local.data.ipc.BF200Data
import com.shellinfo.common.data.local.data.ipc.RupayCardDataRead
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class CSAMasterData(
    var csaBinData: CsaBin?=null,
    var csaUpdatedBinData:CsaBin?=null,
    var csaDisplayData: CSADataDisplay? = null,
    var tagsData: RupayCardDataRead? =null,
    var bf200Data: BF200Data? =null,
    var rupayMessage: RupayMessage? =RupayMessage(),
    var csaDataRaw: CSADataRaw?=null
)