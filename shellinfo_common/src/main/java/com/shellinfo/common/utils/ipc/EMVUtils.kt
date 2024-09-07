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


    fun calculateSecondsOfTxn2(
        cardactiveDate: String?,
        millis: Long,
        sizeOfIntInHalfBytes: Int
    ): String? {
        var date: String? = ""
        try {
            val epoch =
                SimpleDateFormat("yyMMddHHmmss", Locale.UK).parse(cardactiveDate).time / 1000
            val millistoseconds = millis / 1000
            var finalepoch = millistoseconds - epoch
            finalepoch = finalepoch / 60 // finalepoch is in minutes
            date = decToHex(finalepoch.toInt(), sizeOfIntInHalfBytes)
//            /*
//            Log.e("epoch", "" + epoch);
//            Log.e("mintoseconds", "" + millistoseconds);
//            Log.e("final epoch", "" + finalepoch);*/EMVUtils.Logger(
//                "final date conversion" + calculateSecondsOfTxn(
//                    cardactiveDate,
//                    finalepoch
//                )
//            )
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
}