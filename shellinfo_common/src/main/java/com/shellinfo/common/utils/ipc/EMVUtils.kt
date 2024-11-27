package com.shellinfo.common.utils.ipc

import android.util.Log
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EMVUtils @Inject constructor() {

    var df = DecimalFormat("0.00")

    private val numberOfBitsInAHalfByte = 4
    private val halfByte = 0x0F
    private val hexDigits =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

    fun getHexatoDecimal(hex: String): Long {
        return hex.toInt(16).toLong()
    }

    fun calculateSecondsOfTxn(cardactiveDate: String?, txnminutes: Long): String {
        var date = ""
        try {
            val date1 = SimpleDateFormat("yyMMddHHmmss", Locale.UK)
            date1.set2DigitYearStart(date1.parse("01012000000000"))
            val parsedate = date1.parse(cardactiveDate)
            val epoch = parsedate.time / 1000
            val mintoseconds = txnminutes * 60
            val finalepoch = epoch + mintoseconds
            Log.e("epoch", "" + epoch)
            Log.e("mintoseconds", "" + mintoseconds)
            Log.e("final epoch", "" + finalepoch)
            date =
                SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.UK).format(Date(finalepoch * 1000))
            Log.e("date", "" + date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }





    fun decToHex(dec: Int, sizeOfIntInHalfBytes: Int): String? {
        var dec = dec
        val hexBuilder = StringBuilder(sizeOfIntInHalfBytes)
        hexBuilder.setLength(sizeOfIntInHalfBytes)
        for (i in sizeOfIntInHalfBytes - 1 downTo 0) {
            val j = dec and halfByte
            hexBuilder.setCharAt(i, hexDigits.get(j))
            dec = dec shr numberOfBitsInAHalfByte
        }
        return hexBuilder.toString()
    }

    fun replacedString(originalStr: String?, start: Int, end: Int, replaceStr: String?): String? {
        val buf = StringBuffer(originalStr)
        buf.replace(start, end, replaceStr)
        return buf.toString()
    }

    fun getStationIdFromStationDetailList(stationId:String):String{
        return "name"
    }

    fun hexStringToByteArray(hexString: String): ByteArray {
        val length = hexString.length
        val byteArray = ByteArray(length / 2)

        for (i in 0 until length step 2) {
            val hex = hexString.substring(i, i + 2)
            byteArray[i / 2] = hex.toInt(16).toByte()
        }

        return byteArray
    }

    fun combineToByte(value1: Int, value2: Int): Byte {
        // Ensure both values fit within 4 bits
        require(value1 in 0..15) { "value1 must be a 4-bit value (0-15)" }
        require(value2 in 0..15) { "value2 must be a 4-bit value (0-15)" }

        // Shift value1 to the upper 4 bits and combine with value2
        return ((value1 shl 4) or value2).toByte()
    }

}