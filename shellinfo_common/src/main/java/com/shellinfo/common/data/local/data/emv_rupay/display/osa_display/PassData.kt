package com.shellinfo.common.data.local.data.emv_rupay.display.osa_display

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PassData(
    var passType:String,
    var passLimit:String,
    var startDateTime:String,
    var endDate:String,
    var validZoneId:String,
    var validZoneFare:Double,
    var validEntryStationId:String,
    var validExitStationId:String,
    var validEntryStationName:String,
    var validExitStationName:String,
    var tripConsumed:String,
    var lastConsumedDate:String,
    var classType:String,
    var dailyLimit:String,
    var priority:Int
)
