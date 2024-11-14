package com.shellinfo.common.data.local.data

import android.service.controls.DeviceTypes
import com.shellinfo.common.code.enums.ApiMode
import com.shellinfo.common.code.enums.EquipmentType

data class InitData(

    var appId:String,

    var appName: String,

    var appVersionCode:String,

    var appVersionName:String,

    var deviceType: EquipmentType,

    var appType:String,

    var deviceSerial:String,

    var apiMode:ApiMode,

    var stationId:String,
)
