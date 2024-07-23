package com.shellinfo.common.code.enums

enum class MqttTopicType(val topic: String) {

    OTA_UPDATE("OTA_UPDATE"),
    LOG_STATUS("LOG_STATUS"),
    CONFIG_UPDATE("CONFIG_UPDATE"),
    FIRMWARE_UPDATE("FIRMWARE_UPDATE"),
    KEY_INJECTION("KEY_INJECTION"),
    DEVICE_CONTROL_COMMANDS("DEVICE_CONTROL_COMMANDS"),
    SPECIAL_MODES_COMMANDS("SPECIAL_MODES_COMMANDS"),
    PARAMETER_TOPICS("PARAMETER_TOPICS");

    companion object {
        fun fromTopic(topicString: String?): MqttTopicType? {
            return values().find { it.topic == topicString }
        }
    }
}

