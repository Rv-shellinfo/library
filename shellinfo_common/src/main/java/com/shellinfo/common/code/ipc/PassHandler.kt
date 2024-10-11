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
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
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
        passList.removeIf { pass -> pass.priority?.toInt() !=0 && (isExpired(pass.endDateTime!!) || pass.passLimit!!.toInt() <= 0) }

        // If less than 3 passes are present after removal, add default passes back
        while (passList.size < 3) {
            passList.add(PassBin())
        }

        // Check if new pass can be added based on the number of valid passes
        if (passList.size == 3 && passList.none { it.priority!!.toInt() == 0 }) {
            // If there are already 3 active passes, and no blank passes left
            throw IllegalStateException("All valid passes are already present.")
        }


        // Add the new pass, sort by priority, and handle same-priority scenario
        passList.add(pass)
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
            passInfo = databaseCall.getPassInfo(PassType.getPassCodeHex(passRequest.passType.passCode))
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
        passBin.passLimit = passInfo.dailyLimit.toByte()
        passBin.dailyLimit = 10.toByte()
        Utils.numToBin(passBin.startDateTime,trxTimeFromCardEffDate,2)

        //TODO expiry date needs to be set dynamic from the API
        Utils.numToBin(passBin.endDateTime,getExpiryDate(trxTimeFromCardEffDate,30),2)
        passBin.priority = passInfo.passPriority.toByte()
        passBin.lastConsumedDate = DateUtils.getCurrentDateAsCustomBytes()

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

        val expiryDateTime= binToNum(expiryDate,2)

        return LocalDateTime.now().isAfter(longToLocalDateTime(expiryDateTime).truncatedTo(
            ChronoUnit.HOURS).withHour(12))
    }

    fun longToLocalDateTime(epochMillis: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault())
    }


}