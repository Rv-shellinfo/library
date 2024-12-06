package com.shellinfo.common.code.enums

enum class MqttAckTopicType(val topic: Int) {

    //Publish message for MQTT acknowledgment
    OTA_UPDATE_ACK(1),
    LOG_STATUS_ACK(2),
    CONFIG_UPDATE_ACK(3),
    FIRMWARE_UPDATE_ACK(4),
    DEVICE_CONTROL_COMMAND_ACK(5),
    SPECIAL_MODES_COMMAND_ACK(6),
    PARAMETER_ACK(7),
    SLE_DATABASE_STATUS_ACK(8),
    SLE_MESSAGE_ACK(9);

    companion object {
        fun fromAckTopic(topic: Int?): MqttAckTopicType? {
            return MqttAckTopicType.values().find { it.topic == topic }
        }
    }
}