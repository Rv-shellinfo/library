package com.shellinfo.common.code.ipc

import com.shellinfo.common.code.DatabaseCall
import com.shellinfo.common.code.ShellInfoLibrary
import com.shellinfo.common.code.enums.PassType
import com.shellinfo.common.data.local.data.emv_rupay.OSAMasterData
import com.shellinfo.common.data.local.data.emv_rupay.binary.osa_bin.PassBin
import com.shellinfo.common.data.local.db.entity.PassTable
import com.shellinfo.common.utils.DateUtils
import com.shellinfo.common.utils.Utils
import com.shellinfo.common.utils.Utils.binToNum
import com.shellinfo.common.utils.ipc.RupayUtils
import kotlinx.coroutines.runBlocking
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PassHandler @Inject constructor(
    private val rupayUtils: RupayUtils,
    private val databaseCall: DatabaseCall
){

    //card effective date
    private var cardEffectiveDate:String? =null

    fun createPass(osaMasterData: OSAMasterData):OSAMasterData{

        val now = LocalDateTime.now()

        //already present pass list
        val passList = osaMasterData.osaBinData!!.passes.toMutableList()

        //get new pass
        val pass= getPassData(osaMasterData)


        // Remove expired or exhausted passes
        passList.removeIf { passData -> (isExpired(passData.endDateTime) || passData.passLimit!!.toInt() <= 0) }

        // If less than 3 passes are present after removal, add default passes back
        while (passList.size < 3) {
            passList.add(PassBin())
        }

        // Check if new pass can be added based on the number of valid passes
        if (passList.size == 3 && passList.none { it.priority!!.toInt() == 0 }) {
            // If there are already 3 active passes, and no blank passes left
            throw IllegalStateException("All valid passes are already present.")
        }


        // Find the existing pass with the same id
        val existingPass = passList.find { it.productType!!.toInt() == pass.productType!!.toInt() }

        //if already pass present then change expiry date of existing pass and increase the trips
        if(existingPass!=null){

            //update expiry date of existing pass
            existingPass.endDateTime = pass.endDateTime

            //increase pass limit old+new
            val newPassLimit = existingPass.passLimit!!.toInt()+pass.passLimit!!.toInt()

            //update pass limit
            existingPass.passLimit = newPassLimit.toByte()

        }else{

            // Add the new pass, sort by priority, and handle same-priority scenario
            passList.add(pass)
        }



        passList.sortWith(compareBy { if (it.priority?.toInt() == 0) Int.MAX_VALUE else it.priority?.toInt() })

        // Ensure that we have exactly 3 passes
        if (passList.size > 3) {
            passList.removeLast()
        }

        osaMasterData.osaUpdatedBinData!!.passes=passList

        return osaMasterData
    }

    /**
     * Method to create pass data which needs to be write on the OSA area
     */
    private fun getPassData(osaMasterData: OSAMasterData):PassBin{

        //pass info
        var passInfo:PassTable

        //get the pass request
        val passRequest= ShellInfoLibrary.passCreateRequest

        //get pass information from database
        runBlocking {
            passInfo = databaseCall.getPassById(passRequest.productType)
        }

        //calculate card effective date
        cardEffectiveDate= rupayUtils.readCardEffectiveDate(rupayUtils.hexStringToByteArray(osaMasterData.bf200Data!!.b.`5F25`!!))


        //update transaction date time
        val trxTimeFromEpoch: Long = System.currentTimeMillis() / 1000
        val trxTimeFromCardEffDate = rupayUtils.calculateTrxTimeFromCardEffectiveDate(
            cardEffectiveDate!!,
            trxTimeFromEpoch
        )

        //pass data set
        val passBin = PassBin()
        passBin.productType = passInfo.passCode.removePrefix("0x").toInt(16).toByte()
        passBin.passLimit = passInfo.dailyLimitDefault.toByte()
        passBin.dailyLimit = 10.toByte()
        Utils.numToBin(passBin.startDateTime,trxTimeFromCardEffDate,3)

        //TODO expiry date needs to be set dynamic from the API
        passBin.endDateTime= DateUtils.saveFutureDateInTwoBytes(30)
        passBin.lastConsumedDate=DateUtils.saveFutureDateInTwoBytes(0)
        passBin.priority = passInfo.passPriority.toByte()

        return passBin
    }

    /**
     * Method to get expiry date with n+ number of days and with card effective date
     */
    private fun getExpiryDate(currentTime:Long,daysToAdd: Int):Long{

        // Convert trxTimeFromEpoch to LocalDateTime
        val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(currentTime), ZoneId.systemDefault())

        // Add the specified number of days and set time to 12 PM
        val adjustedDateTime = localDateTime.plusDays(daysToAdd.toLong()).withHour(12).withMinute(0).withSecond(0).withNano(0)

        // Convert back to epoch seconds
        return adjustedDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
    }

    // Checks if the pass is expired (assuming expiryDate is until 12PM)
    private fun isExpired(expiryDate:ByteArray): Boolean {

        val expiryDatePass = DateUtils.getDateFromByteArrayPass(expiryDate)  //format (dd-mm-yyyy)


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

    fun longToLocalDateTime(epochMillis: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault())
    }

    fun deletePasses(osaMasterData: OSAMasterData):OSAMasterData{

        val osaBin = osaMasterData.osaUpdatedBinData

        for(pass in osaBin!!.passes){
            pass.productType = 0x0.toByte()
            pass.passLimit = 0x0.toByte()
            pass.startDateTime = byteArrayOf(0x0.toByte(),0x0.toByte(),0x0.toByte())
            pass.endDateTime = byteArrayOf(0x0.toByte(),0x0.toByte())
            pass.validZoneId = 0x0.toByte()
            pass.validEntryStationId = 0x0.toByte()
            pass.validExitStationId = 0x0.toByte()
            pass.tripCount = 0x0.toByte()
            pass.lastConsumedDate = byteArrayOf(0x0.toByte(),0x0.toByte())
            pass.dailyLimit = 0x0.toByte()
            pass.priority = 0x0.toByte()

        }

        osaMasterData.osaUpdatedBinData = osaBin

        return osaMasterData
    }

}