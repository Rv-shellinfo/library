package com.shellinfo.common.data.remote.response.model.config.device_config

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeviceConfigResponse(
    @Json(name ="serial_no") val serialNumber:String,
    @Json(name ="equipment_id") val equipmentId:String,
    @Json(name ="equipment_group_id") val equipmentGroupId:String,
    @Json(name ="terminal_id") val terminalId:String,
    @Json(name ="line_id") val lineId:String,
    @Json(name ="station_id") val stationId:String,
    @Json(name ="location") val location:String,
    @Json(name ="activation_code") val activationCode:String,
    @Json(name ="cc_ip_address") val ccIpAddress:String,
    @Json(name ="sc_ip_address") val scIpAddress:String,
    @Json(name ="cc_port") val ccPort:String,
    @Json(name ="sc_port") val scPort:String,
    @Json(name ="slave_device_restrict") val slaveDeviceRestrict:Boolean,
    @Json(name ="slave_device_config_url") val slaveDeviceConfigUrl:String,
    @Json(name ="rupay_config_url") val rupayConfigUrl:String,
    @Json(name ="general_config_url") val generalConfigUrl:String,
    @Json(name ="version") val version:String

)
