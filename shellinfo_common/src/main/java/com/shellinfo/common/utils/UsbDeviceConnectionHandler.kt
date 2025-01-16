package com.shellinfo.common.utils

import abbasi.android.filelogger.FileLogger
import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import com.shellinfo.common.code.enums.MqttTopicType
import com.shellinfo.common.code.enums.SlaveDeviceType
import com.shellinfo.common.code.mqtt.MQTTManager
import com.shellinfo.common.code.serial_comm.SerialCommManager
import com.shellinfo.common.data.local.data.mqtt.BaseMessageMqtt
import com.shellinfo.common.data.local.data.mqtt.SlaveDeviceStatus
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsbDeviceConnectionHandler @Inject constructor(
    private val mqttManager: MQTTManager,
    private val spUtils: SharedPreferenceUtil,
    private val serialCommManager: SerialCommManager
){

    fun sendDeviceConnectionToServer(device: UsbDevice, isConnected:Boolean, context: Context) {
        val vendorId = device.vendorId
        val productId = device.productId
        var slaveDeviceStatus: SlaveDeviceStatus? = null
        Log.d("UsbReceiver", "Device Connected: VID=$vendorId, PID=$productId")

        when (vendorId) {
            DeviceVendors.EMV_READER_VENDOR_ID -> {
                slaveDeviceStatus = getSlaveDeviceStatus(SlaveDeviceType.EMV_READER.type,device.serialNumber!!,isConnected)
            }
            DeviceVendors.VALIDATOR_QR_SCANNER_VENDOR_ID -> {
                slaveDeviceStatus = getSlaveDeviceStatus(SlaveDeviceType.QR_READER_VALIDATOR.type,device.serialNumber!!,isConnected)
            }

            DeviceVendors.TOM_QR_SCANNER_VENDOR_ID -> {
                slaveDeviceStatus = getSlaveDeviceStatus(SlaveDeviceType.QR_READER_TOM.type,device.serialNumber!!,isConnected)
            }
            DeviceVendors.TVM_QR_SCANNER_VENDOR_ID -> {
                slaveDeviceStatus = getSlaveDeviceStatus(SlaveDeviceType.QR_READER_TVM.type,device.serialNumber!!,isConnected)
            }
            DeviceVendors.POS_DEVICE_VENDOR_ID -> {
                slaveDeviceStatus = getSlaveDeviceStatus(SlaveDeviceType.POS_DEVICE.type,device.serialNumber!!,isConnected)
            }
            DeviceVendors.PDU_DEVICE_TVM_VENDOR_ID -> {
                slaveDeviceStatus = getSlaveDeviceStatus(SlaveDeviceType.PDU_DEVICE_TVM.type,device.serialNumber!!,isConnected)
            }
            DeviceVendors.PDU_DEVICE_TOM_VENDOR_ID -> {
                slaveDeviceStatus = getSlaveDeviceStatus(SlaveDeviceType.PDU_DEVICE_TOM.type,device.serialNumber!!,isConnected)
            }
            DeviceVendors.PRINTER_DEVICE_TOM_VENDOR_ID -> {
                slaveDeviceStatus = getSlaveDeviceStatus(SlaveDeviceType.PRINTER_TOM.type,device.serialNumber!!,isConnected)
            }
            DeviceVendors.PRINTER_DEVICE_TVM_VENDOR_ID -> {
                slaveDeviceStatus = getSlaveDeviceStatus(SlaveDeviceType.PRINTER_TVM.type,device.serialNumber!!,isConnected)
            }
            DeviceVendors.ECU_VENDOR_ID -> {
                slaveDeviceStatus = getSlaveDeviceStatus(SlaveDeviceType.ECU.type,device.serialNumber!!,isConnected)

                if(isConnected){
                    val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

                    val connection = usbManager.openDevice(device)
                    if (connection != null) {
                        // Connection successful
                        serialCommManager.openPort(context,device,connection)
                    } else {
                        // Connection failed
                        Log.e("USB", "Failed to open device")
                    }
                }
            }

            else -> {
                Log.d("UsbReceiver", "Unknown device connected!")
            }
        }

        slaveDeviceStatus?.let {

            val baseMqttMsg = getSlaveDeviceMqttMessage(it)

            //convert message to json string
            val jsonString = mqttManager.mqttMessageAdapter.toJson(baseMqttMsg)

            //log ack message
            FileLogger.i("ACK_MESSAGE",jsonString)

            //publish the message for slave device status
            mqttManager.publish(MqttTopicType.SLAVE_DEVICE_STATUS.topic,jsonString)

        }
    }

    fun getSlaveDeviceStatus(deviceType:Int,serial:String,isConnect:Boolean): SlaveDeviceStatus {

        val deviceLocation = spUtils.getPreference(SpConstants.READER_LOCATION,"entry").lowercase()

        return SlaveDeviceStatus(
            MqttTopicType.SLAVE_DEVICE_STATUS.name,
            deviceType,
            SlaveDeviceType.fromSlaveDeviceType(deviceType)!!.name,
            serial,
            deviceLocation,
            isConnect
        )
    }

    fun getSlaveDeviceMqttMessage(slaveDeviceStatus: SlaveDeviceStatus): BaseMessageMqtt<SlaveDeviceStatus> {

        val grpId= spUtils.getPreference(SpConstants.EQUIPMENT_GROUP_ID,"04")
        val grpName= "VALIDATOR"
        val lineId = spUtils.getPreference(SpConstants.LINE_ID,"04")
        val stationId = spUtils.getPreference(SpConstants.STATION_ID,"0422")
        val equipmentId = spUtils.getPreference(SpConstants.EQUIPMENT_ID,"1005")

        val baseMqttMessage = BaseMessageMqtt(
            message = MqttTopicType.SLAVE_DEVICE_STATUS.name,
            messageId = 10,
            equipmentGroupId = grpId,
            equipmentGroupName = grpName,
            lineId = lineId,
            stationId = stationId,
            isAllEquipments = false,
            equipmentId = listOf(equipmentId),
            applyDateTime = DateUtils.getSysDateTime(),
            activationDateTime = DateUtils.getSysDateTime(),
            data = slaveDeviceStatus
        )

        return baseMqttMessage
    }

    fun sendTrxSuccessToEcu(){
        serialCommManager.sendTrxSuccessToEcu()
    }

    fun sendTrxErrorToEcu(){
        serialCommManager.sendTrxErrorToEcu()
    }
}