package com.shellinfo.common.data.remote.response.model.pass

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class PassRequest(
    val productType:Int,
    val productCode:String?="",
    val isZonePass:Boolean?=false,
    val zoneId:Int?=99,
    val zoneAmount:Double?=99.00,
    val passLimitId:Int?=99,
    val passLimitValue:Int?=99,
    val dailyLimitId:Int?=99,
    val dailyLimitValue:Int?=99,
    val sourceStationId:String?="99",
    val destStationId:String?="99",
    var startDateTime:String?="",
    var expiryDate:String?="",
    val amount:Double? =0.0,
    val bankDetail:BankTransactionDetail
)