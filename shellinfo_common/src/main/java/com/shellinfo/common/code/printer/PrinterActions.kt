package com.shellinfo.common.code.printer

import android.content.Context
import android.graphics.Bitmap

interface PrinterActions {

    fun initPrinterService(context: Context?)

    fun removePrinterService(context: Context?)

    fun print3Line()

    fun getPrinterSerialNumber():String?

    fun getPrinterModel():String?

    fun getPrinterVersion():String?

    fun getPrinterPaperSpecification():String?

    fun setAlign(align:Int)

    fun printText(content: String?, size: Float, isBold: Boolean, isUnderLine: Boolean, typeface: String?)

    fun printBarCode(data: String?, symbology: Int, height: Int, width: Int, textposition: Int)

    fun printQr(data: String?, modulesize: Int, errorlevel: Int)

    fun printTable(txts: Array<String?>?, width: IntArray?, align: IntArray?)

    fun printBitmap(bitmap: Bitmap?, orientation: Int?)

    fun getPrinterStatus(context: Context?) :String?

}