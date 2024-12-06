package com.shellinfo.common.code.mqtt.topic_handler

import com.shellinfo.common.data.local.data.mqtt.ConfigUpdateMessage
import com.shellinfo.common.data.shared.SharedDataManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MqttConfigHandler @Inject constructor(
    private val sharedDataManager: SharedDataManager
) {

    /**
     * method to handle the configurations
     */
    fun handleConfigUpdate(message:ConfigUpdateMessage){

    }
}