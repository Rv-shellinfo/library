package com.shellinfo.common.utils.ipc

import android.util.Log
import com.shellinfo.common.data.local.data.emv_rupay.CSAMasterData
import com.shellinfo.common.data.local.data.emv_rupay.HistoryQueue
import com.shellinfo.common.data.local.data.emv_rupay.binary.csa_bin.CsaBin
import com.shellinfo.common.data.local.data.emv_rupay.binary.csa_bin.GeneralBin
import com.shellinfo.common.data.local.data.emv_rupay.binary.csa_bin.HistoryBin
import com.shellinfo.common.data.local.data.emv_rupay.binary.csa_bin.ValidationBin
import com.shellinfo.common.data.local.data.emv_rupay.display.CSADataDisplay
import com.shellinfo.common.data.local.data.emv_rupay.display.TxnHistory
import com.shellinfo.common.data.local.data.emv_rupay.raw.CSADataRaw
import com.shellinfo.common.data.local.data.emv_rupay.raw.GeneralData
import com.shellinfo.common.data.local.data.emv_rupay.raw.HistoryData
import com.shellinfo.common.data.local.data.emv_rupay.raw.ServiceData
import com.shellinfo.common.data.local.data.emv_rupay.raw.TerminalData
import com.shellinfo.common.data.local.data.emv_rupay.raw.ValidationData
import com.shellinfo.common.data.local.data.ipc.BF200Data
import com.shellinfo.common.utils.IPCConstants
import java.nio.BufferOverflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject
import kotlin.reflect.full.memberProperties


