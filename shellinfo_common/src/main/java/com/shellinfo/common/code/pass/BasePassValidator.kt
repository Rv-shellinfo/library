package com.shellinfo.common.code.pass

import com.shellinfo.common.data.local.data.emv_rupay.binary.osa_bin.PassBin
import com.shellinfo.common.utils.DateUtils
import com.shellinfo.common.utils.Utils
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class BasePassValidator @Inject constructor(){

    lateinit var passBin: PassBin

    /**
     *
     */
    open fun setPass(passBin: PassBin){
        this.passBin=passBin
    }

    /**
     * Method to validate Expiry Date
     */
    open fun validateExpiry():Boolean{

        //get expiry date
        val endDateTimeEpoch = Utils.binToNum(passBin.endDateTime,2)

        //current date time from epoch
        val currentTimeFromEpoch: Long = System.currentTimeMillis() / 1000

        // Check if the current time is greater than or equal to endDateTime (meaning the pass is expired)
        return currentTimeFromEpoch >= endDateTimeEpoch
    }

    /**
     * Method to validate trips
     */
    open fun validateTrips():Boolean{

        //last date when pass consumed
        val lastDate= DateUtils.getDateFromString(DateUtils.getDateFromByteArray(passBin.lastConsumedDate))

        //today's date
        val todayDate= DateUtils.getDateFromString(DateUtils.getDateFromByteArray(DateUtils.getCurrentDateAsCustomBytes()))


        // Reset today's consumed trips if the date has changed
        if(todayDate.after(lastDate)){
            passBin.tripCount= 0 // Reset for the new day
            passBin.lastConsumedDate = DateUtils.getCurrentDateAsCustomBytes() // Update the last consumed date
        }

        // Check if today's limit is exceeded
        if(passBin.tripCount!!.toInt() >= passBin.dailyLimit!!.toInt() ){
            return false
        }

        // Check if pass limit finished
        if(passBin.passLimit!!.toInt() <= 0){
            return false
        }



        return true
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