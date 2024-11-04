package com.shellinfo.common.data.local.data.pass

import com.shellinfo.common.code.enums.PassType
import com.shellinfo.common.code.enums.pass.PassParams
import com.shellinfo.common.code.enums.pass.PeriodPassType
import com.shellinfo.common.code.enums.pass.TripPassType

data class PassCreateRequest(
    val passType: PassType,
    val periodPassType:PeriodPassType,
    val tripPassType:TripPassType,
    val passParams: PassParams,
    val zoneId:Int,
    val zoneAmount:Double,
    val sourceStationId:Int?=0,
    val destStationId:Int?=0
)

data class PassRequest(
    val productType:Int,
    val isZonePass:Boolean?=false,
    val zoneId:Int?=99,
    val tripLimitId:Int?=99,
    val dailyLimitId:Int?=99,
    val sourceStationId:Int?=99,
    val destStationId:Int?=99
)