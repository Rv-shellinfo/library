package com.shellinfo.common.code.usb

import abbasi.android.filelogger.FileLogger
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import com.shellinfo.common.code.enums.MqttAckTopicType
import com.shellinfo.common.code.enums.MqttTopicType
import com.shellinfo.common.code.enums.SlaveDeviceType
import com.shellinfo.common.code.mqtt.MQTTManager
import com.shellinfo.common.data.local.data.mqtt.BaseMessageMqtt
import com.shellinfo.common.data.local.data.mqtt.SlaveDeviceStatus
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.utils.Constants.ACTION_USB_PERMISSION
import com.shellinfo.common.utils.DateUtils
import com.shellinfo.common.utils.DeviceVendors.ECU_TVM_VENDOR_ID
import com.shellinfo.common.utils.DeviceVendors.EMV_READER_VENDOR_ID
import com.shellinfo.common.utils.DeviceVendors.PDU_DEVICE_TOM_VENDOR_ID
import com.shellinfo.common.utils.DeviceVendors.PDU_DEVICE_TVM_VENDOR_ID
import com.shellinfo.common.utils.DeviceVendors.POS_DEVICE_VENDOR_ID
import com.shellinfo.common.utils.DeviceVendors.PRINTER_DEVICE_TOM_VENDOR_ID
import com.shellinfo.common.utils.DeviceVendors.PRINTER_DEVICE_TVM_VENDOR_ID
import com.shellinfo.common.utils.DeviceVendors.TOM_QR_SCANNER_VENDOR_ID
import com.shellinfo.common.utils.DeviceVendors.TVM_QR_SCANNER_VENDOR_ID
import com.shellinfo.common.utils.DeviceVendors.VALIDATOR_QR_SCANNER_VENDOR_ID
import com.shellinfo.common.utils.SpConstants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UsbReceiver : BroadcastReceiver() {


    @Inject
    lateinit var mqttManager: MQTTManager

    @Inject
    lateinit var spUtils:SharedPreferenceUtil


    override fun onReceive(context: Context, intent: Intent) {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

        when (intent.action) {

            Intent.ACTION_CONFIGURATION_CHANGED->{
                val config = context.resources.configuration
                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)

                // A QWERTY keyboard is now connected
                val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
                val deviceList = usbManager.deviceList

                // Iterate through connected devices
                for ((_, device) in deviceList) {
                    // Check if the device is likely a keyboard (filter by class/subclass if needed)
                    if (config.keyboard == Configuration.KEYBOARD_QWERTY) {
                        val vendorId = device.vendorId
                        val productId = device.productId
                        val serialNumber = device.serialNumber // This might require permissions

                        sendDeviceConnectionToServer(device!!,true)
                        // Log or use the information
                        Log.d("ConfigChangeReceiver", "Keyboard detected: Vendor=$vendorId, Product=$productId, Serial=$serialNumber")
                    }
                }
                if (config.keyboard == Configuration.KEYBOARD_QWERTY) {
                   // sendDeviceConnectionToServer(device!!,true)
                }

            }
            ACTION_USB_PERMISSION -> {
                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)

                if (granted && device != null) {
                    Log.d("USBReceiver", "Permission granted for device: ${device.deviceName}")
                    sendDeviceConnectionToServer(device,true)

                } else {
                    Log.d("USBReceiver", "Permission denied for device")
                }
            }

            UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                if (device != null) {
                    sendDeviceConnectionToServer(device,true)
//                    Log.d("USBReceiver", "Device attached: ${device.deviceName}")
//                    if (!usbManager.hasPermission(device)) {
//                        val permissionIntent = PendingIntent.getBroadcast(
//                            context, 0, Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//                        )
//                        usbManager.requestPermission(device, permissionIntent)
//                    } else {
//                        sendDeviceConnectionToServer(device,true)
//                    }
                }
            }

            UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                if (device != null) {
                    Log.d("USBReceiver", "Device detached: ${device.deviceName}")
                    sendDeviceConnectionToServer(device,false)
                }
            }
        }
    }



     fun sendDeviceConnectionToServer(device: UsbDevice, isConnected:Boolean) {
        val vendorId = device.vendorId
        val productId = device.productId
        var slaveDeviceStatus:SlaveDeviceStatus? = null
        Log.d("UsbReceiver", "Device Connected: VID=$vendorId, PID=$productId")

        when (vendorId) {
            EMV_READER_VENDOR_ID -> {
                slaveDeviceStatus = getSlaveDeviceStatus(SlaveDeviceType.EMV_READER.type,device.serialNumber!!,isConnected)
            }
            VALIDATOR_QR_SCANNER_VENDOR_ID -> {
                slaveDeviceStatus = getSlaveDeviceStatus(SlaveDeviceType.QR_READER_VALIDATOR.type,device.serialNumber!!,isConnected)
            }

            TOM_QR_SCANNER_VENDOR_ID -> {
                slaveDeviceStatus = getSlaveDeviceStatus(SlaveDeviceType.QR_READER_TOM.type,device.serialNumber!!,isConnected)
            }
            TVM_QR_SCANNER_VENDOR_ID -> {
                slaveDeviceStatus = getSlaveDeviceStatus(SlaveDeviceType.QR_READER_TVM.type,device.serialNumber!!,isConnected)
            }
            POS_DEVICE_VENDOR_ID -> {
                slaveDeviceStatus = getSlaveDeviceStatus(SlaveDeviceType.POS_DEVICE.type,device.serialNumber!!,isConnected)
            }
            PDU_DEVICE_TVM_VENDOR_ID -> {
                slaveDeviceStatus = getSlaveDeviceStatus(SlaveDeviceType.PDU_DEVICE_TVM.type,device.serialNumber!!,isConnected)
            }
            PDU_DEVICE_TOM_VENDOR_ID -> {
                slaveDeviceStatus = getSlaveDeviceStatus(SlaveDeviceType.PDU_DEVICE_TOM.type,device.serialNumber!!,isConnected)
            }
            PRINTER_DEVICE_TOM_VENDOR_ID -> {
                slaveDeviceStatus = getSlaveDeviceStatus(SlaveDeviceType.PRINTER_TOM.type,device.serialNumber!!,isConnected)
            }
            PRINTER_DEVICE_TVM_VENDOR_ID -> {
                slaveDeviceStatus = getSlaveDeviceStatus(SlaveDeviceType.PRINTER_TVM.type,device.serialNumber!!,isConnected)
            }
            ECU_TVM_VENDOR_ID -> {
                slaveDeviceStatus = getSlaveDeviceStatus(SlaveDeviceType.ECU.type,device.serialNumber!!,isConnected)
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

    fun getSlaveDeviceStatus(deviceType:Int,serial:String,isConnect:Boolean):SlaveDeviceStatus{

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

     fun getSlaveDeviceMqttMessage(slaveDeviceStatus: SlaveDeviceStatus):BaseMessageMqtt<SlaveDeviceStatus>{

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

}