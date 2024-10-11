package com.shellinfo.common.data.local.data.emv_rupay.display.osa_display

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PassData(
    var passType:String,
    var passLimit:String,
    var startDateTime:String,
    var endDate:String,
    var validZoneId:String,
    var validEntryStationId:String,
    var validExitStationId:String,
    var tripConsumed:String,
    var classType:String,
    var dailyLimit:String,
    var priority:Int
)
