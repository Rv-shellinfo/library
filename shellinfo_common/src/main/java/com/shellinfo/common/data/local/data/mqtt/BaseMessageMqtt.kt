package com.shellinfo.common.data.local.data.mqtt

import android.os.Parcelable
import com.shellinfo.common.data.local.data.GenericParceler
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith

@JsonClass(generateAdapter = true)
data class BaseMessageMqtt<T:MqttData>(
    val message_id: String,
    val equipmentGroupId: String,
    val equipmentGroupName: String,
    val lineId: Int,
    val stationId: Int,
    val isAllEquipments: Boolean,
    val equipment_id: List<String>,
    val data: T
)