package com.shellinfo.common.code.mqtt

import com.shellinfo.common.code.enums.EquipmentType
import com.shellinfo.common.code.enums.MqttTopicType
import com.shellinfo.common.code.mqtt.topic_handler.MqttConfigHandler
import com.shellinfo.common.code.mqtt.topic_handler.modes.MqttDeviceControlHandler
import com.shellinfo.common.code.mqtt.topic_handler.MqttFirmwareHandler
import com.shellinfo.common.code.mqtt.topic_handler.MqttLogHandler
import com.shellinfo.common.code.mqtt.topic_handler.MqttOtaHandler
import com.shellinfo.common.code.mqtt.topic_handler.MqttParamsHandler
import com.shellinfo.common.code.mqtt.topic_handler.modes.MqttSpecialModesHandler
import com.shellinfo.common.data.local.data.mqtt.BaseMessageMqtt
import com.shellinfo.common.data.local.data.mqtt.FirmwareUpdateMessage
import com.shellinfo.common.data.local.data.mqtt.LogStatusMessage
import com.shellinfo.common.data.local.data.mqtt.MqttData
import com.shellinfo.common.data.local.data.mqtt.OtaUpdateMessage
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.data.shared.SharedDataManager
import com.shellinfo.common.utils.SpConstants
import com.squareup.moshi.JsonAdapter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MqttMessageHandler @Inject constructor(
    private val spUtils:SharedPreferenceUtil,
    private val mqttOtaHandler: MqttOtaHandler,
    private val mqttSpecialModesHandler: MqttSpecialModesHandler,
    private val mqttDeviceControlHandler: MqttDeviceControlHandler,
    private val mqttConfigHandler: MqttConfigHandler,
    private val mqttFirmwareHandler: MqttFirmwareHandler,
    private val mqttLogHandler: MqttLogHandler,
    private val mqttParamsHandler: MqttParamsHandler,
    private val sharedDataManager: SharedDataManager,
    private val mqttMessageAdapter: JsonAdapter<BaseMessageMqtt<MqttData>>,
) {

    //mqtt manager
    private lateinit var mqttManager: MQTTManager

    fun setMqttManager(mqttManager: MQTTManager){
        this.mqttManager = mqttManager
    }


    /**
     * Method to handle the MQTT message
     */
    fun consumeMessage(topic: MqttTopicType?, message: BaseMessageMqtt<*>?){

        //first validate the mqtt message
        if(!validateMqttMessage(message)){
            return
        }

        when(topic){

            MqttTopicType.OTA_UPDATE ->{
                //convert message to json string
                val jsonString = mqttMessageAdapter.toJson(message)

                //save message
                spUtils.savePreference(SpConstants.UPDATE_ACK_MESSAGE,jsonString)

                //handle ota message
                mqttOtaHandler.handleOta(message?.data as OtaUpdateMessage)
            }

            MqttTopicType.LOG_STATUS ->{
                mqttLogHandler.handleLogs(message?.data as LogStatusMessage)
            }

            MqttTopicType.CONFIG_UPDATE ->{
            }

            MqttTopicType.FIRMWARE_UPDATE ->{
                mqttFirmwareHandler.handleFirmwareUpdate(message?.data as FirmwareUpdateMessage)
            }

            MqttTopicType.KEY_INJECTION ->{

            }

            MqttTopicType.DEVICE_CONTROL_COMMAND ->{
                mqttDeviceControlHandler.handleDeviceControlCommands(message!!,mqttManager)
            }

            MqttTopicType.SPECIAL_MODE_COMMAND ->{
                mqttSpecialModesHandler.handleSpecialModeCommands(message!!,mqttManager)
            }

            MqttTopicType.PARAMETER ->{

            }

            MqttTopicType.SLE_DATABASE_STATUS->{

            }

            MqttTopicType.SLE_MESSAGE->{
                sharedDataManager.sendSleMessage(message!!)
            }

            else -> {}
        }
    }

    /**
     * Method to validate the MQTT message before processing
     */
    private fun validateMqttMessage(message: BaseMessageMqtt<*>?): Boolean {
        message ?: return false  // Return false if message is null

        val deviceGroupType =  EquipmentType.fromEquipment(spUtils.getPreference(SpConstants.DEVICE_TYPE, ""))?.type
        val lineId =  spUtils.getPreference(SpConstants.LINE_ID, "03")
        val stationId =  spUtils.getPreference(SpConstants.STATION_ID, "0301")
        val equipmentId =  spUtils.getPreference(SpConstants.EQUIPMENT_ID, "3001")


        return deviceGroupType == message.equipmentGroupName &&
                (message.lineId == "99" || message.lineId == lineId) &&
                (message.stationId == "99" || message.stationId == stationId) &&
                (message.isAllEquipments || message.equipmentId.contains(equipmentId))
    }

}