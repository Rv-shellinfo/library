package com.shellinfo.common.code.printer

import android.content.Context
import android.graphics.Bitmap

class PrinterProcessor(private val printerInterface: PrinterActions) {

    fun initPrinterService(context: Context?){
        printerInterface.initPrinterService(context)
    }

    fun removePrinterService(context: Context?){
        printerInterface.removePrinterService(context)
    }

    fun print3Line(){
        printerInterface.print3Line()
    }

    fun getPrinterSerialNumber():String?{
        return printerInterface.getPrinterSerialNumber()
    }

    fun getPrinterModel():String?{
        return printerInterface.getPrinterModel()
    }

    fun getPrinterVersion():String?{
        return printerInterface.getPrinterVersion()
    }

    fun getPrinterPaperSpecification():String?{
        return printerInterface.getPrinterPaperSpecification()
    }

    fun setAlign(align:Int){
        printerInterface.setAlign(align)
    }

    fun printText(content: String?, size: Float, isBold: Boolean, isUnderLine: Boolean, typeface: String?){
        printerInterface.printText(content, size, isBold, isUnderLine, typeface)
    }

    fun printBarCode(data: String?, symbology: Int, height: Int, width: Int, textposition: Int){
        printerInterface.printBarCode(data,symbology,height,width,textposition)
    }

    fun printQr(data: String?, modulesize: Int, errorlevel: Int){
        printerInterface.printQr(data,modulesize,errorlevel)
    }

    fun printTable(txts: Array<String?>?, width: IntArray?, align: IntArray?){
        printerInterface.printTable(txts,width,align)
    }

    fun printBitmap(bitmap: Bitmap?, orientation: Int?){
        printerInterface.printBitmap(bitmap,orientation)
    }

    fun getPrinterStatus(context: Context?) :String?{
        return printerInterface.getPrinterStatus(context)
    }
}