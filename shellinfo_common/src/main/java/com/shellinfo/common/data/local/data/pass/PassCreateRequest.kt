package com.shellinfo.common.data.local.data.pass

import com.shellinfo.common.code.enums.PassType

data class PassCreateRequest(
    val passType: PassType,
    val sourceStationId:Int?=0,
    val destStationId:Int?=0
)