package com.shellinfo.common.code.printer

import android.content.Context
import android.graphics.Bitmap
import android.os.RemoteException
import android.widget.Toast
import com.sunmi.peripheral.printer.InnerPrinterCallback
import com.sunmi.peripheral.printer.InnerPrinterException
import com.sunmi.peripheral.printer.InnerPrinterManager
import com.sunmi.peripheral.printer.SunmiPrinterService
import com.sunmi.peripheral.printer.WoyouConsts

class SunmiPrinter: PrinterActions{
    var NoSunmiPrinter = 0x00000000
    var CheckSunmiPrinter = 0x00000001
    var FoundSunmiPrinter = 0x00000002
    var LostSunmiPrinter = 0x00000003
    lateinit var mContext: Context

    /**
     * sunmiPrinter means checking the printer connection status
     */
    var sunmiPrinter = CheckSunmiPrinter

    /**
     * SunmiPrinterService for API
     */
    private var sunmiPrinterService: SunmiPrinterService? = null


    private val innerPrinterCallback: InnerPrinterCallback = object : InnerPrinterCallback() {
        override fun onConnected(service: SunmiPrinterService) {
            sunmiPrinterService = service
            checkSunmiPrinterService(service)
        }

        override fun onDisconnected() {
            sunmiPrinterService = null
            sunmiPrinter = LostSunmiPrinter
        }
    }

    fun initSunmiPrinterService(context: Context?) {
        try {
            this.mContext = context!!
            val ret = InnerPrinterManager.getInstance().bindService(
                context,
                innerPrinterCallback
            )
            if (!ret) {
                sunmiPrinter = NoSunmiPrinter
            }
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
        }
    }

    /**
     * deInit sunmi print service
     */
    fun deInitSunmiPrinterService(context: Context?) {
        try {
            if (sunmiPrinterService != null) {
                InnerPrinterManager.getInstance().unBindService(context, innerPrinterCallback)
                sunmiPrinterService = null
                sunmiPrinter = LostSunmiPrinter
            }
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
        }
    }

    /**
     * Check the printer connection,
     * like some devices do not have a printer but need to be connected to the cash drawer through a print service
     */
    private fun checkSunmiPrinterService(service: SunmiPrinterService) {
        var ret = false
        try {
            ret = InnerPrinterManager.getInstance().hasPrinter(service)
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
        }
        sunmiPrinter = if (ret) FoundSunmiPrinter else NoSunmiPrinter
    }

    /**
     * Some conditions can cause interface calls to fail
     * For example: the version is too low、device does not support
     * You can see [ExceptionConst]
     * So you have to handle these exceptions
     */
    private fun handleRemoteException(e: RemoteException) {
        //TODO process when get one exception
    }

    /**
     * send esc cmd
     */
    fun sendRawData(data: ByteArray?) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.sendRAWData(data, null)
        } catch (e: RemoteException) {
            handleRemoteException(e)
        }
    }

    /**
     * Printer cuts paper and throws exception on machines without a cutter
     */
    fun cutpaper() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.cutPaper(null)
        } catch (e: RemoteException) {
            handleRemoteException(e)
        }
    }


    override fun initPrinterService(context: Context?) {
        try {
            this.mContext = context!!
            val ret = InnerPrinterManager.getInstance().bindService(
                context,
                innerPrinterCallback
            )
            if (!ret) {
                sunmiPrinter = NoSunmiPrinter
            }
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
        }
    }

    override fun removePrinterService(context: Context?) {
        try {
            if (sunmiPrinterService != null) {
                InnerPrinterManager.getInstance().unBindService(context, innerPrinterCallback)
                sunmiPrinterService = null
                sunmiPrinter = LostSunmiPrinter
            }
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
        }
    }

    override fun print3Line() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.lineWrap(3, null)
        } catch (e: RemoteException) {
            handleRemoteException(e)
        }
    }

    override fun getPrinterSerialNumber(): String? {
        return if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            ""
        } else try {
            sunmiPrinterService!!.printerSerialNo
        } catch (e: RemoteException) {
            handleRemoteException(e)
            ""
        }
    }

    override fun getPrinterModel(): String? {
        return if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            ""
        } else try {
            sunmiPrinterService!!.printerModal
        } catch (e: RemoteException) {
            handleRemoteException(e)
            ""
        }
    }

    override fun getPrinterVersion(): String? {
        return if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            ""
        } else try {
            sunmiPrinterService!!.printerVersion
        } catch (e: RemoteException) {
            handleRemoteException(e)
            ""
        }
    }

    override fun getPrinterPaperSpecification(): String? {
        return if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            ""
        } else try {
            if (sunmiPrinterService!!.printerPaper == 1) "58mm" else "80mm"
        } catch (e: RemoteException) {
            handleRemoteException(e)
            ""
        }
    }

    override fun setAlign(align: Int) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.setAlignment(align, null)
        } catch (e: RemoteException) {
            handleRemoteException(e)
        }
    }

    override fun printText(
        content: String?,
        size: Float,
        isBold: Boolean,
        isUnderLine: Boolean,
        typeface: String?
    ) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            Toast.makeText(mContext, "Printer not available!", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            try {
                sunmiPrinterService!!.setPrinterStyle(
                    WoyouConsts.ENABLE_BOLD,
                    if (isBold) WoyouConsts.ENABLE else WoyouConsts.DISABLE
                )
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            try {
                sunmiPrinterService!!.setPrinterStyle(
                    WoyouConsts.ENABLE_UNDERLINE,
                    if (isUnderLine) WoyouConsts.ENABLE else WoyouConsts.DISABLE
                )
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            sunmiPrinterService!!.printTextWithFont(content, typeface, size, null)
            Toast.makeText(mContext, "Print Successful!", Toast.LENGTH_SHORT).show()

        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun printBarCode(
        data: String?,
        symbology: Int,
        height: Int,
        width: Int,
        textposition: Int
    ) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.printBarCode(data, symbology, height, width, textposition, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun printQr(data: String?, modulesize: Int, errorlevel: Int) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.printQRCode(data, modulesize, errorlevel, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun printTable(txts: Array<String?>?, width: IntArray?, align: IntArray?) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            sunmiPrinterService!!.printColumnsString(txts, width, align, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun printBitmap(bitmap: Bitmap?, orientation: Int?) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return
        }
        try {
            if (orientation == 0) {
                sunmiPrinterService!!.printBitmap(bitmap, null)

            } else {
                sunmiPrinterService!!.printBitmap(bitmap, null)

            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun getPrinterStatus(context: Context?): String? {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return ""
        }
        var result = "Interface is too low to implement interface"
        try {
            val res = sunmiPrinterService!!.updatePrinterState()
            when (res) {
                1 -> result = "printer is running"
                2 -> result = "printer found but still initializing"
                3 -> result = "printer hardware interface is abnormal and needs to be reprinted"
                4 -> result = "printer is out of paper"
                5 -> result = "printer is overheating"
                6 -> result = "printer's cover is not closed"
                7 -> result = "printer's cutter is abnormal"
                8 -> result = "printer's cutter is normal"
                9 -> result = "not found black mark paper"
                505 -> result = "printer does not exist"
                else -> {}
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

        return result
    }
}