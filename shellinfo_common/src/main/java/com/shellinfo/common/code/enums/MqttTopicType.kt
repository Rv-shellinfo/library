package com.shellinfo.common.code.enums

enum class MqttTopicType(val topic: String) {

    //consumer messages from MQTT
    OTA_UPDATE("OTA_UPDATE"),
    LOG_STATUS("LOG_STATUS"),
    CONFIG_UPDATE("CONFIG_UPDATE"),
    FIRMWARE_UPDATE("FIRMWARE_UPDATE"),
    KEY_INJECTION("KEY_INJECTION"),
    DEVICE_CONTROL_COMMAND("DEVICE_CONTROL_COMMAND"),
    SPECIAL_MODE_COMMAND("SPECIAL_MODE_COMMAND"),
    PARAMETER("PARAMETER"),
    SLE_DATABASE_STATUS("SLE_DATABASE_STATUS"),
    SLE_MESSAGE("SLE_MESSAGE"),
    STATION_TRANSACTION("STATION_TRANSACTION");



    companion object {
        fun fromTopic(topicString: String?): MqttTopicType? {
            return values().find { it.topic == topicString }
        }
    }
}

