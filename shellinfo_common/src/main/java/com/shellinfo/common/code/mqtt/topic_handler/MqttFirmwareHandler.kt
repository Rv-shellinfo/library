package com.shellinfo.common.code.mqtt.topic_handler

import com.shellinfo.common.data.local.data.mqtt.FirmwareUpdateMessage
import com.shellinfo.common.data.shared.SharedDataManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MqttFirmwareHandler @Inject constructor(
    private val sharedDataManager: SharedDataManager
) {

    /**
     * Method to handle the firmware update
     */
    fun handleFirmwareUpdate(message:FirmwareUpdateMessage){

    }
}