package com.shellinfo.common.utils.ipc

import abbasi.android.filelogger.FileLogger
import android.util.Log
import com.shellinfo.common.code.DatabaseCall
import com.shellinfo.common.code.ShellInfoLibrary
import com.shellinfo.common.data.local.data.emv_rupay.CSAMasterData
import com.shellinfo.common.data.local.data.emv_rupay.HistoryQueue
import com.shellinfo.common.data.local.data.emv_rupay.OSAMasterData
import com.shellinfo.common.data.local.data.emv_rupay.binary.csa_bin.CsaBin
import com.shellinfo.common.data.local.data.emv_rupay.binary.csa_bin.GeneralBin
import com.shellinfo.common.data.local.data.emv_rupay.binary.csa_bin.HistoryBin
import com.shellinfo.common.data.local.data.emv_rupay.binary.csa_bin.ValidationBin
import com.shellinfo.common.data.local.data.emv_rupay.binary.osa_bin.GeneralBinOsa
import com.shellinfo.common.data.local.data.emv_rupay.binary.osa_bin.HistoryBinOsa
import com.shellinfo.common.data.local.data.emv_rupay.binary.osa_bin.OsaBin
import com.shellinfo.common.data.local.data.emv_rupay.binary.osa_bin.PassBin
import com.shellinfo.common.data.local.data.emv_rupay.binary.osa_bin.ValidationBinOsa
import com.shellinfo.common.data.local.data.emv_rupay.display.csa_display.CSADataDisplay
import com.shellinfo.common.data.local.data.emv_rupay.display.csa_display.TxnHistory
import com.shellinfo.common.data.local.data.emv_rupay.display.osa_display.OSADataDisplay
import com.shellinfo.common.data.local.data.emv_rupay.display.osa_display.PassData
import com.shellinfo.common.data.local.data.emv_rupay.display.osa_display.TxnHistoryOsa
import com.shellinfo.common.data.local.data.emv_rupay.raw.CSADataRaw
import com.shellinfo.common.data.local.data.emv_rupay.raw.GeneralData
import com.shellinfo.common.data.local.data.emv_rupay.raw.HistoryData
import com.shellinfo.common.data.local.data.emv_rupay.raw.ServiceData
import com.shellinfo.common.data.local.data.emv_rupay.raw.TerminalData
import com.shellinfo.common.data.local.data.emv_rupay.raw.ValidationData
import com.shellinfo.common.data.local.data.ipc.BF200Data
import com.shellinfo.common.data.local.db.entity.PassTable
import com.shellinfo.common.data.local.db.entity.StationsTable
import com.shellinfo.common.data.local.db.entity.ZoneTable
import com.shellinfo.common.utils.DateUtils
import com.shellinfo.common.utils.IPCConstants
import com.shellinfo.common.utils.IPCConstants.TXN_STATUS_PENALTY
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.nio.BufferOverflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.Inject


