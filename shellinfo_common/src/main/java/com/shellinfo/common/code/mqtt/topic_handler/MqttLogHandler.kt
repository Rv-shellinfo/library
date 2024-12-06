package com.shellinfo.common.code.mqtt.topic_handler

import com.shellinfo.common.data.local.data.mqtt.LogStatusMessage
import com.shellinfo.common.data.shared.SharedDataManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MqttLogHandler @Inject constructor(
    private val sharedDataManager: SharedDataManager
) {

    /**
     * Method to handle the log status
     */
    fun handleLogs(message:LogStatusMessage){

    }
}