package com.shellinfo.common.code.mqtt.topic_handler.modes

import com.shellinfo.common.code.enums.ModeType
import com.shellinfo.common.code.enums.ModeType.*
import com.shellinfo.common.code.mqtt.MQTTManager
import com.shellinfo.common.data.local.data.mqtt.BaseMessageMqtt
import com.shellinfo.common.data.local.data.mqtt.DeviceControlMessage
import com.shellinfo.common.data.local.data.mqtt.SpecialModeMessage
import com.shellinfo.common.data.shared.SharedDataManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MqttSpecialModesHandler @Inject constructor(
    private val sharedDataManager: SharedDataManager,
    private val modeManager: ModeManager
){



    /**
     * Method to handle the special modes commands
     */
    fun handleSpecialModeCommands(message: BaseMessageMqtt<*>,mqttManager: MQTTManager){

        //send data to transit app
        sharedDataManager.sendSpecialModes(message)

        val data = message.data as SpecialModeMessage

        //handle modes
        when(ModeType.getDeviceMode(data.commandTypeId)){

            EMERGENCY_MODE->{
                modeManager.setMode(EMERGENCY_MODE)
            }
            INCIDENT_MODE->{
                modeManager.setMode(INCIDENT_MODE)
            }
            SEQ_OVERRIDE_MODE->{
                modeManager.setMode(SEQ_OVERRIDE_MODE)
            }
            TIME_OVERRIDE_MODE->{
                modeManager.setMode(TIME_OVERRIDE_MODE)
            }
            STATION_CLOSE_MODE->{
                modeManager.setMode(STATION_CLOSE_MODE)
            }
            FARE_BYPASS_ONE_MODE->{
                modeManager.setMode(FARE_BYPASS_ONE_MODE)
            }
            FARE_BYPASS_TWO_MODE->{
                modeManager.setMode(FARE_BYPASS_TWO_MODE)
            }
            DEVICE_CLOSE_MODE->{
                modeManager.setMode(DEVICE_CLOSE_MODE)
            }
            MAINTENANCE_MODE->{
                modeManager.setMode(MAINTENANCE_MODE)
            }
            else->{}
        }

        //send back mqtt ack
        mqttManager.sendMqttAck(message)

    }


    fun setEmergencyMode(){

    }

}