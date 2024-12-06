package com.shellinfo.common.code.mqtt.topic_handler

import com.shellinfo.common.data.local.data.mqtt.KeyInjectionMessage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MqttKeyInjectionHandler @Inject constructor() {

    /**
     * Method to handle the reader key injection
     */
    fun handleKeyInjection(message:KeyInjectionMessage){

    }
}