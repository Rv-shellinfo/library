package com.shellinfo.common.data.local.data.mqtt

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BaseMessageMqtt<out T:MqttData>(
    val message: String,
    val messageId: Int,
    val equipmentGroupId: String,
    val equipmentGroupName: String,
    val lineId: String,
    val stationId: String,
    val isAllEquipments: Boolean,
    val equipmentId: List<String>,
    val applyDateTime: String,
    val activationDateTime: String,
    val data: T
)