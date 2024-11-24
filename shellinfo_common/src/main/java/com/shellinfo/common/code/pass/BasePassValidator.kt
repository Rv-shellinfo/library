package com.shellinfo.common.code.pass

import android.util.Log
import com.shellinfo.common.data.local.data.emv_rupay.binary.osa_bin.PassBin
import com.shellinfo.common.utils.DateUtils
import com.shellinfo.common.utils.Utils
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class BasePassValidator @Inject constructor(){

    lateinit var passBin: PassBin

    var validPassIndex= -1

    /**
     *
     */
    open fun setPass(passBin: PassBin){
        this.passBin=passBin
    }


    // Checks if the pass is expired (assuming expiryDate is until 12PM)
    fun isExpired(expiryDate:ByteArray): Boolean {


        val expiryDatePass = DateUtils.getDateFromByteArrayPass(expiryDate)  //format (dd-mm-yyyy)

        Log.e("Expiry Date", expiryDatePass)

        try {
            // Define the date format
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yy")

            // Parse the input date
            val inputDate = LocalDate.parse(expiryDatePass, formatter)

            // Get the current date
            val currentDate = LocalDate.now()

            // Check if the input date is before the current date
            return inputDate.isBefore(currentDate)

        } catch (e: DateTimeParseException) {
            // Handle invalid date formats
            e.printStackTrace()
            return false
        }
    }

    /**
     * Method to check if all pass expired
     */
    open fun isAllPassExpired(passList: List<PassBin>):Boolean{

        for(pass in passList){
            if (!isExpired(pass.endDateTime)) return false // If any pass is not expired, return false
        }

        return true
    }


    /**
     * Method to validate trips
     */
    open fun validateDailyLimit(passBin:PassBin){

        //last consumed date of pass
        val lastConsumedPassDate =passBin.lastConsumedDate

        val lastConsumedDate = DateUtils.getDateFromByteArrayPass(lastConsumedPassDate)  //format (dd-mm-yyyy)

            // Define the date format
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yy")

            // Parse the input date
            val inputDate = LocalDate.parse(lastConsumedDate, formatter)

            // Get the current date
            val currentDate = LocalDate.now()

            // Check if the input date is before the current date
           if(currentDate.isAfter(inputDate)){
               passBin.tripCount= 0 // Reset for the new day
               passBin.lastConsumedDate = DateUtils.saveFutureDateInTwoBytes(0)
           }

    }

    /**
     * Method to validate entry station for zone passes
     */
    open fun entryValidation(zoneId:Int,entryStationId:Int):Boolean{

        //check on entry if entry station valid for zone pass
        return zoneId == passBin.validZoneId!!.toInt() && entryStationId == passBin.validEntryStationId!!.toInt()
    }

    /**
     * Method to validate exit station for zone passes
     */
    open fun exitValidation(zoneId:Int,entryStationId:Int,exitStationId:Int):Boolean{

        //check on exit if entry station and exit station valid for zone pass
        return zoneId == passBin.validZoneId!!.toInt() && entryStationId == passBin.validEntryStationId!!.toInt() && exitStationId == passBin.validExitStationId!!.toInt()
    }

    fun updatePassData(){

        // If validation passes, update the trip counts
        passBin.tripCount = (passBin.tripCount!!.toInt()+1).toByte()
        passBin.passLimit = (passBin.passLimit!!.toInt()-1).toByte()
    }


}