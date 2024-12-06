package com.shellinfo.common.code.mqtt.topic_handler

import com.shellinfo.common.data.shared.SharedDataManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MqttSleDatabaseStatusHandler @Inject constructor(
    private val sharedDataManager: SharedDataManager
){
}