class RupayUtils @Inject constructor(
    private val emvUtils: EMVUtils,
    private val databaseCall: DatabaseCall
){

    //============= CSA DATA====================//

    //csa master data in which raw and display data combined
    private lateinit var csaMasterData: CSAMasterData

    //csa raw data i.e.hex values based on the index
    private lateinit var csaRawData: CSADataRaw

    //============= OSA DATA ==================//

    //osa master data
    private lateinit var osaMasterData: OSAMasterData

    //osa global master data
    private lateinit var osaGlobalData:OSAMasterData

    /**
     * Method to set osa global data
     */
    fun setOsaGlobalData(osaMasterData: OSAMasterData){
        osaGlobalData=osaMasterData
    }


    /**
     * Method to read tlv data and set the raw and display OSA data classes
     */
    fun readOSAData(bF200Data: BF200Data):OSAMasterData{

        val df33_data= bF200Data.b.DF33!!
        val data_5F25= bF200Data.b.`5F25`!!

        //init master osa data
        osaMasterData= OSAMasterData()

        try{

            //osa bin data
            val osaBin = setOsaBinValue(bF200Data.serviceRelatedData!!.toByteArray().sliceArray(bF200Data.serviceDataIndex!!..bF200Data.serviceDataIndex!!+95))

        //============================VALIDATION==========================================//

            //error string any
            val error_code: String = getSubString(df33_data, 68, 70)
            val errorFormat: String = getError(error_code)

            //product (pass name)
            var productInfo:PassTable?=null
            val productValue: Int = osaBin.validationData.productType.toInt()
            var productName:String = "--"

            runBlocking {
                productInfo = databaseCall.getPassById(productValue)
            }

            if(productInfo!=null){
                productName= productInfo!!.passName
            }



            //last transaction date time
            val date: String = getSubString(df33_data, 72, 78)
            val finaltxndate = if (emvUtils.getHexatoDecimal(date).toInt() == 0) {
                "--"
            } else {
                emvUtils.calculateSecondsOfTxn(
                    data_5F25 + "000000",
                    emvUtils.getHexatoDecimal(date)
                )
            }

            //last station name
            val lastStationId:Int = osaBin.validationData.stationCode[0].toInt()
            var lastStationName :String ? ="--"
            var lastStationStringId:String?=""
            var lastStationInfo:StationsTable?=null
            runBlocking {
                lastStationInfo=databaseCall.getStationByStationId(lastStationId)
            }

            if(lastStationInfo!=null){
                lastStationName = lastStationInfo!!.stationName
                lastStationStringId= lastStationInfo!!.stationId
            }


            //last status
            //status
            val status: String = getSubString(df33_data, 82, 84)
            val statusValue: Int = getTxnStatusNCMC(status)
            val statusFormat: String = getTxnStatus(status)

       //============================ HISTORY 1 ==========================================//
            var stationsTable:StationsTable?=null

            //last equipment id, operator id and station id
            val terminalId= osaBin.history.get(0).terminalID
            val terminalInfo1: String = byteArrayToHex(terminalId!!)
            var h1_lastStationName= ""
            var h1_lastStationStringId:String=""
            var equipmentGroupId1:String=""


            if(!terminalInfo1.all { it == '0' }){
                val parsedValues1 = parseTerminalHexValue(terminalInfo1)

                val lineId1 = parsedValues1["lineId"]
                val stationNumber1 = parsedValues1["stationNumber"]
                equipmentGroupId1 =""+ parsedValues1["equipmentGroupId"]

                //last station name
                val h1_lastStationId =""+lineId1+stationNumber1

                runBlocking {
                    stationsTable = databaseCall.getStationByStationId(h1_lastStationId)
                }

                if(stationsTable!=null){
                    h1_lastStationName = stationsTable!!.stationName.toString()
                    h1_lastStationStringId = stationsTable!!.stationId
                }
            }


            //date time
            val dateTimeHex = getSubString(df33_data, 96, 102)
            val dateTime1 = if (emvUtils.getHexatoDecimal(dateTimeHex).toInt() == 0) {
                "-- --"
            } else {
                emvUtils.calculateSecondsOfTxn(data_5F25 + "000000", emvUtils.getHexatoDecimal(dateTimeHex))
            }

            //trx sequence number
            val txnSeqNo1 = bin2num(osaBin.history.get(0).trxSeqNum!!,2).toString()


            //pass limit
            val passLimit = bin2num(osaBin.history.get(0).passLimit!!,2).toString()

            //trip counts today
            val tripCount = osaBin.history.get(0).tripCount!!.toInt().toString()

            //daily limit
            val dailyLimit = osaBin.history.get(0).dailyLimit!!.toInt().toString()

            //trx status
            val trxType1 = getTxnStatusFromHex(osaBin.history.get(0).trxStatus!!.toInt())


            //product 1
            val historyProduct1: Int = osaBin.history.get(0).productType!!.toInt()
            var historyProductName1= ""
            productInfo=null
            runBlocking {
                productInfo = databaseCall.getPassById(historyProduct1)
            }

            if(productInfo!=null){
                historyProductName1= productInfo!!.passName
            }



        //============================ HISTORY 2 ==========================================//

            //last equipment id, operator id and station id
            val terminalId2= osaBin.history.get(1).terminalID
            val terminalInfo2: String = byteArrayToHex(terminalId2!!)
            var h2_lastStationName= ""
            var h2_lastStationStringId:String=""
            var equipmentGroupId2:String=""


            if(!terminalInfo2.all { it == '0' }){
                val parsedValues1 = parseTerminalHexValue(terminalInfo2)

                val lineId2 = parsedValues1["lineId"]
                val stationNumber2 = parsedValues1["stationNumber"]
                equipmentGroupId2 =""+ parsedValues1["equipmentGroupId"]

                //last station name
                val h2_lastStationId =""+lineId2+stationNumber2

                runBlocking {
                    stationsTable = databaseCall.getStationByStationId(h2_lastStationId)
                }

                if(stationsTable!=null){
                    h2_lastStationName = stationsTable!!.stationName.toString()
                    h2_lastStationStringId = stationsTable!!.stationId
                }
            }


            //date time
            val dateTimeHex2 = getSubString(df33_data, 132, 138)
            val dateTime2 = if (emvUtils.getHexatoDecimal(dateTimeHex2).toInt() == 0) {
                "-- --"
            } else {
                emvUtils.calculateSecondsOfTxn(data_5F25 + "000000", emvUtils.getHexatoDecimal(dateTimeHex2))
            }

            //trx sequence number
            val txnSeqNo2 = bin2num(osaBin.history.get(1).trxSeqNum!!,2).toString()


            //pass limit
            val passLimit2 = bin2num(osaBin.history.get(1).passLimit!!,2).toString()

            //trip counts today
            val tripCount2 = osaBin.history.get(1).tripCount!!.toInt().toString()

            //daily limit
            val dailyLimit2 = osaBin.history.get(1).dailyLimit!!.toInt().toString()

            //trx status
            val trxType2 = getTxnStatusFromHex(osaBin.history.get(1).trxStatus!!.toInt())


            //product 1
            val historyProduct2: Int = osaBin.history.get(1).productType!!.toInt()
            var historyProductName2= ""
            productInfo=null
            runBlocking {
                productInfo = databaseCall.getPassById(historyProduct2)
            }

            if(productInfo!=null){
                historyProductName2= productInfo!!.passName
            }



        //================================== History List Create ==================================================//
            //adding the transaction history
            val transactions = mutableListOf<TxnHistoryOsa>()
            transactions.add(
                TxnHistoryOsa(
                    terminalId = terminalInfo1,
                    txnSeqNo1,
                    dateTime1.split(" ")[0],
                    dateTime1.split(" ")[1],
                    passLimit,
                    dailyLimit,
                    tripCount,
                    trxType1,
                    historyProductName1,
                    h1_lastStationName,
                    h1_lastStationStringId
                )
            )
            transactions.add(
                TxnHistoryOsa(
                    terminalId = terminalInfo2,
                    txnSeqNo2,
                    dateTime2.split(" ")[0],
                    dateTime2.split(" ")[1],
                    passLimit2,
                    dailyLimit2,
                    tripCount2,
                    trxType2,
                    historyProductName2,
                    h2_lastStationName,
                    h2_lastStationStringId
                )
            )

        //===============================    PASS 1 =================================================================//

            var passInfo:PassTable?
            var zoneInfo:ZoneTable?
            var stationInfo:StationsTable?

            //pass id and name
            val pass1= getSubString(df33_data, 156, 158)

            //get pass information from database
            runBlocking {
                Log.e("PASS ID",hexToByte(pass1)!!.toInt().toString())
                passInfo = databaseCall.getPassById(hexToByte(pass1)!!.toInt())
            }

            Log.e("PASS Name",passInfo?.passName ?:"")
            val pass1Name= passInfo?.passName ?:""

            //pass limit
            val pass1Limit = emvUtils.getHexatoDecimal(getSubString(df33_data, 158, 160)).toString()

            //pass start time
            val pass1StartDateTimeHex = getSubString(df33_data, 160, 166)
            val pass1StartDateTime = if (emvUtils.getHexatoDecimal(pass1StartDateTimeHex).toInt() == 0) {
                "-- --"
            } else {
                emvUtils.calculateSecondsOfTxn(data_5F25 + "000000", emvUtils.getHexatoDecimal(pass1StartDateTimeHex))
            }

            //pass expiry date
            val pass1ExpiryDateHex = getSubString(df33_data, 166, 170)
            val pass1ExpiryDate = if (emvUtils.getHexatoDecimal(pass1ExpiryDateHex).toInt() == 0) {
                "-- --"
            } else {
                DateUtils.getDateFromByteArrayPass(emvUtils.hexStringToByteArray(pass1ExpiryDateHex))
                //emvUtils.calculateSecondsOfTxn(data_5F25 + "000000", emvUtils.getHexatoDecimal(pass1ExpiryDateHex))
            }

            //pass zone
            val pass1ZoneId= emvUtils.getHexatoDecimal(getSubString(df33_data, 170, 172)).toString()
            var pass1ZoneFare:Double=0.0

            if(!pass1ZoneId.equals("0") && !pass1ZoneId.equals("99")) {
                runBlocking {
                    zoneInfo = databaseCall.getZoneById(pass1ZoneId.toInt())
                    pass1ZoneFare = zoneInfo!!.zoneAmount
                }
            }

            //entry station id
            var pass1EntryStationId= emvUtils.getHexatoDecimal(getSubString(df33_data, 172, 174)).toString()
            var pass1EntryStationName=""

            Log.e("pass1EntryStationId",pass1EntryStationId)

            try {
                if(pass1EntryStationId != "0" && pass1EntryStationId != "99" && pass1EntryStationId != "") {
                    runBlocking {
                        stationInfo= databaseCall.getStationByStationId(pass1EntryStationId.toInt())
                        pass1EntryStationName= stationInfo!!.stationName!!
                        pass1EntryStationId= stationInfo!!.stationId!!
                    }
                }
            }catch (ex:Exception){

            }




            //exit station id
            var pass1ExitStationId= emvUtils.getHexatoDecimal(getSubString(df33_data, 174, 176)).toString()
            var pass1ExitStationName=""

            try {

                if(pass1ExitStationId != "0" && pass1ExitStationId != "99" && pass1ExitStationId != "") {
                    runBlocking {
                        stationInfo= databaseCall.getStationByStationId(pass1ExitStationId.toInt())
                        pass1ExitStationName= stationInfo!!.stationName!!
                        pass1ExitStationId= stationInfo!!.stationId!!
                    }
                }
            }catch (ex:Exception){

            }



            //trip counts
            val pass1TripCounts= emvUtils.getHexatoDecimal(getSubString(df33_data, 176, 178)).toString()

            //last consumed date
            val pass1LastConsumedDateHex= getSubString(df33_data, 178, 182)
            val pass1LastConsumedDate = if (emvUtils.getHexatoDecimal(pass1LastConsumedDateHex).toInt() == 0) {
                "-- --"
            } else {
                DateUtils.getDateFromByteArrayPass(emvUtils.hexStringToByteArray(pass1LastConsumedDateHex))
                //emvUtils.calculateSecondsOfTxn(data_5F25 + "000000", emvUtils.getHexatoDecimal(pass1LastConsumedDateHex))
            }

            //daily limit
            val pass1DailyLimit= emvUtils.getHexatoDecimal(getSubString(df33_data, 182, 184)).toString()

            //priority
            val pass1Priority= emvUtils.getHexatoDecimal(getSubString(df33_data, 184, 186))

        //===============================    PASS 2 =================================================================//

            //pass id and name
            val pass2= getSubString(df33_data, 186, 188)
            //get pass information from database
            runBlocking {
                passInfo = databaseCall.getPassById(hexToByte(pass2)!!.toInt())
            }

            val pass2Name= passInfo?.passName ?:""

            //pass limit
            val pass2Limit = emvUtils.getHexatoDecimal(getSubString(df33_data, 188, 190)).toString()

            //pass start time
            val pass2StartDateTimeHex = getSubString(df33_data, 190, 196)
            val pass2StartDateTime = if (emvUtils.getHexatoDecimal(pass2StartDateTimeHex).toInt() == 0) {
                "-- --"
            } else {
                emvUtils.calculateSecondsOfTxn(data_5F25 + "000000", emvUtils.getHexatoDecimal(pass2StartDateTimeHex))
            }

            //pass expiry date
            val pass2ExpiryDateHex = getSubString(df33_data, 196, 200)
            val pass2ExpiryDate = if (emvUtils.getHexatoDecimal(pass2ExpiryDateHex).toInt() == 0) {
                "-- --"
            } else {
                DateUtils.getDateFromByteArrayPass(emvUtils.hexStringToByteArray(pass2ExpiryDateHex))
                //emvUtils.calculateSecondsOfTxn(data_5F25 + "000000", emvUtils.getHexatoDecimal(pass2ExpiryDateHex))
            }

            //pass zone
            val pass2ZoneId= emvUtils.getHexatoDecimal(getSubString(df33_data, 200, 202)).toString()
            var pass2ZoneFare:Double=0.0

            if(!pass2ZoneId.equals("0") && !pass2ZoneId.equals("99")) {
                runBlocking {
                    zoneInfo = databaseCall.getZoneById(pass2ZoneId.toInt())
                    pass2ZoneFare = zoneInfo!!.zoneAmount
                }
            }

            //entry station id
            var pass2EntryStationId= emvUtils.getHexatoDecimal(getSubString(df33_data, 202, 204)).toString()
            var pass2EntryStationName=""

            try {
                if(pass2EntryStationId != "0" && pass2EntryStationId != "99" && pass2EntryStationId != "") {
                    runBlocking {
                        stationInfo= databaseCall.getStationByStationId(pass2EntryStationId.toInt())
                        pass2EntryStationName= stationInfo!!.stationName!!
                        pass2EntryStationId= stationInfo!!.stationId!!
                    }
                }
            }catch (ex:Exception){

            }


            //exit station id
            var pass2ExitStationId= emvUtils.getHexatoDecimal(getSubString(df33_data, 204, 206)).toString()
            var pass2ExitStationName=""

            try {

                if(pass2ExitStationId != "0" && pass2ExitStationId != "99" && pass2ExitStationId != "") {
                    runBlocking {
                        stationInfo= databaseCall.getStationByStationId(pass2ExitStationId.toInt())
                        pass2ExitStationName= stationInfo!!.stationName!!
                        pass2ExitStationId= stationInfo!!.stationId!!
                    }
                }
            }catch (ex:Exception){

            }


            //trip counts
            val pass2TripCounts= emvUtils.getHexatoDecimal(getSubString(df33_data, 206, 208)).toString()

            //last consumed date
            val pass2LastConsumedDateHex= getSubString(df33_data, 208, 212)
            val pass2LastConsumedDate = if (emvUtils.getHexatoDecimal(pass2LastConsumedDateHex).toInt() == 0) {
                "-- --"
            } else {
                DateUtils.getDateFromByteArrayPass(emvUtils.hexStringToByteArray(pass2LastConsumedDateHex))
                //emvUtils.calculateSecondsOfTxn(data_5F25 + "000000", emvUtils.getHexatoDecimal(pass2LastConsumedDateHex))
            }

            //daily limit
            val pass2DailyLimit= emvUtils.getHexatoDecimal(getSubString(df33_data, 212, 214)).toString()

            //priority
            val pass2Priority= emvUtils.getHexatoDecimal(getSubString(df33_data, 214, 216))


        //===============================    PASS 3 =================================================================//

            //pass id and name
            val pass3= getSubString(df33_data, 216, 218)
            //get pass information from database
            runBlocking {
                passInfo = databaseCall.getPassById(hexToByte(pass3)!!.toInt())
            }

            val pass3Name= passInfo?.passName ?:""

            //pass limit
            val pass3Limit = emvUtils.getHexatoDecimal(getSubString(df33_data, 218, 220)).toString()

            //pass start time
            val pass3StartDateTimeHex = getSubString(df33_data, 220, 226)
            val pass3StartDateTime = if (emvUtils.getHexatoDecimal(pass3StartDateTimeHex).toInt() == 0) {
                "-- --"
            } else {
                emvUtils.calculateSecondsOfTxn(data_5F25 + "000000", emvUtils.getHexatoDecimal(pass3StartDateTimeHex))
            }

            //pass expiry date
            val pass3ExpiryDateHex = getSubString(df33_data, 226, 230)
            val pass3ExpiryDate = if (emvUtils.getHexatoDecimal(pass3ExpiryDateHex).toInt() == 0) {
                "-- --"
            } else {
                DateUtils.getDateFromByteArrayPass(emvUtils.hexStringToByteArray(pass3ExpiryDateHex))
                //emvUtils.calculateSecondsOfTxn(data_5F25 + "000000", emvUtils.getHexatoDecimal(pass3ExpiryDateHex))
            }

            //pass zone
            val pass3ZoneId= emvUtils.getHexatoDecimal(getSubString(df33_data, 230, 232)).toString()
            var pass3ZoneFare:Double=0.0

            if(!pass3ZoneId.equals("0") && !pass3ZoneId.equals("99")) {
                runBlocking {
                    zoneInfo = databaseCall.getZoneById(pass3ZoneId.toInt())
                    pass3ZoneFare = zoneInfo!!.zoneAmount
                }
            }

            //entry station id
            var pass3EntryStationId= emvUtils.getHexatoDecimal(getSubString(df33_data, 232, 234)).toString()
            var pass3EntryStationName=""

            try {

                if(pass3EntryStationId != "0" && pass3EntryStationId != "99" && pass3EntryStationId != "") {
                    runBlocking {
                        stationInfo= databaseCall.getStationByStationId(pass3EntryStationId.toInt())
                        pass3EntryStationName= stationInfo!!.stationName!!
                        pass3EntryStationId= stationInfo!!.stationId!!
                    }
                }
            }catch (ex:Exception){

            }



            //exit station id
            var pass3ExitStationId= emvUtils.getHexatoDecimal(getSubString(df33_data, 234, 236)).toString()
            var pass3ExitStationName=""

            try {


                if(pass3ExitStationId != "0" && pass3ExitStationId != "99" && pass3ExitStationId != "") {
                    runBlocking {
                        stationInfo= databaseCall.getStationByStationId(pass3ExitStationId.toInt())
                        pass3ExitStationName= stationInfo!!.stationName!!
                        pass3ExitStationId= stationInfo!!.stationId!!

                    }
                }
            }catch (ex:Exception){

            }


            //trip counts
            val pass3TripCounts= emvUtils.getHexatoDecimal(getSubString(df33_data, 236, 238)).toString()

            //last consumed date
            val pass3LastConsumedDateHex= getSubString(df33_data, 238, 242)
            val pass3LastConsumedDate = if (emvUtils.getHexatoDecimal(pass3LastConsumedDateHex).toInt() == 0) {
                "-- --"
            } else {
                DateUtils.getDateFromByteArrayPass(emvUtils.hexStringToByteArray(pass3LastConsumedDateHex))
                //emvUtils.calculateSecondsOfTxn(data_5F25 + "000000", emvUtils.getHexatoDecimal(pass3LastConsumedDateHex))
            }

            //daily limit
            val pass3DailyLimit= emvUtils.getHexatoDecimal(getSubString(df33_data, 242, 244)).toString()

            //priority
            val pass3Priority= emvUtils.getHexatoDecimal(getSubString(df33_data, 244, 246)).toString()


        //=================================== PASS List Create ==================================================================//


            //adding the transaction history
            val passes = mutableListOf<PassData>()

            //add passes to list
            passes.add(
                PassData(
                    passType = pass1Name,
                    passLimit = pass1Limit,
                    startDateTime = pass1StartDateTime,
                    endDate = pass1ExpiryDate,
                    validZoneId = pass1ZoneId,
                    validZoneFare = pass1ZoneFare,
                    validEntryStationId = pass1EntryStationId,
                    validEntryStationName = pass1EntryStationName,
                    validExitStationName = pass1ExitStationName,
                    validExitStationId = pass1ExitStationId,
                    tripConsumed = pass1TripCounts,
                    classType = "Normal",
                    lastConsumedDate = pass1LastConsumedDate,
                    dailyLimit = pass1DailyLimit,
                    priority = pass1Priority.toInt(),
                ))

            passes.add(
                PassData(
                    passType = pass2Name,
                    passLimit = pass2Limit,
                    startDateTime = pass2StartDateTime,
                    endDate = pass2ExpiryDate,
                    validZoneId = pass2ZoneId,
                    validZoneFare = pass2ZoneFare,
                    validEntryStationId = pass2EntryStationId,
                    validEntryStationName = pass2EntryStationName,
                    validExitStationId = pass2ExitStationId,
                    validExitStationName = pass2ExitStationName,
                    tripConsumed = pass2TripCounts,
                    classType = "Normal",
                    lastConsumedDate = pass2LastConsumedDate,
                    dailyLimit = pass2DailyLimit,
                    priority = pass2Priority.toInt()
                ))

            passes.add(
                PassData(
                    passType = pass3Name,
                    passLimit = pass3Limit,
                    startDateTime = pass3StartDateTime,
                    endDate = pass3ExpiryDate,
                    validZoneId = pass3ZoneId,
                    validZoneFare = pass3ZoneFare,
                    validEntryStationId = pass3EntryStationId,
                    validEntryStationName = pass3EntryStationName,
                    validExitStationId = pass3ExitStationId,
                    validExitStationName = pass3ExitStationName,
                    tripConsumed = pass3TripCounts,
                    classType = "Normal",
                    lastConsumedDate = pass3LastConsumedDate,
                    dailyLimit = pass3DailyLimit,
                    priority = pass3Priority.toInt()
                ))


        //==================================  END =========================================================================================//


            //card effective date
            val cardEffectiveDate = data_5F25


            //Osa display data
            val osaDataDisplay = OSADataDisplay(
                error= errorFormat,
                errorCode= error_code.toInt(),
                lastTxnDateTime = finaltxndate,
                lastTxnStatus =statusFormat,
                lastStationName=  lastStationName!!,
                product=productName,
                txnStatus= statusValue,
                cardEffectiveDate= cardEffectiveDate,
                cardHistory = transactions,
                cardPassesList = passes,
                lastStationId = lastStationStringId!!
            )




            //now set all values to OSA Master
            osaMasterData.bf200Data= bF200Data
            osaMasterData.osaDisplayData= osaDataDisplay
            osaMasterData.osaBinData=osaBin
            osaMasterData.osaUpdatedBinData=osaBin



        }catch (ex:Exception){

            FileLogger.e("Error",">>> Error in parsing OSA data")
            ex.printStackTrace()
        }


        return osaMasterData
    }



    /**
     * Method to read tlv data and set the raw and display CSA data classes
     */
    fun readCSAData(bF200Data: BF200Data): CSAMasterData {

        val df33_data= bF200Data.b.DF33!!
        val data_5F25= bF200Data.b.`5F25`!!

        //init master csa data
        csaMasterData= CSAMasterData()

        //check
        if(ShellInfoLibrary.isOsaTrxAbort || ShellInfoLibrary.isOsaTrxAbortWithPenalty){

            //previous osa df33 data
            val osa_df33 = osaGlobalData.bf200Data!!.b.DF33


            //error string any
            val error_code: String = getSubString(osa_df33!!, 68, 70)
            setSubstring(df33_data,68,error_code)

            //trx date time
            val date: String = getSubString(osa_df33, 72, 78)
            setSubstring(df33_data,84,date)

            //last station name
            val lastStationHex: String = getSubString(osa_df33, 78, 82)
            setSubstring(df33_data,84,lastStationHex)

            //last status
            val status: String = getSubString(osa_df33, 82, 84)
            setSubstring(df33_data,104,status)

            //last history
            val lastHistory : String = getSubString(osa_df33,120,154)
            setSubstring(df33_data,220,lastHistory)

            if(ShellInfoLibrary.isOsaTrxAbortWithPenalty){
                setSubstring(df33_data,68,TXN_STATUS_PENALTY.toString())
            }
        }

        try {

            //global wallet balance
            val cardBalance_str: String = getSubString(df33_data, 46, 58)
            val cardbalance = cardBalance_str.toDouble() / 100
            val cardBalanceFormat = emvUtils.df.format(cardbalance)

//            //penalty amount
//            val penalty_str: String = getSubString(df33_data, 90, 94)
//            val penalty_amnt = penalty_str.toDouble() / 100
//            val penaltyAmtFormat = emvUtils.df.format(penalty_amnt)

            //error string any
            val error_code: String = getSubString(df33_data, 68, 70)
            val errorFormat: String = getError(error_code)

            //status
            val status: String = getSubString(df33_data, 104, 106)
            val statusValue: Int = status.toInt()
            val statusFormat: String = getTxnStatus(status)

            //last transaction date time
            val date: String = getSubString(df33_data, 84, 90)
            val finaltxndate = if (emvUtils.getHexatoDecimal(date).toInt() == 0) {
                "--"
            } else {
                emvUtils.calculateSecondsOfTxn(
                    data_5F25 + "000000",
                    emvUtils.getHexatoDecimal(date)
                )
            }



            //last equipment id
            val terminalInfo: String = getSubString(df33_data, 72, 84)
            var stationsTable:StationsTable?=null
            var lastStationName= ""
            var lastStationStringId:String=""
            if(!terminalInfo.all { it == '0' }){
                val parsedValues = parseTerminalHexValue(terminalInfo)

                val lineId = parsedValues["lineId"]
                val stationNumber = parsedValues["stationNumber"]
                val equipmentGroupId = parsedValues["equipmentGroupId"]

                //last station name
                val lastStationId =""+lineId+stationNumber

                runBlocking {
                    stationsTable = databaseCall.getStationByStationId(lastStationId)
                }

                if(stationsTable!=null){
                    lastStationName = stationsTable!!.stationName.toString()
                    lastStationStringId = stationsTable!!.stationId
                }
            }





            //history last transactions
            //history log1


//-----------------------------------------------------------------------------------------------------------------


            //history log1
            var datentime1: String = getSubString(df33_data, 118, 124)
            datentime1 =
                if (emvUtils.getHexatoDecimal(datentime1)
                        .toInt() == 0
                ) "-- --" else emvUtils.calculateSecondsOfTxn(
                    data_5F25 + "000000",
                    emvUtils.getHexatoDecimal(datentime1)
                )

            val txn_amnt_str1: String =
                getSubString(df33_data, 128, 132)
            val txn_amnt1 = (emvUtils.getHexatoDecimal(txn_amnt_str1)).toDouble() / 10

            val txnseqno1 = "" + emvUtils.getHexatoDecimal(
                getSubString(
                    df33_data,
                    124,
                    128
                )
            )
            val transtype1 =
                if (datentime1 == "-- --") "--" else getHistoryTxnStatus(
                    getSubString(df33_data, 137, 138)
                )

            var terminalInfo1: String = getSubString(df33_data, 106, 118)
            var h1_lastStationName= ""
            var h1_lastStationStringId:String=""
            var equipmentGroupId1:String=""
            stationsTable=null

            if(!terminalInfo1.all { it == '0' }){
                val parsedValues1 = parseTerminalHexValue(terminalInfo1)

                val lineId1 = parsedValues1["lineId"]
                val stationNumber1 = parsedValues1["stationNumber"]
                 equipmentGroupId1 =""+ parsedValues1["equipmentGroupId"]

                //last station name
                val h1_lastStationId =""+lineId1+stationNumber1

                runBlocking {
                    stationsTable = databaseCall.getStationByStationId(h1_lastStationId)
                }

                if(stationsTable!=null){
                    h1_lastStationName = stationsTable!!.stationName.toString()
                    h1_lastStationStringId = stationsTable!!.stationId
                }
            }




            //history log2
            var datentime2: String =
                getSubString(df33_data, 152, 158)
            datentime2 =
                if (emvUtils.getHexatoDecimal(datentime2)
                        .toInt() == 0
                ) "-- --" else emvUtils.calculateSecondsOfTxn(
                    data_5F25 + "000000",
                    emvUtils.getHexatoDecimal(datentime2)
                )
            val txn_amnt_str2: String =
                getSubString(df33_data, 162, 166)
            val txn_amnt2 = (emvUtils.getHexatoDecimal(txn_amnt_str2)).toDouble() / 10

            val txnseqno2 = "" + emvUtils.getHexatoDecimal(
                getSubString(
                    df33_data,
                    158,
                    162
                )
            )
            val transtype2 =
                if (datentime2 == "-- --") "--" else getHistoryTxnStatus(
                    getSubString(df33_data, 171, 172)
                )


            //terminal info 2
            var terminalInfo2: String = getSubString(df33_data, 140, 152)
            var h2_lastStationName= ""
            var h2_lastStationStringId:String=""
            var equipmentGroupId2:String=""
            stationsTable=null

            if(!terminalInfo2.all { it == '0' }) {
                val parsedValues2 = parseTerminalHexValue(terminalInfo2)

                val lineId2 = parsedValues2["lineId"]
                val stationNumber2 = parsedValues2["stationNumber"]
                 equipmentGroupId2 = ""+parsedValues2["equipmentGroupId"]

                //last station name
                val h2_lastStationId = "" + lineId2 + stationNumber2

                runBlocking {
                    stationsTable = databaseCall.getStationByStationId(h2_lastStationId)
                }

                if (stationsTable != null) {
                    h2_lastStationName = stationsTable!!.stationName.toString()
                    h2_lastStationStringId = stationsTable!!.stationId
                }
            }

            //history log3
            var datentime3: String =
                getSubString(df33_data, 186, 192)
            datentime3 =
                if (emvUtils.getHexatoDecimal(datentime3)
                        .toInt() == 0
                ) "-- --" else emvUtils.calculateSecondsOfTxn(
                    data_5F25 + "000000",
                    emvUtils.getHexatoDecimal(datentime3)
                )
            val txn_amnt_str3: String =
                getSubString(df33_data, 196, 200)
            val txn_amnt3 = (emvUtils.getHexatoDecimal(txn_amnt_str3)).toDouble() / 10

            val txnseqno3 = "" + emvUtils.getHexatoDecimal(
                getSubString(
                    df33_data,
                    192,
                    196
                )
            )
            val transtype3 =
                if (datentime3 == "-- --") "--" else getHistoryTxnStatus(
                    getSubString(df33_data, 205, 206)
                )


            //terminal info 3
            var terminalInfo3: String = getSubString(df33_data, 174, 186)
            var h3_lastStationName= ""
            var h3_lastStationStringId:String=""
            var equipmentGroupId3:String=""
            stationsTable=null
            if(!terminalInfo3.all { it== '0' }){
                val parsedValues3 = parseTerminalHexValue(terminalInfo3)

                val lineId3 = parsedValues3["lineId"]
                val stationNumber3 = parsedValues3["stationNumber"]
                 equipmentGroupId3 = ""+parsedValues3["equipmentGroupId"]

                //last station name
                val h3_lastStationId =""+lineId3+stationNumber3
                runBlocking {
                    stationsTable = databaseCall.getStationByStationId(h3_lastStationId)
                }

                if(stationsTable!=null){
                    h3_lastStationName = stationsTable!!.stationName.toString()
                    h3_lastStationStringId = stationsTable!!.stationId
                }
            }


            //history log4
            var datentime4: String =
                getSubString(df33_data, 220, 226)
            datentime4 =
                if (emvUtils.getHexatoDecimal(datentime4)
                        .toInt() == 0
                ) "-- --" else emvUtils.calculateSecondsOfTxn(
                    data_5F25 + "000000",
                    emvUtils.getHexatoDecimal(datentime4)
                )

            val txn_amnt_str4: String =
                getSubString(df33_data, 230, 234)
            val txn_amnt4 = (emvUtils.getHexatoDecimal(txn_amnt_str4)).toDouble() / 10
            val txnseqno4 = "" + emvUtils.getHexatoDecimal(
                getSubString(
                    df33_data,
                    226,
                    230
                )
            )
            val transtype4 =
                if (datentime4 == "-- --") "--" else getHistoryTxnStatus(
                    getSubString(df33_data, 239, 240)
                )



            //terminal info 3
            var terminalInfo4: String = getSubString(df33_data, 208, 220)
            var h4_lastStationName= ""
            var h4_lastStationStringId:String=""
            var equipmentGroupId4:String=""

            if(!terminalInfo4.all { it == '0' }){
                val parsedValues4 = parseTerminalHexValue(terminalInfo4)

                val lineId4 = parsedValues4["lineId"]
                val stationNumber4 = parsedValues4["stationNumber"]
                 equipmentGroupId4 = ""+parsedValues4["equipmentGroupId"]

                //last station name
                val h4_lastStationId =""+lineId4+stationNumber4

                runBlocking {
                    stationsTable = databaseCall.getStationByStationId(h4_lastStationId)
                }

                if(stationsTable!=null){
                    h4_lastStationName = stationsTable!!.stationName.toString()
                    h4_lastStationStringId = stationsTable!!.stationId
                }
            }



            //adding the transaction history
            val transactions = mutableListOf<TxnHistory>()
            transactions.add(
                TxnHistory(
                    txnseqno1,
                    datentime1.split(" ")[0],
                    datentime1.split(" ")[1],
                    emvUtils.df.format(txn_amnt1),
                    getTxnStatus(transtype1),
                    h1_lastStationName,
                    h1_lastStationStringId,
                    equipmentGroupId1.toString()
                )
            )
            transactions.add(
                TxnHistory(
                    txnseqno2,
                    datentime2.split(" ")[0],
                    datentime2.split(" ")[1],
                    emvUtils.df.format(txn_amnt2),
                    getTxnStatus(transtype2),
                    h2_lastStationName,
                    h2_lastStationStringId,
                    equipmentGroupId2.toString()

                )
            )
            transactions.add(
                TxnHistory(
                    txnseqno3,
                    datentime3.split(" ")[0],
                    datentime3.split(" ")[1],
                    emvUtils.df.format(txn_amnt3),
                    getTxnStatus(transtype3),
                    h3_lastStationName,
                    h3_lastStationStringId,
                    equipmentGroupId3.toString()
                )
            )
            transactions.add(
                TxnHistory(
                    txnseqno4,
                    datentime4.split(" ")[0],
                    datentime4.split(" ")[1],
                    emvUtils.df.format(txn_amnt4),
                    getTxnStatus(transtype4),
                    h4_lastStationName,
                    h4_lastStationStringId,
                    equipmentGroupId4.toString()
                )
            )


            //card effective date
            val cardEffectiveDate = data_5F25

            //CSA full data for display
            val csaDataDisplay = CSADataDisplay(
                cardbalance,
                cardBalanceFormat,
                0.0,
                "",
                errorFormat,
                error_code.toInt(),
                finaltxndate,
                statusFormat,
                lastStationName,
                lastStationStringId,
                statusValue,
                cardEffectiveDate,
                transactions
            )

            //CSA raw data for writing
            val csaRawData = setCsaRawData(df33_data)

            //CSA binary data
            val csaBinData = setCsaBinValue(bF200Data.serviceRelatedData!!.toByteArray().sliceArray(bF200Data.serviceDataIndex!!..bF200Data.serviceDataIndex!!+95))

            //set master data
            csaMasterData.csaDisplayData = csaDataDisplay
            csaMasterData.csaBinData = csaBinData
            csaMasterData.csaUpdatedBinData=csaBinData
            Log.e("TAG","SUCCESS parsing")

            return csaMasterData

        }catch (ex:Exception){
            Log.e("TAG","Exception :${ex.printStackTrace()}")
            return csaMasterData
        }
    }






    /**
     * Method to set the CSA Hex values
     */
    private fun setCsaRawData(tagDF33:String):CSADataRaw{



        //set service data
        val serviceData= ServiceData()
        serviceData.serviceIndex= tagDF33.substring(0,2)
        serviceData.serviceId= tagDF33.substring(2,6)
        serviceData.serviceControl= tagDF33.substring(6,10)
        serviceData.kcv= tagDF33.substring(10,16)
        serviceData.keyPRMacqKeyIndex= tagDF33.substring(16,18)
        serviceData.rfu= tagDF33.substring(18,26)
        serviceData.serviceLastUpdateAtc= tagDF33.substring(26,30)
        serviceData.serviceLastUpdateDateTime= tagDF33.substring(30,42)
        serviceData.serviceAtc= tagDF33.substring(42,46)
        serviceData.serviceBalance= tagDF33.substring(46,58)
        serviceData.serviceCurrency= tagDF33.substring(58,62)
        serviceData.serviceDataLength= tagDF33.substring(62,64)


        //set general data
        val generalData = GeneralData()
        generalData.version = tagDF33.substring(64,66)
        generalData.langRfu = tagDF33.substring(66,68)


        //set validation data
        val validationData = ValidationData()
        validationData.errorCode = tagDF33.substring(68,70)
        validationData.productType = tagDF33.substring(70,72)

        //current terminal info
        val terminalData = TerminalData()
        terminalData.acquirerId = tagDF33.substring(72,76)
        terminalData.operatorId = tagDF33.substring(76,80)
        terminalData.terminalId = tagDF33.substring(80,84)
        validationData.terminalInfo= terminalData

        validationData.txnDateTime = tagDF33.substring(84,90)
        validationData.fareAmount = tagDF33.substring(90,94)
        validationData.routeNo = tagDF33.substring(94,98)
        validationData.serviceProviderData = tagDF33.substring(98,104)
        validationData.txnStatus = tagDF33.substring(104,105)
        validationData.rfu = tagDF33.substring(105,106)


        //set history data 1
        val historyData1 = HistoryData()

        //set history terminal 1 data
        val terminalData1 = TerminalData()
        terminalData.acquirerId = tagDF33.substring(106,110)
        terminalData.operatorId = tagDF33.substring(110,114)
        terminalData.terminalId = tagDF33.substring(114,118)
        historyData1.terminalInfo = terminalData1

        historyData1.txnDateTime = tagDF33.substring(118,124)
        historyData1.txnSeqNumber = tagDF33.substring(124,128)
        historyData1.txnAmount = tagDF33.substring(128,132)
        historyData1.cardBalance = tagDF33.substring(132,137)
        historyData1.txnStatus = tagDF33.substring(137,138)
        historyData1.rfu = tagDF33.substring(138,140)


        //set history data 2
        val historyData2 = HistoryData()

        //set history terminal 2 data
        val terminalData2 = TerminalData()
        terminalData.acquirerId = tagDF33.substring(140,144)
        terminalData.operatorId = tagDF33.substring(144,148)
        terminalData.terminalId = tagDF33.substring(148,152)
        historyData2.terminalInfo = terminalData2

        historyData2.txnDateTime = tagDF33.substring(152,158)
        historyData2.txnSeqNumber = tagDF33.substring(158,162)
        historyData2.txnAmount = tagDF33.substring(162,166)
        historyData2.cardBalance = tagDF33.substring(166,171)
        historyData2.txnStatus = tagDF33.substring(171,172)
        historyData2.rfu = tagDF33.substring(172,174)



        //set history data 3
        val historyData3 = HistoryData()

        //set history terminal 3 data
        val terminalData3 = TerminalData()
        terminalData.acquirerId = tagDF33.substring(174,178)
        terminalData.operatorId = tagDF33.substring(178,182)
        terminalData.terminalId = tagDF33.substring(182,186)
        historyData3.terminalInfo = terminalData3

        historyData3.txnDateTime = tagDF33.substring(186,192)
        historyData3.txnSeqNumber = tagDF33.substring(192,196)
        historyData3.txnAmount = tagDF33.substring(196,200)
        historyData3.cardBalance = tagDF33.substring(200,205)
        historyData3.txnStatus = tagDF33.substring(205,206)
        historyData3.rfu = tagDF33.substring(206,208)


        //set history data 4
        val historyData4 = HistoryData()

        //set history terminal 4 data
        val terminalData4 = TerminalData()
        terminalData.acquirerId = tagDF33.substring(208,212)
        terminalData.operatorId = tagDF33.substring(212,216)
        terminalData.terminalId = tagDF33.substring(216,220)
        historyData4.terminalInfo = terminalData4

        historyData4.txnDateTime = tagDF33.substring(220,226)
        historyData4.txnSeqNumber = tagDF33.substring(226,230)
        historyData4.txnAmount = tagDF33.substring(230,234)
        historyData4.cardBalance = tagDF33.substring(234,239)
        historyData4.txnStatus = tagDF33.substring(239,240)
        historyData4.rfu = tagDF33.substring(240,242)


        //create history queue
        val historyQueue = HistoryQueue<HistoryData>(4)
        historyQueue.add(historyData1)
        historyQueue.add(historyData2)
        historyQueue.add(historyData3)
        historyQueue.add(historyData4)

        //set csa raw data to send back
        csaRawData = CSADataRaw(serviceData = serviceData,
            generalData= generalData,
            validationData=validationData,
            historyData=historyQueue,
            rfu = tagDF33.substring(242,256)
            )
//        csaRawData.serviceData= serviceData
//        csaRawData.generalData= generalData
//        csaRawData.validationData =validationData
//        csaRawData.historyData=historyQueue
//        csaRawData.rfu = tagDF33.substring(242,256)

        return csaRawData

    }

    /**
     * Set CSA Bytes values
     */
    private fun setCsaBinValue(byteArray: ByteArray):CsaBin{

        // Create a ByteBuffer wrapping the byteArray and set it to little-endian order
        val buffer = ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN)

        // Extract generalInfo
        val versionNumber = buffer.get()
        val languageInfo = buffer.get()

        val generalInfo = GeneralBin(versionNumber, languageInfo)

        // Extract validationData
        val errorCode = buffer.get()
        val productType = buffer.get()
        val acquirerID = buffer.get()
        val operatorID = ByteArray(2)
        buffer.get(operatorID)
        val terminalID = ByteArray(3)
        buffer.get(terminalID)
        val trxDateTime = ByteArray(3)
        buffer.get(trxDateTime)
        val fareAmt = ByteArray(2)
        buffer.get(fareAmt)
        val routeNo = ByteArray(2)
        buffer.get(routeNo)
        val serviceProviderData = ByteArray(3)
        buffer.get(serviceProviderData)
        val trxStatus = buffer.get()

        val validationData = ValidationBin(
            errorCode, productType, acquirerID, operatorID, terminalID,
            trxDateTime, fareAmt, routeNo, serviceProviderData, trxStatus
        )

        // Extract history (4 entries, each 17 bytes)
        val history = List(4) {
            val acquirerID = buffer.get()
            val operatorID = ByteArray(2)
            buffer.get(operatorID)
            val terminalID = ByteArray(3)
            buffer.get(terminalID)
            val trxDateTime = ByteArray(3)
            buffer.get(trxDateTime)
            val trxSeqNum = ByteArray(2)
            buffer.get(trxSeqNum)
            val trxAmt = ByteArray(2)
            buffer.get(trxAmt)
            val cardBalance1 = buffer.get()
            val cardBalance2 = buffer.get()
            val cardBalance3 = buffer.get() // cardBalance3 and trxStatus share bits
            val trxStatus = cardBalance3
            val rfu = buffer.get()

            HistoryBin(acquirerID, operatorID, terminalID, trxDateTime, trxSeqNum, trxAmt, cardBalance1, cardBalance2, cardBalance3, trxStatus, rfu)
        }

        // Extract rfu
        val rfu = ByteArray(7)
        buffer.get(rfu)

        //create history queue
        val historyQueue = HistoryQueue<HistoryBin>(4)
        historyQueue.add(history.get(0))
        historyQueue.add(history.get(1))
        historyQueue.add(history.get(2))
        historyQueue.add(history.get(3))

        return CsaBin(generalInfo, validationData, historyQueue, rfu)
    }


    /**
     * Set OSA Bytes values
     */
    fun setOsaBinValue(byteArray: ByteArray): OsaBin {
        val buffer = ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN)

        // Extract GeneralInfo
        val versionNumber = buffer.get()
        val languageInfo = buffer.get()
        val generalInfo = GeneralBinOsa(versionNumber, languageInfo)

        // Extract ValidationData
        val errorCode = buffer.get()
        val productType = buffer.get()
        val trxDateTime = ByteArray(3)
        buffer.get(trxDateTime)
        val stationCode = ByteArray(2)
        buffer.get(stationCode)
        val trxStatusAndRfu = buffer.get()

        val validationData = ValidationBinOsa(errorCode, productType, trxDateTime, stationCode, trxStatusAndRfu)

        // Extract History (2 entries, each 16 bytes)
        val history = List(2) {
            val acquirerID = buffer.get()
            val operatorID = ByteArray(2)
            buffer.get(operatorID)
            val terminalID = ByteArray(3)
            buffer.get(terminalID)
            val trxDateTime = ByteArray(3)
            buffer.get(trxDateTime)
            val trxSeqNum = ByteArray(2)
            buffer.get(trxSeqNum)
            val trxAmt = ByteArray(2)
            buffer.get(trxAmt)
            val cardBalance1 = buffer.get()
            val cardBalance2 = buffer.get()
            val trxStatus = buffer.get()
            val productType = buffer.get()
            val rfu = buffer.get()

            HistoryBinOsa(acquirerID, operatorID, terminalID, trxDateTime, trxSeqNum, trxAmt, cardBalance1, cardBalance2, trxStatus, productType, rfu)
        }

        // Extract Passes (3 entries)
        val passes = List(3) {
            val productType = buffer.get()
            val passLimit = buffer.get()
            val startDateTime = ByteArray(3)
            buffer.get(startDateTime)
            val endDateTime = ByteArray(2)
            buffer.get(endDateTime)
            val validZoneId = buffer.get()
            val validEntryStationId = buffer.get()
            val validExitStationId = buffer.get()
            val tripCount = buffer.get()
            val lastConsumedDate = ByteArray(2)
            buffer.get(lastConsumedDate)
            val dailyLimit = buffer.get()
            val priority = buffer.get()

            PassBin(productType, passLimit, startDateTime, endDateTime, validZoneId, validEntryStationId, validExitStationId, tripCount, lastConsumedDate, dailyLimit, priority)
        }

        val rfu = ByteArray(5)
        buffer.get(rfu)

        // Create History Queue
        val historyQueue = HistoryQueue<HistoryBinOsa>(2)
        historyQueue.add(history[0])
        historyQueue.add(history[1])

        return OsaBin(generalInfo, validationData, historyQueue, passes,rfu)
    }






    /**
     * method to convert Csa bin to ByteArray
     */
    fun csaToByteArray(csaBin: CsaBin): ByteArray {
        // Create a ByteBuffer of appropriate size and set it to little-endian order
        val buffer = ByteBuffer.allocate(96).order(ByteOrder.LITTLE_ENDIAN) // Adjust the size accordingly

        // Serialize generalInfo
        buffer.put(csaBin.generalInfo.versionNumber)
        buffer.put(csaBin.generalInfo.languageInfo)

        // Serialize validationData
        buffer.put(csaBin.validationData.errorCode!!)
        buffer.put(csaBin.validationData.productType)
        buffer.put(csaBin.validationData.acquirerID)
        buffer.put(csaBin.validationData.operatorID)
        buffer.put(csaBin.validationData.terminalID)
        buffer.put(csaBin.validationData.trxDateTime)
        buffer.put(csaBin.validationData.fareAmt)
        buffer.put(csaBin.validationData.routeNo)
        buffer.put(csaBin.validationData.serviceProviderData)
        buffer.put(csaBin.validationData.trxStatusAndRfu)


         // Serialize history (4 entries, each 17 bytes)
         for (history in csaBin.history) {
             // Ensure the buffer has enough remaining space
             if (buffer.remaining() < 17) {
                 throw BufferOverflowException()
             }

             buffer.put(history.acquirerID!!) // Single byte
             buffer.put(history.operatorID!!)
             buffer.put(history.terminalID!!)
             buffer.put(history.trxDateTime!!)
             buffer.put(history.trxSeqNum!!)
             buffer.put(history.trxAmt!!)
             buffer.put(history.cardBalance1!!) // Single byte
             buffer.put(history.cardBalance2!!) // Single byte
             buffer.put(history.cardBalance3!!) // Single byte
             buffer.put(history.rfu!!) // Single byte
         }


        // Serialize rfu (7 bytes)
        buffer.put(csaBin.rfu)

        // Return the byte array
        return buffer.array()
    }

    /**
     * Method to convert Osa bin to ByteArray
     */
     fun osaBinToByteArray(osaBin: OsaBin): ByteArray {
        // Initialize a ByteBuffer with a size large enough to hold the full byte array
        val buffer = ByteBuffer.allocate(96).order(ByteOrder.LITTLE_ENDIAN)

        // Add generalInfo fields
        buffer.put(osaBin.generalInfo.versionNumber)
        buffer.put(osaBin.generalInfo.languageInfo)

        // Add validationData fields
        buffer.put(osaBin.validationData.errorCode ?: 0) // Handle null case with 0
        buffer.put(osaBin.validationData.productType)
        buffer.put(osaBin.validationData.trxDateTime) // 3 bytes
        buffer.put(osaBin.validationData.stationCode) // 2 bytes
        buffer.put(osaBin.validationData.trxStatusAndRfu)


        // Serialize history (2 entries, each 17 bytes)
        for (historyBin in osaBin.history) {
            buffer.put(historyBin.acquirerID!!)
            buffer.put(historyBin.operatorID!!) // 2 bytes
            buffer.put(historyBin.terminalID!!) // 3 bytes
            buffer.put(historyBin.trxDateTime!!) // 3 bytes
            buffer.put(historyBin.trxSeqNum!!) // 2 bytes
            buffer.put(historyBin.passLimit!!) // 2 bytes
            buffer.put(historyBin.tripCount!!)
            buffer.put(historyBin.dailyLimit!!)
            buffer.put(historyBin.trxStatus!!) //  trxStatus
            buffer.put(historyBin.productType!!) // product type
            buffer.put(historyBin.rfu!!)
        }

        for (passBin in osaBin.passes) {
            buffer.put(passBin.productType ?: 0)
            buffer.put(passBin.passLimit ?: 0)
            buffer.put(passBin.startDateTime ?: ByteArray(3)) // 3 bytes
            buffer.put(passBin.endDateTime ?: ByteArray(2)) // 2 bytes
            buffer.put(passBin.validZoneId ?: 0) // 1 Byte
            buffer.put(passBin.validEntryStationId ?: 0) // 1 Byte
            buffer.put(passBin.validExitStationId ?: 0) // 1 Byte
            buffer.put(passBin.tripCount ?: 0) // 1 Byte
            buffer.put(passBin.lastConsumedDate ?: ByteArray(2))
            buffer.put(passBin.dailyLimit ?: 0)
            buffer.put(passBin.priority ?: 0)
        }



        // Serialize rfu (7 bytes)
        buffer.put(osaBin.rfu)

        // Return the byte array
        return buffer.array()
    }



    fun getSubString(originalstring: String, startIndex: Int, endIndex: Int): String {
        return originalstring.substring(startIndex, endIndex)
    }

    fun setSubstring(originalString: String, startIndex: Int, replacement: String): String {
        require(startIndex >= 0 && startIndex + replacement.length <= originalString.length) {
            "Invalid start index or replacement length"
        }

        return originalString.substring(0, startIndex) +
                replacement +
                originalString.substring(startIndex + replacement.length)
    }

    fun getTxnStatus(status: String): String {
        var status = status
        if (status == "10") {
            status = "Entry"
        } else if (status == "00") {
            status = "Exit"
        } else if (status == "20") {
            status = "Penalty"
        }
        return status
    }

    fun getTxnStatusNCMC(status: String): Int {
        var txnStatus=-1
        if (status == "0") {
            txnStatus = IPCConstants.TXN_STATUS_EXIT
        } else if (status == "1") {
            txnStatus = IPCConstants.TXN_STATUS_ENTRY
        } else if (status == "2") {
            txnStatus = IPCConstants.TXN_STATUS_PENALTY
        }
        return txnStatus
    }

    fun getTxnStatusFromHex(status:Int):String{
        var trxStatus = ""
        when(status){
            IPCConstants.TXN_STATUS_EXIT->{
                trxStatus = "Exit"
            }

            IPCConstants.TXN_STATUS_ENTRY->{
                trxStatus = "Entry"
            }

            IPCConstants.TXN_STATUS_PENALTY->{
                trxStatus = "Penalty"
            }

        }

        return trxStatus
    }

    fun getHistoryTxnStatus(status: String): String {
        var status = status
        if (status == "0") {
            status = "Exit"
        } else if (status == "1") {
            status = "Entry"
        } else if (status == "2") {
            status = "Penalty"
        }
        return status
    }


    fun getError(error: String?): String {
        /* 0x00
        No Error
        0x01
        Amount not sufficient for Entry/Exit
        0x02
        Torn Transaction
        0x03
        Entry not found in validation area in CSA
        0x04
        Exit not found in validation area in CSA
        0x05
        service area present but all pass invalid
        0x06
        Travel Time Exceeded
        0x07
        Card Expired*/
        var error = error
        error = when (error) {
            "00" -> "No Penalty"
            "01" -> "Insufficient Balance" // if it is unpaid area txn status is exit, else it is entry
            "02" -> "Torn Transaction"
            "03" -> "Entry not found " //   txn status is entry
            "04" -> "Exit not found " //   txn status is exit
            "05" -> "Service area present but all pass invalid"
            "06" -> "Travel Time Exceeded" // txn status is entry
            "07" -> "Card Expired" //no change in the card
            else -> "Unknown error"
        }
        return error
    }

    fun getLastEquipmentId(terminalStr: String): String {
        var terminalStr = terminalStr
        when (terminalStr) {
            "101101" -> terminalStr = "4001"
            "101142" -> terminalStr = "5001"
            "106182" -> terminalStr = "6001"
            "106181" -> terminalStr = "6001"
            "1010C0" -> terminalStr = "1132"
            "1060C0" -> terminalStr = "1164"
        }
        return terminalStr
    }


    /**
     * Method to read card effective date
     */
    fun readCardEffectiveDate(tag5F25: Int): String {
        val date = CharArray(8)

        // Assuming tag5F25 is an integer, you can extract the bytes by shifting and masking
        val byte0 = (tag5F25 shr 16) and 0xFF
        val byte1 = (tag5F25 shr 8) and 0xFF
        val byte2 = tag5F25 and 0xFF

        date[0] = (((byte2 and 0xF0) shr 4) + 0x30).toChar()
        date[1] = ((byte2 and 0x0F) + 0x30).toChar()

        date[2] = (((byte1 and 0xF0) shr 4) + 0x30).toChar()
        date[3] = ((byte1 and 0x0F) + 0x30).toChar()

        date[4] = 0x32.toChar()
        date[5] = 0x30.toChar()

        date[6] = (((byte0 and 0xF0) shr 4) + 0x30).toChar()
        date[7] = ((byte0 and 0x0F) + 0x30).toChar()

        val cardEffectiveDate = String(date)

        return cardEffectiveDate
    }

    fun readCardEffectiveDate(tag5F25: ByteArray): String {
        val date = CharArray(8)

        // Extract day from tag5F25[2]
        date[0] = ((tag5F25[2].toInt() and 0xF0) shr 4).plus(0x30).toChar()
        date[1] = (tag5F25[2].toInt() and 0x0F).plus(0x30).toChar()

        // Extract month from tag5F25[1]
        date[2] = ((tag5F25[1].toInt() and 0xF0) shr 4).plus(0x30).toChar()
        date[3] = (tag5F25[1].toInt() and 0x0F).plus(0x30).toChar()

        // Year is hardcoded as starting with "20"
        date[4] = '2'
        date[5] = '0'

        // Extract year from tag5F25[0]
        date[6] = ((tag5F25[0].toInt() and 0xF0) shr 4).plus(0x30).toChar()
        date[7] = (tag5F25[0].toInt() and 0x0F).plus(0x30).toChar()

        // Return the date as a String
        return String(date)
    }



    /**
     * Converts a hex string into a single Byte or a ByteArray depending on its length.
     * @param hex Hexadecimal string.
     * @return ByteArray for lengths greater than 2, or a single Byte for 2-character strings.
     */
    fun hexToBytes(hex: String): Any? {
        val len = hex.length
        return when {
            len % 2 != 0 -> null  // Hex string length must be even
            len == 2 -> hexToByte(hex)  // For 2-character hex strings, convert to single Byte
            else -> hexToByteArray(hex) // For longer strings, convert to ByteArray
        }
    }

    /**
     * Converts a two-character hex string (e.g., "1A") into a single Byte.
     * @param hex Two-character hex string.
     * @return Byte value, or null if the hex string is invalid.
     */
    fun hexToByte(hex: String): Byte? {
        val firstNibble = chr2nib(hex[0])
        val secondNibble = chr2nib(hex[1])

        if (firstNibble >= 0x10 || secondNibble >= 0x10) return null

        return ((firstNibble shl 4) or secondNibble).toByte()
    }


    fun byteToHex(byte: Byte): String {
        // Convert Byte to unsigned integer to handle values from 0x00 to 0xFF
        val unsignedByte = byte.toInt() and 0xFF

        // Convert the integer to a hexadecimal string with 2 digits
        return String.format("%02X", unsignedByte)
    }

    /**
     * Converts a longer hex string into a ByteArray.
     * @param hex Hexadecimal string.
     * @return ByteArray representation of the hex string.
     */
    fun hexToByteArray(hex: String): ByteArray? {
        val len = hex.length
        val byteArray = ByteArray(len / 2)

        for (i in byteArray.indices) {
            val firstNibble = chr2nib(hex[i * 2])
            val secondNibble = chr2nib(hex[i * 2 + 1])

            if (firstNibble >= 0x10 || secondNibble >= 0x10) return null

            byteArray[i] = ((firstNibble shl 4) or secondNibble).toByte()
        }
        return byteArray
    }


    fun byteArrayToHex(byteArray: ByteArray): String {
        return byteArray.joinToString("") { byte ->
            // Convert each byte to a 2-digit hexadecimal string and ensure uppercase
            String.format("%02X", byte)
        }
    }

    /**
     * Converts a single hex character to its corresponding nibble (4 bits).
     * @param c Hexadecimal character ('0'-'9', 'A'-'F', 'a'-'f').
     * @return Nibble value (0-15), or 16 if the character is invalid.
     */
    private fun chr2nib(c: Char): Int {
        return when (c) {
            in '0'..'9' -> c - '0'
            in 'A'..'F' -> c - 'A' + 10
            in 'a'..'f' -> c - 'a' + 10
            else -> 16 // Invalid hex character
        }
    }

    /**
     * Converts a Long integer into a ByteArray (hex format).
     * Example: 512 (0x200) -> [0x02, 0x00]
     *
     * @param num The long integer to be converted
     * @param len The length of the ByteArray
     * @return The ByteArray representing the binary (hex) format of the number
     */
    fun num2bin(num: Long, len: Int): ByteArray? {
        var number = num
        val bin = ByteArray(len)

        for (i in len - 1 downTo 0) {
            bin[i] = (number % 256).toByte()
            number /= 256
        }

        // If number is not zero, return null to indicate an error (equivalent to the C version's lblKO)
        return if (number == 0L) bin else null
    }

    /**
     * Converts a ByteArray (hex format) into a Long integer.
     * Example: [0x02, 0x00] -> 512 (0x200)
     *
     * @param bin The ByteArray to be converted
     * @param len The length of the ByteArray
     * @return The Long integer representing the binary (hex) format of the input
     */
    fun bin2num(bin: ByteArray, len: Int): Long {
        var num: Long = 0

        for (i in 0 until len) {
            num = num * 256 + (bin[i].toInt() and 0xFF)
        }

        return num
    }


    fun createTerminalID(stationId: Int, stationCategory: Int, stationSerial: Int): ByteArray {
        // Ensure that the inputs fit into their respective bit lengths
        require(stationId in 0..0xFFF) { "Station ID must be a 12-bit value (0 to 4095)" }
        require(stationCategory in 0..0x3F) { "Station Category must be a 6-bit value (0 to 63)" }
        require(stationSerial in 0..0x3F) { "Station Serial must be a 6-bit value (0 to 63)" }

        // Pack stationId, stationCategory, and stationSerial into 3 bytes
        val terminalId = ByteArray(3)

        // First byte: Upper 8 bits of stationId
        terminalId[0] = (stationId shr 4).toByte()

        // Second byte: Lower 4 bits of stationId and upper 4 bits of stationCategory
        terminalId[1] = ((stationId and 0x0F) shl 4 or (stationCategory shr 2)).toByte()

        // Third byte: Lower 2 bits of stationCategory and all 6 bits of stationSerial
        terminalId[2] = (((stationCategory and 0x03) shl 6) or (stationSerial and 0x3F)).toByte()

        return terminalId
    }


    fun extractTerminalID(terminalId: ByteArray): Triple<Int, Int, Int> {
        require(terminalId.size == 3) { "Terminal ID byte array must be exactly 3 bytes long" }

        // Extract the stationId (12 bits)
        val stationId = ((terminalId[0].toInt() and 0xFF) shl 4) or ((terminalId[1].toInt() and 0xF0) shr 4)

        // Extract the stationCategory (6 bits)
        val stationCategory = ((terminalId[1].toInt() and 0x0F) shl 2) or ((terminalId[2].toInt() and 0xC0) shr 6)

        // Extract the stationSerial (6 bits)
        val stationSerial = terminalId[2].toInt() and 0x3F

        return Triple(stationId, stationCategory, stationSerial)
    }

    fun readCardEffectiveDateFromHexString(tag5F25Hex: String): Long {
        // Convert the hex string to ByteArray
        val tag5F25 = hexStringToByteArray(tag5F25Hex)

        // Ensure tag5F25 has at least 3 bytes
        if (tag5F25.size < 3) {
            throw IllegalArgumentException("tag5F25 must contain at least 3 bytes")
        }

        // Extract year, month, and day from tag5F25 (in reverse order as in C code)
        val year = ((tag5F25[0].toInt() and 0xF0) shr 4) * 10 + (tag5F25[0].toInt() and 0x0F)
        val month = ((tag5F25[1].toInt() and 0xF0) shr 4) * 10 + (tag5F25[1].toInt() and 0x0F)
        val day = ((tag5F25[2].toInt() and 0xF0) shr 4) * 10 + (tag5F25[2].toInt() and 0x0F)

        // Construct the full date as a long value in the format YYYYMMDD
        val fullYear = 2000 + year // Assuming the century is 2000 (based on "20" prefix)
        return fullYear * 10000L + month * 100L + day
    }

    fun hexStringToByteArray(hex: String): ByteArray {
        val result = ByteArray(hex.length / 2)
        for (i in result.indices) {
            val index = i * 2
            val byte = hex.substring(index, index + 2).toInt(16).toByte()
            result[i] = byte
        }
        return result
    }





    fun calculateTrxTimeFromCardEffectiveDate(cardEffectiveDate: String, trxTimeFromEpoch: Long): Long {
        // Input cardEffectiveDate is expected in DDMMYYYY format
        if (cardEffectiveDate.length != 8) {
            throw IllegalArgumentException("Card effective date must be in DDMMYYYY format")
        }

        // Extract day, month, and year from the cardEffectiveDate string
        val day = cardEffectiveDate.substring(0, 2).toInt()
        val month = cardEffectiveDate.substring(2, 4).toInt()
        val year = cardEffectiveDate.substring(4, 8).toInt()

        // Create a calendar instance and set the card's effective date
        val cardEffCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1) // Months are 0-based in Calendar
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Convert card's effective date to epoch time (seconds since Unix epoch)
        val cardEffDateFromEpoch = cardEffCalendar.timeInMillis / 1000

        // Calculate the transaction time relative to card effective date in minutes
        val trxTimeFromCardEffDate = (trxTimeFromEpoch / 60) - (cardEffDateFromEpoch / 60)

        // For debugging: print the cardEffectiveDate and calculated times
        println("Card Effective Date: ${SimpleDateFormat("dd/MM/yyyy").format(cardEffCalendar.time)}")
        println("trxTimeFromEpoch: $trxTimeFromEpoch, cardEffDateFromEpoch: $cardEffDateFromEpoch, trxTimeFromCardEffDate: $trxTimeFromCardEffDate")

        return trxTimeFromCardEffDate
    }

    fun convertAmountToBCD(amount: Long, numericAmount: ByteArray?) {
        val AMOUNT_NUMERIC_SIZE = 12 // Define this according to your specific requirements

        if (numericAmount != null) {
            numericAmount.fill(0, 0, AMOUNT_NUMERIC_SIZE / 2)
            var tmpamount = amount
            var i = 0
            while (tmpamount > 0) {
                numericAmount[(AMOUNT_NUMERIC_SIZE / 2) - 1 - (i / 2)] =
                    (numericAmount[(AMOUNT_NUMERIC_SIZE / 2) - 1 - (i / 2)].toInt() + ((tmpamount % 10).toInt() shl (4 * (i % 2)))).toByte()
                tmpamount /= 10
                i++
            }
        }
    }


    fun combineToByte(value1: Int, value2: Int): Byte {
        // Ensure both values fit within 4 bits
        require(value1 in 0..15) { "value1 must be a 4-bit value (0-15)" }
        require(value2 in 0..15) { "value2 must be a 4-bit value (0-15)" }

        // Shift value1 to the upper 4 bits and combine with value2
        return ((value1 shl 4) or value2).toByte()
    }

    fun getFirst4Bits(value: Byte): Int {
        return (value.toInt() shr 4) and 0x0F
    }


