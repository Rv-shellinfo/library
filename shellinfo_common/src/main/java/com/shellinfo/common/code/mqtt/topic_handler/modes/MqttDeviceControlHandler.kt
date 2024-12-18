package com.shellinfo.common.code.mqtt.topic_handler.modes

import com.shellinfo.common.code.enums.DeviceControlCommandType
import com.shellinfo.common.code.enums.ModeType.*
import com.shellinfo.common.code.enums.ModeType
import com.shellinfo.common.code.mqtt.MQTTManager
import com.shellinfo.common.data.local.data.mqtt.BaseMessageMqtt
import com.shellinfo.common.data.local.data.mqtt.DeviceControlMessage
import com.shellinfo.common.data.local.data.mqtt.MqttData
import com.shellinfo.common.data.shared.SharedDataManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MqttDeviceControlHandler @Inject constructor(
    private val sharedDataManager: SharedDataManager,
    private val modeManager: ModeManager
) {

    /**
     * Method to handle the device control commands
     */
    fun handleDeviceControlCommands(message:BaseMessageMqtt<*>,mqttManager: MQTTManager){

        //send data to transit app
        sharedDataManager.sendDeviceControlCommand(message)

        val data = message.data as DeviceControlMessage

        when(ModeType.getDeviceMode(data.commandTypeId)){

            IN_SERVICE_MODE->{
                modeManager.setMode(IN_SERVICE_MODE)
            }
            OUT_OF_SERVICE_MODE->{
                modeManager.setMode(OUT_OF_SERVICE_MODE)
            }

            POWER_SAVING_MODE->{
                modeManager.setMode(POWER_SAVING_MODE)
            }
            TEST_MODE->{
                modeManager.setMode(TEST_MODE)
            }
            FAILURE_MODE->{
                modeManager.setMode(FAILURE_MODE)
            }
            REBOOT->{}
            SHUTDOWN->{}
            else -> {}
        }

        //send back mqtt ack
        //mqttManager.sendMqttAck(message)
    }

    fun setInServiceMode(message:DeviceControlMessage){

    }

    fun setOutOfServiceMode(message:DeviceControlMessage){

    }

    fun setPowerSavingMode(message:DeviceControlMessage){

    }

    fun setTestMode(message:DeviceControlMessage){

    }
}