class RupayUtils @Inject constructor(
    private val emvUtils: EMVUtils
){

    //csa master data in which raw and display data combined
    private lateinit var csaMasterData: CSAMasterData

    //csa raw data i.e.hex values based on the index
    private lateinit var csaRawData: CSADataRaw

    //csa binary data i.e.byte values based on the index
    private lateinit var csaBinData: CsaBin

    //csa display data i.e. actual string values to show on the display
    private lateinit var csaDisplayData: CSADataDisplay

    /**
     * Method to read tlv data and set the raw and display csa data classes
     */
    fun readCSAData(bF200Data: BF200Data): CSAMasterData {

        val df33_data= bF200Data.b.DF33!!
        val data_5F25= bF200Data.b.`5F25`!!

        //init master csa data
        csaMasterData= CSAMasterData()

        try {

            //global wallet balance
            val cardBalance_str: String = getSubString(df33_data, 46, 58)
            val cardbalance = cardBalance_str.toDouble() / 100
            val cardBalanceFormat = emvUtils.df.format(cardbalance)

            //penalty amount
            val penalty_str: String = getSubString(df33_data, 90, 94)
            val penalty_amnt = penalty_str.toDouble() / 100
            val penaltyAmtFormat = emvUtils.df.format(penalty_amnt)

            //error string any
            val error_code: String = getSubString(df33_data, 68, 70)
            val errorFormat: String = getError(error_code)

            //status
            val status: String = getSubString(df33_data, 104, 106)
            val statusValue: Int = getTxnStatusNCMC(status)
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

            Log.e("error", errorFormat)
            Log.e("staus", statusFormat)
            Log.e("finaltxndate", finaltxndate)

            //last equipment id
            var lastequip: String = getSubString(df33_data, 72, 84)
            lastequip = getLastEquipmentId(lastequip.substring(6, 11))

            //last station name
            //TODO need to fix the station name
//            var lastStation = lastequip.substring(6, 9)
//            lastStation = "NAGOLE"
            var lastStation = "NAGOLE"



            //history last transactions
            //history log1


//-----------------------------------------------------------------------------------------------------------------


            //history log1
            var datentime1: String = getSubString(df33_data, 118, 124)
            datentime1 =
                if (emvUtils.getHexatoDecimal(datentime1)
                        .toInt() === 0
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

            val transStationId1 = getSubString(df33_data, 106, 118)


            //history log2
            var datentime2: String =
                getSubString(df33_data, 152, 158)
            datentime2 =
                if (emvUtils.getHexatoDecimal(datentime2)
                        .toInt() === 0
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

            val transStationId2 = getSubString(df33_data, 106, 118)

            //history log3
            var datentime3: String =
                getSubString(df33_data, 140, 152)
            datentime3 =
                if (emvUtils.getHexatoDecimal(datentime3)
                        .toInt() === 0
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

            val transStationId3 = getSubString(df33_data, 174, 186)

            //history log4
            var datentime4: String =
                getSubString(df33_data, 220, 226)
            datentime4 =
                if (emvUtils.getHexatoDecimal(datentime4)
                        .toInt() === 0
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

            val transStationId4 = getSubString(df33_data, 208, 220)

            var station_str1 = transStationId1.substring(6, 9)
            station_str1 =
                if (station_str1 == "000") "--" else emvUtils.getStationIdFromStationDetailList(
                    "0$station_str1"
                )

            var station_str2: String = transStationId2.substring(6, 9)
            station_str2 =
                if (station_str2 == "000") "--" else emvUtils.getStationIdFromStationDetailList(
                    "0$station_str2"
                )
            var station_str3: String = transStationId3.substring(6, 9)
            station_str3 =
                if (station_str3 == "000") "--" else emvUtils.getStationIdFromStationDetailList(
                    "0$station_str3"
                )
            var station_str4: String = transStationId4.substring(6, 9)
            station_str4 =
                if (station_str4 == "000") "--" else emvUtils.getStationIdFromStationDetailList(
                    "0$station_str4"
                )


            //adding the transaction history
            val transactions = mutableListOf<TxnHistory>()
            transactions.add(
                TxnHistory(
                    txnseqno1,
                    datentime1.split(" ")[0],
                    datentime1.split(" ")[1],
                    emvUtils.df.format(txn_amnt1),
                    getTxnStatus(transtype1),
                    station_str1
                )
            )
            transactions.add(
                TxnHistory(
                    txnseqno2,
                    datentime2.split(" ")[0],
                    datentime2.split(" ")[1],
                    emvUtils.df.format(txn_amnt2),
                    getTxnStatus(transtype2),
                    station_str2
                )
            )
            transactions.add(
                TxnHistory(
                    txnseqno3,
                    datentime3.split(" ")[0],
                    datentime3.split(" ")[1],
                    emvUtils.df.format(txn_amnt3),
                    getTxnStatus(transtype3),
                    station_str3
                )
            )
            transactions.add(
                TxnHistory(
                    txnseqno4,
                    datentime4.split(" ")[0],
                    datentime4.split(" ")[1],
                    emvUtils.df.format(txn_amnt4),
                    getTxnStatus(transtype4),
                    station_str4
                )
            )


            //card effective date
            val cardEffectiveDate = data_5F25

            //CSA full data for display
            val csaDataDisplay = CSADataDisplay(
                cardbalance,
                cardBalanceFormat,
                penalty_amnt,
                penaltyAmtFormat,
                errorFormat,
                error_code.toInt(),
                finaltxndate,
                statusFormat,
                lastStation,
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


    fun getSubString(originalstring: String, startIndex: Int, endIndex: Int): String {
        return originalstring.substring(startIndex, endIndex)
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
            "05" -> "service area present but all pass invalid"
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

    fun readCardEffectiveDate(tag5F25Hex: String): String {
        // Ensure the input hex string is valid (must be exactly 6 characters, representing 3 bytes)
        require(tag5F25Hex.length == 6) { "Input hex string must be exactly 6 characters long" }

        // Convert the hex string into a ByteArray (3 bytes)
        val tag5F25 = ByteArray(3)
        for (i in tag5F25.indices) {
            tag5F25[i] = tag5F25Hex.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }

        val date = CharArray(8)

        // Extract day (tag5F25[2])
        date[0] = ((tag5F25[2].toInt() and 0xF0) shr 4 + 0x30).toChar()
        date[1] = ((tag5F25[2].toInt() and 0x0F) + 0x30).toChar()

        // Extract month (tag5F25[1])
        date[2] = ((tag5F25[1].toInt() and 0xF0) shr 4 + 0x30).toChar()
        date[3] = ((tag5F25[1].toInt() and 0x0F) + 0x30).toChar()

        // Set the century (20)
        date[4] = '2'
        date[5] = '0'

        // Extract year (tag5F25[0])
        date[6] = ((tag5F25[0].toInt() and 0xF0) shr 4 + 0x30).toChar()
        date[7] = ((tag5F25[0].toInt() and 0x0F) + 0x30).toChar()

        // Return the final date string
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
}