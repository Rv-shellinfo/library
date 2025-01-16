package com.shellinfo.common.code.serial_comm

import abbasi.android.filelogger.FileLogger
import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.os.Parcel
import android.util.Log
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import com.shellinfo.common.data.local.data.GenericParceler.write
import com.shellinfo.common.data.local.data.serial.SerialMessage
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

class SerialCommManager @Inject constructor(): SerialInputOutputManager.Listener{

    private var usbIoManager: SerialInputOutputManager? = null

    private val TAG = SerialCommManager::class.java.simpleName

    private lateinit var usbSerialPort: UsbSerialPort

    /**
     * method to open the port for the serial communication
     */
    fun openPort(context: Context, usbDevice: UsbDevice, connection: UsbDeviceConnection){

        //driver selection
        val driver = UsbSerialProber.getDefaultProber().probeDevice(usbDevice)
        usbSerialPort= driver.ports.get(0)

        //open connection
        usbSerialPort.open(connection)



        try {
            usbSerialPort.setParameters(115200, 8, 1, UsbSerialPort.PARITY_NONE)
        } catch (e: UnsupportedOperationException) {
            FileLogger.e(TAG, e)
        }catch (e: Exception) {
            FileLogger.e(TAG, e)
        }

        usbIoManager = SerialInputOutputManager(usbSerialPort, this)

        sendTrxSuccessToEcu()

    }

    /**
     * Method to send the transaction success message to ECU
     */

    fun sendTrxSuccessToEcu(){

        //bytes array for success message
        val trxSuccessMsg: ByteArray = byteArrayOf(0x03, 0x04, 0x01, 0x01, 0x01, 0x00, 0x02)
        usbIoManager!!.start()
        usbSerialPort.write(trxSuccessMsg,0)
    }

    /**
     * Method to send the error transaction
     */
    fun sendTrxErrorToEcu(){

        //bytes array for error message
        val trxErrorMsg: ByteArray = byteArrayOf(0x03, 0x04, 0x01, 0x01, 0x01, 0x01, 0x02)


        usbIoManager!!.start()
        usbSerialPort.write(trxErrorMsg,0)

    }

    /**
     * Method to get the response
     */
    override fun onNewData(data: ByteArray?) {
        usbIoManager!!.stop()
        Log.e("Received Data",data.toString())


        TODO("Not yet implemented")
    }

    override fun onRunError(e: Exception?) {
        Log.e("Received Data",e.toString())
        TODO("Not yet implemented")
    }


}