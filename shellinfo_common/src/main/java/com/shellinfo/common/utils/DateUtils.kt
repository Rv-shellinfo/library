package com.shellinfo.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


object DateUtils {

    private val calendar = Calendar.getInstance()

    fun getTimerText(): String {
        return "%02d-%02d-%02d %02d:%02d:%02d".format(
            calendar.get(Calendar.DATE),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND)
        )
    }


    @SuppressLint("SimpleDateFormat")
    fun getDate(format:String):String{
        val sdf = SimpleDateFormat(format)
        val date: Date = Date()
        return sdf.format(date.time)
    }


    /**
     * method to check server time and current time to make sure
     * ticket is purchasing for the available timings
     */
    fun checkOperationalTimings(startTime: String, endTime: String,context: Context): Boolean{

        try{

            val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta"))
            val tz: TimeZone = cal.timeZone
            val df = SimpleDateFormat("ddMMyyyyHHmm", Locale.getDefault())
            df.timeZone = tz

            val date = df.format(Calendar.getInstance().time)
            val currentTime: Date = df.parse(date)!!
            val pickedStartTime: Date = df.parse(startTime)!!
            val pickedEndTime: Date = df.parse(endTime)!!

            val dateformatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            val starttimeAm = dateformatter.format(pickedStartTime)
            val endtimePm = dateformatter.format(pickedEndTime)

            val dateFormat = SimpleDateFormat("ddMMyyyyHHmm", Locale.getDefault())
            val cal1 = Calendar.getInstance()
            cal1.time = currentTime
            cal1.add(Calendar.DATE, 1)
            val convertedDate = dateFormat.format(cal1.time)

            if (currentTime.before(pickedStartTime)) {
                Toast.makeText(context, "Book a Ticket after $starttimeAm AM", Toast.LENGTH_LONG).show()
                return false
            } else if (currentTime.after(pickedStartTime)) {
                Toast.makeText(context, "Book a Ticket before $endtimePm PM", Toast.LENGTH_LONG).show()
                return false
            } else if (convertedDate > df.format(pickedStartTime)) {
                Toast.makeText(context, "Book a Ticket add before $endtimePm PM", Toast.LENGTH_LONG).show()
                return false
            } else {
                return true
            }

        }catch (ex:Exception){
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
            return false
        }
    }


    /**
     * method to get date and time in specific format
     */
    fun getDateInSpecificFormat(dateFormat: String): String? {
        val dateFormat = SimpleDateFormat(dateFormat)
        return dateFormat.format(Calendar.getInstance().time)
    }


    /**
     * method to get the data in format specific
     */
    fun getTimeInYMDHMS(timeInSeconds: Long): String {
        // Convert the seconds to a LocalDateTime object
        val dateTime = LocalDateTime.ofEpochSecond(timeInSeconds, 0, ZoneOffset.UTC)

        // Format the date and time
        val formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss", Locale.getDefault())

        // Return the formatted string
        return dateTime.format(formatter)
    }

    /**
     * method to get current date as byteArray
     */
    fun getCurrentDateAsCustomBytes(): ByteArray {
        // Format the current date as yyMMdd
        val dateFormat = SimpleDateFormat("yyMMdd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        // Convert the formatted date string to bytes (each char is 1 byte)
        return currentDate.toByteArray(Charsets.UTF_8)
    }

    /**
     * method to get date from byte array
     */
    fun getDateFromByteArray(byteArray: ByteArray): String {
        // Convert ByteArray back to String (using UTF-8 encoding)
        return byteArray.toString(Charsets.UTF_8)
    }

    /**
     * method to get string date in Date(yyMMdd)
     */
    fun getDateFromString(date: String): Date {
        // Define the expected date format as yyMMdd
        val dateFormat = SimpleDateFormat("yyMMdd", Locale.getDefault())

        // Parse the string into a Date object
        return dateFormat.parse(date) ?: throw IllegalArgumentException("Invalid date format")
    }


    // Method to add n days to the current date and return in 4 bytes
    // Method to add 'n' number of days to the current date and return the new date in 2 bytes
    fun saveFutureDateInTwoBytes(n: Int): ByteArray {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, n) // Add 'n' days

        val year = calendar.get(Calendar.YEAR) - 2000
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Pack the date: year (7 bits), month (4 bits), and day (5 bits)
        val packedValue = (year shl 9) or (month shl 5) or day

        // Convert to byte array
        return byteArrayOf(
            ((packedValue shr 8) and 0xFF).toByte(),  // High byte
            (packedValue and 0xFF).toByte()           // Low byte
        )

    }

    fun getFutureDate(n:Int):String{

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, n) // Add 'n' days

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun getDateFromByteArrayPass(dateBytes: ByteArray): String {
        if (dateBytes.size != 2) {
            throw IllegalArgumentException("Invalid byte array size. Must be exactly 2 bytes.")
        }

        // Combine the two bytes into one integer
        val packedValue = (dateBytes[0].toInt() shl 8) or (dateBytes[1].toInt() and 0xFF)

        // Extract day (5 bits), month (4 bits), and year (7 bits)
        val day = packedValue and 0b11111
        val month = (packedValue shr 5) and 0b1111
        val year = (packedValue shr 9) and 0b1111111

        // Format the result into DD-MM-YY
        return String.format("%02d-%02d-%02d",day,month, year % 100)
    }

}