//    fun hexStringToByteArray(hex: String): ByteArray {
//        val cleanHex = hex.filterNot { it.isWhitespace() }  // In case the string has spaces
//        val result = ByteArray(cleanHex.length / 2)
//        for (i in cleanHex.indices step 2) {
//            result[i / 2] = ((cleanHex[i].digitToInt(16) shl 4) + cleanHex[i + 1].digitToInt(16)).toByte()
//        }
//        return result
//    }
//
//    fun classToByteArray(anyClass: Any): ByteArray {
//        val byteArrayList = mutableListOf<Byte>()
//        val properties = anyClass::class.memberProperties
//
//        // Iterate through all properties of the class
//        for (property in properties) {
//            // Only handle properties that are strings (representing hex values)
//            val value = property.get(anyClass) as? String
//            if (value != null && value.isNotEmpty()) {
//                // Convert hex string to byte array and add it to the list
//                val byteArray = hexStringToByteArray(value)
//                byteArrayList.addAll(byteArray.toList())
//            }
//        }
//        return byteArrayList.toByteArray()
//    }

    fun parseTerminalHexValue(hexValue1: String): Map<String, String> {

        var hexValue =""

        //remove acq id and operator id from terminal info
        if(hexValue1.length !=6){
            hexValue = hexValue1.substring(6,hexValue1.length)
        }else{
            hexValue = hexValue1
        }


        require(hexValue.length == 6) { "Hexadecimal value must be exactly 6 characters long" }

        val bytes = hexValue.chunked(2).map { it.toInt(16) }

        // Bytes from the encoded string
        val byte1 = bytes[0]
        val byte2 = bytes[1]
        val byte3 = bytes[2]

        // Line ID: First 4 bits from byte1
        val lineId = (byte1 shr 4) and 0x0F

        // Station ID: Next 8 bits (lower 4 bits from byte1 and upper 4 bits from byte2)
        val stationNumber = ((byte1 and 0x0F) shl 4) or (byte2 shr 4)

        // Equipment Group ID: Lower 6 bits from byte2 and byte3
        val equipmentGroupId = ((byte2 and 0x0F) shl 2) or (byte3 shr 6)

        // System Number: Last 6 bits from byte3
        val systemNumber = byte3 and 0x3F

        return mapOf(
            "lineId" to lineId.toString().padStart(2, '0'),
            "stationNumber" to stationNumber.toString().padStart(2, '0'),
            "equipmentGroupId" to equipmentGroupId.toString().padStart(2, '0'),
            "systemNumber" to systemNumber.toString().padStart(2, '0')
        )

//        println("Line ID: ${parsedValues["lineId"]}")
//        println("Station ID: ${parsedValues["stationId"]}")
//        println("Equipment Group ID: ${parsedValues["equipmentGroupId"]}")
//        println("System Number: ${parsedValues["systemNumber"]}")
    }


    fun createHexFromTerminalData(lineId: Int, stationId: Int, equipmentGroupId: Int, systemNumber: Int): String {
        // Ensure that inputs are valid within their respective bit lengths
        require(lineId in 0..15) { "Line ID must be between 0 and 15 (4 bits)" }
        require(stationId in 0..255) { "Station ID must be between 0 and 255 (8 bits)" }
        require(equipmentGroupId in 0..63) { "Equipment Group ID must be between 0 and 63 (6 bits)" }
        require(systemNumber in 0..63) { "System Number must be between 0 and 63 (6 bits)" }

        // Create the three bytes
        val byte1 = (lineId shl 4) or (stationId shr 4)  // Line ID in upper 4 bits, part of Station ID in lower 4 bits
        val byte2 = (stationId and 0x0F) shl 4 or (equipmentGroupId shr 2)  // Lower part of Station ID and upper part of Equipment Group ID
        val byte3 = (equipmentGroupId and 0x03) shl 6 or systemNumber  // Lower part of Equipment Group ID and System Number

        // Convert to hexadecimal string (padding to ensure 6 digits)
        return String.format("%02X%02X%02X", byte1, byte2, byte3)
    }


    fun getDateTimeFromHex(hex:String,data_5F25:String):String{

       return emvUtils.calculateSecondsOfTxn(
            data_5F25 + "000000",
            emvUtils.getHexatoDecimal(hex)
        )
    }

}