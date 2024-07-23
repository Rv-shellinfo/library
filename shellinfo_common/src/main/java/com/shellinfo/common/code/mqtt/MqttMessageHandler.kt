package com.shellinfo.common.code.mqtt

import com.shellinfo.common.code.ConfigMaster
import com.shellinfo.common.code.enums.EquipmentType
import com.shellinfo.common.code.enums.MqttTopicType
import com.shellinfo.common.data.local.data.mqtt.BaseMessageMqtt
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MqttMessageHandler @Inject constructor() {

    @Inject
    lateinit var configMaster: ConfigMaster

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

            }

            MqttTopicType.LOG_STATUS ->{

            }

            MqttTopicType.CONFIG_UPDATE ->{

            }

            MqttTopicType.FIRMWARE_UPDATE ->{

            }

            MqttTopicType.KEY_INJECTION ->{

            }

            MqttTopicType.DEVICE_CONTROL_COMMANDS ->{

            }

            MqttTopicType.SPECIAL_MODES_COMMANDS ->{

            }

            MqttTopicType.PARAMETER_TOPICS ->{

            }

            else -> {}
        }
    }

    /**
     * Method to validate the MQTT message before processing
     */
    private fun validateMqttMessage(message: BaseMessageMqtt<*>?): Boolean {
        message ?: return false  // Return false if message is null

        val deviceGroupType = EquipmentType.fromEquipment(configMaster.equipment_group_name)?.type

        return deviceGroupType == message.equipmentGroupName &&
                (message.lineId == 99 || message.lineId == configMaster.line_id) &&
                (message.stationId == 99 || message.stationId == configMaster.station_id) &&
                (message.isAllEquipments || message.equipment_id.contains(configMaster.equipment_id))
    }

}