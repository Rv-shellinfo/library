package com.shellinfo.common.code.usb

import abbasi.android.filelogger.FileLogger
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
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
import com.shellinfo.common.utils.Constants.ACTION_USB_PERMISSION
import com.shellinfo.common.utils.DateUtils
import com.shellinfo.common.utils.DeviceVendors.ECU_VENDOR_ID
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
import com.shellinfo.common.utils.UsbDeviceConnectionHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UsbReceiver : BroadcastReceiver() {


    @Inject
    lateinit var usbDeviceConnectionHandler: UsbDeviceConnectionHandler

    override fun onReceive(context: Context, intent: Intent) {
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

                        usbDeviceConnectionHandler.sendDeviceConnectionToServer(device!!,true,context)
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
                    usbDeviceConnectionHandler.sendDeviceConnectionToServer(device,true,context)

                } else {
                    Log.d("USBReceiver", "Permission denied for device")
                }
            }

            UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                if (device != null) {
                    usbDeviceConnectionHandler.sendDeviceConnectionToServer(device,true,context)
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
                    usbDeviceConnectionHandler.sendDeviceConnectionToServer(device,false,context)
                }
            }
        }
    }





}