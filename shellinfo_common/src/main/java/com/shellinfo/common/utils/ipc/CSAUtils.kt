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
import com.shellinfo.common.utils.IPCConstants
import javax.inject.Inject


class CSAUtils @Inject constructor(){

    @Inject
    lateinit var emvUtils: EMVUtils

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
    fun readCSAData(df33_data:String, data_5F25:String): CSAMasterData {

        //init master csa data
        csaMasterData= CSAMasterData()

        //global wallet balance
        val cardBalance_str: String = getSubString(df33_data,46,55)
        val cardbalance = cardBalance_str.toDouble() / 100
        val cardBalanceFormat = emvUtils.df.format(cardbalance)

        //penalty amount
        val penalty_str: String = getSubString(df33_data,90, 94)
        val penalty_amnt = penalty_str.toDouble() / 100
        val penaltyAmtFormat = emvUtils.df.format(penalty_amnt)

        //error string any
        val error_code: String = getSubString(df33_data,68, 70)
        val errorFormat: String = getError(error_code)

        //status
        val status: String = getSubString(df33_data,104, 106)
        val statusValue:Int = getTxnStatusNCMC(status)
        val statusFormat: String = getTxnStatus(status)

        //last transaction date time
        val date: String = getSubString(df33_data,84, 90)
        val finaltxndate = if (emvUtils.getHexatoDecimal(date).toInt() == 0) {
            "--"
        } else {
            emvUtils.calculateSecondsOfTxn(data_5F25 + "000000", emvUtils.getHexatoDecimal(date))
        }

        Log.e("error", errorFormat)
        Log.e("staus", statusFormat)
        Log.e("finaltxndate", finaltxndate)

        //last equipment id
        var lastequip: String = getSubString(df33_data, 72, 84)
        lastequip = getLastEquipmentId(lastequip.substring(6, 12))

        //last station name
        //TODO need to fix the station name
        var lastStation=lastequip.substring(6,9)
        lastStation= "NAGOLE"


        //history last transactions
        //history log1


//-----------------------------------------------------------------------------------------------------------------


        //history log1
        var datentime1: String = getSubString(df33_data, 118, 124)
        datentime1 =
            if (emvUtils.getHexatoDecimal(datentime1).toInt() === 0) "-- --" else emvUtils.calculateSecondsOfTxn(
                data_5F25 + "000000",
                emvUtils.getHexatoDecimal(datentime1)
            )

        val txn_amnt_str1: String =
            getSubString(df33_data, 128, 132)
        val txn_amnt1 = emvUtils.getHexatoDecimal(txn_amnt_str1) as Double / 10

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

        val transStationId1 = getSubString(df33_data,106, 118)


        //history log2
        var datentime2: String =
            getSubString(df33_data, 152, 158)
        datentime2 =
            if (emvUtils.getHexatoDecimal(datentime2).toInt() === 0) "-- --" else emvUtils.calculateSecondsOfTxn(
                data_5F25 + "000000",
                emvUtils.getHexatoDecimal(datentime2)
            )
        val txn_amnt_str2: String =
            getSubString(df33_data, 162, 166)
        val txn_amnt2 = emvUtils.getHexatoDecimal(txn_amnt_str2) as Double / 10

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

        val transStationId2 = getSubString(df33_data,106, 118)

        //history log3
        var datentime3: String =
            getSubString(df33_data, 140, 152)
        datentime3 =
            if (emvUtils.getHexatoDecimal(datentime3).toInt() === 0) "-- --" else emvUtils.calculateSecondsOfTxn(
                data_5F25 + "000000",
                emvUtils.getHexatoDecimal(datentime3)
            )
        val txn_amnt_str3: String =
            getSubString(df33_data, 196, 200)
        val txn_amnt3 = emvUtils.getHexatoDecimal(txn_amnt_str3) as Double / 10

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

        val transStationId3 = getSubString(df33_data,174, 186)

        //history log4
        var datentime4: String =
            getSubString(df33_data, 220, 226)
        datentime4 =
            if (emvUtils.getHexatoDecimal(datentime4).toInt() === 0) "-- --" else emvUtils.calculateSecondsOfTxn(
                data_5F25 + "000000",
                emvUtils.getHexatoDecimal(datentime4)
            )

        val txn_amnt_str4: String =
            getSubString(df33_data, 230, 234)
        val txn_amnt4 = emvUtils.getHexatoDecimal(txn_amnt_str4) as Double / 10
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

        val transStationId4 = getSubString(df33_data,208, 220)

        var station_str1 = transStationId1.substring(6, 9)
        station_str1 =
            if (station_str1 == "000") "--" else emvUtils.getStationIdFromStationDetailList(
                "0$station_str1"
            )

        var station_str2: String =transStationId2.substring(6, 9)
        station_str2 =
            if (station_str2 == "000") "--" else emvUtils.getStationIdFromStationDetailList(
                "0$station_str2"
            )
        var station_str3: String =transStationId3.substring(6, 9)
        station_str3 =
            if (station_str3 == "000") "--" else emvUtils.getStationIdFromStationDetailList(
                "0$station_str3"
            )
        var station_str4: String =transStationId4.substring(6, 9)
        station_str4 =
            if (station_str4 == "000") "--" else emvUtils.getStationIdFromStationDetailList(
                "0$station_str4"
            )


        //adding the transaction history
        val transactions = mutableListOf<TxnHistory>()
        transactions.add(TxnHistory(txnseqno1,datentime1.split(" ")[0],datentime1.split(" ")[1],emvUtils.df.format(txn_amnt1),getTxnStatus(transtype1),station_str1))
        transactions.add(TxnHistory(txnseqno2,datentime2.split(" ")[0],datentime2.split(" ")[1],emvUtils.df.format(txn_amnt2),getTxnStatus(transtype2),station_str2))
        transactions.add(TxnHistory(txnseqno3,datentime3.split(" ")[0],datentime3.split(" ")[1],emvUtils.df.format(txn_amnt3),getTxnStatus(transtype3),station_str3))
        transactions.add(TxnHistory(txnseqno4,datentime4.split(" ")[0],datentime4.split(" ")[1],emvUtils.df.format(txn_amnt4),getTxnStatus(transtype4),station_str4))


        //card effective date
        val cardEffectiveDate = readCardEffectiveDate(data_5F25.toInt())

        //CSA full data for display
        val csaDataDisplay = CSADataDisplay(cardbalance,cardBalanceFormat,penalty_amnt,penaltyAmtFormat,errorFormat,error_code.toInt(), finaltxndate,statusFormat,lastStation,statusValue,cardEffectiveDate,transactions)

        //CSA raw data for writing
        val csaRawData = setCsaRawData(df33_data)

        //CSA binary data
        val csaBinData = setCsaBinValue(df33_data)

        //set master data
        csaMasterData.csaDisplayData= csaDataDisplay
        csaMasterData.csaRawData= csaRawData
        csaMasterData.csaUpdatedRawData= csaRawData

        return csaMasterData
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
        csaRawData.serviceData= serviceData
        csaRawData.generalData= generalData
        csaRawData.validationData =validationData
        csaRawData.historyData=historyQueue
        csaRawData.rfu = tagDF33.substring(242,256)

        return csaRawData

    }
    
    
    private fun setCsaBinValue(tagDF33:String):CsaBin{

        //set general data
        val generalData = GeneralBin(
            versionNumber = tagDF33.substring(64,66).toInt(16).toByte(),
            languageInfoAndRfu = tagDF33.substring(66,68).toInt(16).toByte()
        )



        //set validation data
        val validationData = ValidationBin(
            errorCode = tagDF33.substring(68,70).toInt(16).toByte(),
            productType = tagDF33.substring(70,72).toInt(16).toByte(),
            acquirerID = tagDF33.substring(72,76).toInt(16).toByte(),
            operatorID = tagDF33.substring(76,80).toByteArray(),
            terminalID = tagDF33.substring(80,84).toByteArray(),
            trxDateTime =  tagDF33.substring(84,90).toByteArray(),
            fareAmt =  tagDF33.substring(90,94).toByteArray(),
            routeNo =  tagDF33.substring(94,98).toByteArray(),
            serviceProviderData =  tagDF33.substring(98,104).toByteArray(),
            trxStatusAndRfu =  tagDF33.substring(104,106).toInt(16).toByte(),
        )



        //set history data 1
        val historyData1 = HistoryBin(
            acquirerID = tagDF33.substring(106,110).toInt(16).toByte(),
            operatorID = tagDF33.substring(110,114).toByteArray(),
            terminalID = tagDF33.substring(114,118).toByteArray(),
            trxDateTime = tagDF33.substring(118,124).toByteArray(),
            trxSeqNum = tagDF33.substring(124,128).toByteArray(),
            trxAmt = tagDF33.substring(128,132).toByteArray(),
            cardBalance1 = tagDF33.substring(132,134).toInt(16).toByte(),
            cardBalance2 = tagDF33.substring(134,136).toInt(16).toByte(),
            cardBalance3 = tagDF33.substring(136,137).toInt(16).toByte(),
            trxStatus = tagDF33.substring(137,138).toInt(16).toByte(),
            rfu = tagDF33.substring(138,140).toInt(16).toByte()
        )


        //set history data 2
        val historyData2 = HistoryBin(
            acquirerID = tagDF33.substring(140,144).toInt(16).toByte(),
            operatorID = tagDF33.substring(144,148).toByteArray(),
            terminalID = tagDF33.substring(148,152).toByteArray(),
            trxDateTime = tagDF33.substring(152,158).toByteArray(),
            trxSeqNum = tagDF33.substring(158,162).toByteArray(),
            trxAmt = tagDF33.substring(162,166).toByteArray(),
            cardBalance1 = tagDF33.substring(166,168).toInt(16).toByte(),
            cardBalance2 = tagDF33.substring(168,170).toInt(16).toByte(),
            cardBalance3 = tagDF33.substring(170,171).toInt(16).toByte(),
            trxStatus = tagDF33.substring(171,172).toInt(16).toByte(),
            rfu = tagDF33.substring(172,174).toInt(16).toByte()

        )


        //set history data 3
        val historyData3 = HistoryBin(
            acquirerID = tagDF33.substring(174,178).toInt(16).toByte(),
            operatorID = tagDF33.substring(178,182).toByteArray(),
            terminalID = tagDF33.substring(182,186).toByteArray(),
            trxDateTime = tagDF33.substring(186,192).toByteArray(),
            trxSeqNum = tagDF33.substring(192,196).toByteArray(),
            trxAmt = tagDF33.substring(196,200).toByteArray(),
            cardBalance1 = tagDF33.substring(200,202).toInt(16).toByte(),
            cardBalance2 = tagDF33.substring(202,204).toInt(16).toByte(),
            cardBalance3 = tagDF33.substring(204,205).toInt(16).toByte(),
            trxStatus = tagDF33.substring(205,206).toInt(16).toByte(),
            rfu = tagDF33.substring(206,208).toInt(16).toByte()
        )


        //set history data 4
        val historyData4 = HistoryBin(
            acquirerID = tagDF33.substring(208,212).toInt(16).toByte(),
            operatorID = tagDF33.substring(212,216).toByteArray(),
            terminalID = tagDF33.substring(216,220).toByteArray(),
            trxDateTime = tagDF33.substring(220,226).toByteArray(),
            trxSeqNum = tagDF33.substring(226,230).toByteArray(),
            trxAmt = tagDF33.substring(230,234).toByteArray(),
            cardBalance1 = tagDF33.substring(234,236).toInt(16).toByte(),
            cardBalance2 = tagDF33.substring(236,238).toInt(16).toByte(),
            cardBalance3 = tagDF33.substring(238,239).toInt(16).toByte(),
            trxStatus = tagDF33.substring(239,240).toInt(16).toByte(),
            rfu = tagDF33.substring(240,242).toInt(16).toByte()
        )



        //create history queue
        val historyQueue = HistoryQueue<HistoryBin>(4)
        historyQueue.add(historyData1)
        historyQueue.add(historyData2)
        historyQueue.add(historyData3)
        historyQueue.add(historyData4)

        //set csa raw data to send back
        //csaRawData.serviceData= serviceData
        csaBinData.generalInfo= generalData
        csaBinData.validationData =validationData
        csaBinData.history=historyQueue
        csaBinData.rfu = tagDF33.substring(242,256).toByteArray()

        return csaBinData
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

}