package com.shellinfo.common.code.enums

enum class MqttTopicType(val topic: String) {

    //consumer messages from MQTT
    OTA_UPDATE("MQTT_OTA_UPDATE"),
    LOG_STATUS("LOG_STATUS"),
    CONFIG_UPDATE("CONFIG_UPDATE"),
    FIRMWARE_UPDATE("FIRMWARE_UPDATE"),
    KEY_INJECTION("KEY_INJECTION"),
    DEVICE_CONTROL_COMMANDS("DEVICE_CONTROL_COMMANDS"),
    SPECIAL_MODES_COMMANDS("SPECIAL_MODES_COMMANDS"),
    PARAMETER_TOPICS("PARAMETER_TOPICS"),

    //Publish message for MQTT acknowledgment
    OTA_UPDATE_ACK("OTA_UPDATE_ACK"),
    LOG_STATUS_ACK("LOG_STATUS_ACK"),
    CONFIG_UPDATE_ACK("CONFIG_UPDATE_ACK"),
    FIRMWARE_UPDATE_ACK("FIRMWARE_UPDATE_ACK"),
    KEY_INJECTION_ACK("KEY_INJECTION_ACK"),
    DEVICE_CONTROL_COMMANDS_ACK("DEVICE_CONTROL_COMMANDS_ACK"),
    SPECIAL_MODES_COMMANDS_ACK("SPECIAL_MODES_COMMANDS_ACK"),
    PARAMETER_TOPICS_ACK("PARAMETER_TOPICS_ACK"),

    //Transaction message at station level
    STATION_TRANSACTION("STATION_TRANSACTION")
    ;



    companion object {
        fun fromTopic(topicString: String?): MqttTopicType? {
            return values().find { it.topic == topicString }
        }
    }
}

