package com.shellinfo.common.code.ipc

import abbasi.android.filelogger.FileLogger
import com.shellinfo.IRemoteService
import com.shellinfo.common.code.NetworkCall
import com.shellinfo.common.code.ShellInfoLibrary
import com.shellinfo.common.code.enums.BoTrxType
import com.shellinfo.common.code.enums.EquipmentType
import com.shellinfo.common.code.enums.ModeType
import com.shellinfo.common.code.enums.NcmcDataType
import com.shellinfo.common.code.enums.TicketType
import com.shellinfo.common.code.mqtt.topic_handler.modes.ModeManager
import com.shellinfo.common.code.pass.BasePassValidator
import com.shellinfo.common.data.local.data.emv_rupay.CSAMasterData
import com.shellinfo.common.data.local.data.emv_rupay.EntryDataCache
import com.shellinfo.common.data.local.data.emv_rupay.OSAMasterData
import com.shellinfo.common.data.local.data.emv_rupay.binary.csa_bin.HistoryBin
import com.shellinfo.common.data.local.data.emv_rupay.binary.osa_bin.HistoryBinOsa
import com.shellinfo.common.data.local.data.emv_rupay.binary.osa_bin.PassBin
import com.shellinfo.common.data.local.data.emv_rupay.display.csa_display.CSADataDisplay
import com.shellinfo.common.data.local.data.ipc.BF200Data
import com.shellinfo.common.data.local.db.entity.EntryTrxTable
import com.shellinfo.common.data.local.db.entity.ExitTrxTable
import com.shellinfo.common.data.local.db.entity.PassTable
import com.shellinfo.common.data.local.db.entity.PurchasePassTable
import com.shellinfo.common.data.local.db.entity.StationsTable
import com.shellinfo.common.data.local.db.entity.ZoneTable
import com.shellinfo.common.data.local.db.repository.DbRepository
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.data.remote.repository.ApiRepository
import com.shellinfo.common.data.remote.response.ApiResponse
import com.shellinfo.common.data.remote.response.model.entry_trx.EntryTrxRequest
import com.shellinfo.common.data.remote.response.model.entry_validation.EntryValidationRequest
import com.shellinfo.common.data.remote.response.model.entry_validation.EntryValidationResponse
import com.shellinfo.common.data.remote.response.model.exit_trx.ExitTrxRequest
import com.shellinfo.common.data.remote.response.model.gate_fare.GateFareRequest
import com.shellinfo.common.data.remote.response.model.gate_fare.GateFareResponse
import com.shellinfo.common.data.remote.response.model.purchase_pass.PurchasePassRequest
import com.shellinfo.common.data.shared.SharedDataManager
import com.shellinfo.common.utils.DateUtils
import com.shellinfo.common.utils.IPCConstants
import com.shellinfo.common.utils.IPCConstants.AMT_NOT_SUFFICIENT
import com.shellinfo.common.utils.IPCConstants.CARD_ALREADY_TAPPED
import com.shellinfo.common.utils.IPCConstants.ENTRY_NOT_FOUND
import com.shellinfo.common.utils.SpConstants.DOUBLE_TAP_THRESHOLD
import com.shellinfo.common.utils.IPCConstants.EXIT_NOT_FOUND
import com.shellinfo.common.utils.IPCConstants.FAILURE_ENTRY_VALIDATION
import com.shellinfo.common.utils.IPCConstants.FAILURE_FARE_API
import com.shellinfo.common.utils.IPCConstants.LANGUAGE_MASK
import com.shellinfo.common.utils.IPCConstants.LANG_ENGLISH
import com.shellinfo.common.utils.IPCConstants.MSG_ID_CONTINUES_READ_CARD_REQUEST_ACK
import com.shellinfo.common.utils.IPCConstants.MSG_ID_CREATE_PASS_DATA
import com.shellinfo.common.utils.IPCConstants.MSG_ID_ERROR_TRANSACTION
import com.shellinfo.common.utils.IPCConstants.MSG_ID_ONE_TIME_READ_CARD_REQUEST_ACK
import com.shellinfo.common.utils.IPCConstants.MSG_ID_REMOVE_PENALTY_DATA
import com.shellinfo.common.utils.IPCConstants.MSG_ID_TRANSIT_VALIDATION_FAIL_RUPAY_NCMC
import com.shellinfo.common.utils.IPCConstants.MSG_ID_TRANSIT_VALIDATION_RUPAY_NCMC
import com.shellinfo.common.utils.IPCConstants.NO_ERROR
import com.shellinfo.common.utils.IPCConstants.PROD_TYPE_SINGLE_JOURNEY
import com.shellinfo.common.utils.IPCConstants.READER_FUNCTIONALITY_DISABLED
import com.shellinfo.common.utils.SpConstants.TERMINAL_ID
import com.shellinfo.common.utils.IPCConstants.TIME_EXCEEDED
import com.shellinfo.common.utils.IPCConstants.TRX_STATUS_MASK
import com.shellinfo.common.utils.IPCConstants.TXN_STATUS_ENTRY
import com.shellinfo.common.utils.IPCConstants.TXN_STATUS_EXIT
import com.shellinfo.common.utils.IPCConstants.TXN_STATUS_ONE_TAP_TICKET
import com.shellinfo.common.utils.IPCConstants.TXN_STATUS_PENALTY
import com.shellinfo.common.utils.SpConstants
import com.shellinfo.common.utils.SpConstants.ACQUIRER_ID
import com.shellinfo.common.utils.SpConstants.BANK_MID
import com.shellinfo.common.utils.SpConstants.BANK_TID
import com.shellinfo.common.utils.SpConstants.DEVICE_TYPE
import com.shellinfo.common.utils.SpConstants.ENTRY_EXIT
import com.shellinfo.common.utils.SpConstants.ENTRY_EXIT_OVERRIDE
import com.shellinfo.common.utils.SpConstants.ENTRY_SIDE
import com.shellinfo.common.utils.SpConstants.EQUIPMENT_GROUP_ID
import com.shellinfo.common.utils.SpConstants.EQUIPMENT_ID
import com.shellinfo.common.utils.SpConstants.EXIT_SIDE
import com.shellinfo.common.utils.SpConstants.IS_TODAY_EVENT
import com.shellinfo.common.utils.SpConstants.IS_TODAY_HOLIDAY
import com.shellinfo.common.utils.SpConstants.LOGGING_ON_OFF
import com.shellinfo.common.utils.SpConstants.MINIMUM_BALANCE
import com.shellinfo.common.utils.SpConstants.OPERATOR_ID
import com.shellinfo.common.utils.SpConstants.OPERATOR_NAME_ID
import com.shellinfo.common.utils.SpConstants.READER_LOCATION
import com.shellinfo.common.utils.SpConstants.STATION_ID
import com.shellinfo.common.utils.SpConstants.TRANSACTION_SEQ_NUMBER
import com.shellinfo.common.utils.Utils
import com.shellinfo.common.utils.ipc.RupayUtils
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RupayDataHandler @Inject constructor(
    private val rupayUtils: RupayUtils,
    private val spUtils: SharedPreferenceUtil,
    private val sharedDataManager: SharedDataManager,
    private val apiRepository: ApiRepository,
    private val networkCall: NetworkCall,
    private val passHandler: PassHandler,
    private val passValidator: BasePassValidator,
    private val dbRepository: DbRepository,
    private val entryDataCache: EntryDataCache,
    private val modeManager: ModeManager
) {

    lateinit var communicationService: IRemoteService

    lateinit var csaMasterGlobal:CSAMasterData

    lateinit var osaMasterGlobal:OSAMasterData

    private val TAG = RupayDataHandler::class.java.simpleName

    private var lastTapTime: Long = 0

    private val ONE_MINUTE = 60

    //flag to check emergency mode at last station for validation API
    private var isCheckEmergencyMode = false

    // CSA,OSA last entry terminal id
    private var lastEntryTerminalId: ByteArray? = null

    // CSA,OSA last entry date time
    private var lastEntryDateTIme: String? = null

    //job to call the api
    private var job: Job? = null

    //Entry exit override
    private var isEntryExitOverrideEnable = false

    //Time override
    private var isTimeOverrideEnable = false

    //card effective date
    private var cardEffectiveDate:String? =null

    //penalty amount
    private var penaltyAmount = 0.0


    /**
     * Method to set the Remote Service
     */
    fun setRemoteService(communicationService: IRemoteService) {
        this.communicationService = communicationService
    }


    /**
     * Method to send the result to Application and to the Payment Application, Both or to only Application
     */
    fun handleError(errorCode:Int, errorMessage:String,sendToPaymentApp:Boolean?=true){

        //create csa master data error
        val csaMasterData = CSAMasterData()

        //set error
        csaMasterData.rupayMessage!!.returnCode= errorCode
        csaMasterData.rupayMessage!!.returnMessage= errorMessage
        csaMasterData.rupayMessage!!.isSuccess =false

        //post value to live data to application
        sharedDataManager.sendCsaData(csaMasterData)

        //if want to send the data to payment app
        if(sendToPaymentApp == true) {
            //send back error to payment app
            communicationService.sendData(MSG_ID_ERROR_TRANSACTION, "MSG_ID_ERROR_TRANSACTION")
        }
    }


    /**
     * Method to send the result to Application and to the Payment Application, Both or to only Application
     */
    fun handleErrorOSA(errorCode:Int, errorMessage:String,sendToPaymentApp:Boolean?=true){

        //create csa master data error
        val osaMasterData = OSAMasterData()

        //set error
        osaMasterData.rupayMessage!!.returnCode= errorCode
        osaMasterData.rupayMessage!!.returnMessage= errorMessage
        osaMasterData.rupayMessage!!.isSuccess =false

        //post value to live data to application
        sharedDataManager.sendOsaData(osaMasterData)

        //if want to send the data to payment app
        if(sendToPaymentApp == true) {
            //send back error to payment app
            communicationService.sendData(MSG_ID_ERROR_TRANSACTION, "MSG_ID_ERROR_TRANSACTION")
        }
    }


    /**
     * Method to send the result to Application not to the payment application
     */
    fun sendMessageToApp(returnCode: Int?, returnMessage: String?){

        //create csa master data error
        val csaMasterData = CSAMasterData()

        //set error
        csaMasterData.rupayMessage!!.returnCode= returnCode!!
        csaMasterData.rupayMessage!!.returnMessage= returnMessage!!

        //post value to live data to application
        sharedDataManager.sendCsaData(csaMasterData)

    }

    /**
     * Method to remove the penalty from the CSA
     */
    fun removePenalty(bF200Data: BF200Data){

        //parsing csa master data
        val csaMasterData = rupayUtils.readCSAData(bF200Data)

        //get csa raw data
        val csaRawData = csaMasterData.csaBinData

        //calculate card effective date
        cardEffectiveDate= rupayUtils.readCardEffectiveDate(rupayUtils.hexStringToByteArray(bF200Data.b.`5F25`!!))

        //check error code
        if(csaRawData?.validationData?.errorCode?.toInt() != NO_ERROR) {


            //update error code to no error
            csaMasterData.csaUpdatedBinData!!.validationData.errorCode = NO_ERROR.toByte()

            //error code
            csaMasterData.rupayMessage?.returnCode = NO_ERROR
            csaMasterData.rupayMessage?.returnMessage = "NO_ERROR"


            //update transaction date time
            val trxTimeFromEpoch: Long = System.currentTimeMillis() / 1000
            val trxTimeFromCardEffDate = rupayUtils.calculateTrxTimeFromCardEffectiveDate(
                cardEffectiveDate!!,
                trxTimeFromEpoch
            )

            Utils.numToBin(
                csaMasterData.csaUpdatedBinData?.validationData?.trxDateTime!!,
                trxTimeFromCardEffDate.toLong(),
                3
            )

            //terminal info
            val acquirerID = spUtils.getPreference(ACQUIRER_ID, "04")
            val operatorID = spUtils.getPreference(OPERATOR_NAME_ID, "6014")
            var terminalID = spUtils.getPreference(TERMINAL_ID, "401101")

            val acquirerIDBytes = rupayUtils.hexToByte(acquirerID)!!
            val operatorIDBytes = rupayUtils.hexToByteArray(operatorID)!!
            val terminalIDBytes = rupayUtils.hexToByteArray(terminalID)!!


            // if penalty amount is more then 0 then have to create an entry in history
            if (penaltyAmount > 0) {
                updateHistory(penaltyAmount, csaMasterData,true,TXN_STATUS_PENALTY)
            }

            //update history as validation data no need to update
            csaMasterData.csaUpdatedBinData!!.history.getLast()!!.terminalID = terminalIDBytes
            csaMasterData.csaUpdatedBinData!!.history.getLast()!!.acquirerID = acquirerIDBytes
            csaMasterData.csaUpdatedBinData!!.history.getLast()!!.operatorID = operatorIDBytes


            //convert the updated csa bin to byte array
            val csaSent =  rupayUtils.csaToByteArray(csaMasterData.csaUpdatedBinData!!)


            //add the header and footer for the update
            val startIndex= bF200Data?.serviceDataIndex!!
            val endIndex= bF200Data?.serviceDataIndex!!+95
            var updatedDataWithHeaderFooter=bF200Data.serviceRelatedData
            for( i in startIndex..endIndex){
                updatedDataWithHeaderFooter!!.set(i, csaSent[i-startIndex])
            }

            //penalty amount needs to deduct from the global wallet
            if(penaltyAmount>0) {

                //first remove last two bytes
                updatedDataWithHeaderFooter!!.removeLast()
                updatedDataWithHeaderFooter!!.removeLast()

                // Convert Amount to tag and append to the service data
                val bcdAmount = ByteArray(6)
                rupayUtils.convertAmountToBCD(penaltyAmount.toLong(), bcdAmount)

                //Add tag 9F02
                updatedDataWithHeaderFooter.add(0x9F.toByte())
                updatedDataWithHeaderFooter.add(0x02.toByte())

                // Append Length 06
                updatedDataWithHeaderFooter.add(0x06.toByte())

                // Append Data (Fare Value)
                for (j in bcdAmount.indices) {
                    updatedDataWithHeaderFooter.add(bcdAmount[j])
                }

                // Add two bytes status at the end of the response(which we removed earlier)
                updatedDataWithHeaderFooter.add(0x30.toByte())
                updatedDataWithHeaderFooter.add(0x30.toByte())

                // Change total Length
                var totalLen =
                    updatedDataWithHeaderFooter[1].toInt() or (updatedDataWithHeaderFooter[0].toInt() shl 8)
                totalLen += 9  // 2 bytes status + 9 byte 9F02

                // Construct 2 byte header lengths
                updatedDataWithHeaderFooter[0] = ((totalLen shr 8) and 0xFF).toByte()
                updatedDataWithHeaderFooter[1] = (totalLen and 0xFF).toByte()
            }


            //send back the message
            communicationService.sendData(MSG_ID_REMOVE_PENALTY_DATA,updatedDataWithHeaderFooter!!.toByteArray().toHexString())

        }
    }


    /**
     * Handling Rupay NCMC card CSA data, Sent by the Payment Application
     */
    fun handleRupayCardCSAData(bF200Data: BF200Data) {

        //parsing csa master data
        val csaMasterData = rupayUtils.readCSAData(bF200Data)
        csaMasterGlobal= csaMasterData


        FileLogger.e(TAG,">>>> CSA READ DATA: ${networkCall.toJson(csaMasterData.csaDisplayData!!,
            CSADataDisplay::class)}")


        //check device type to handle data request
        val deviceType= spUtils.getPreference(DEVICE_TYPE,"")

        if(deviceType.isNotEmpty()){

            //get enum type from device type
            val equipmentType= EquipmentType.fromEquipment(deviceType)

            when(equipmentType){

                EquipmentType.TVM,
                EquipmentType.TOM,
                EquipmentType.TR->{


                    //TODO check for only recharge (if available then send data to update the balance)
                    //TODO for now we only check the balance not updating it(once API available need to implement)
                    val isOnlineRechargeDone=false

                    if(!isOnlineRechargeDone){

                        //create no error rupay
                        csaMasterData.rupayMessage?.returnCode = NO_ERROR
                        csaMasterData.rupayMessage?.returnMessage = "NO_ERROR"

                        //post value to live data
                        sharedDataManager.sendCsaData(csaMasterData)

                        //check if equipment type is TR then send different message id to make sure card read service not stop
                        if(equipmentType == EquipmentType.TR){

                            //send back to reader with no update and card reading service continues
                            communicationService.sendData(MSG_ID_CONTINUES_READ_CARD_REQUEST_ACK,"MSG_ID_CONTINUES_READ_CARD_REQUEST_ACK")

                        }else{

                            //send back to reader with no update but card read service stop
                            communicationService.sendData(MSG_ID_ONE_TIME_READ_CARD_REQUEST_ACK,"MSG_ID_ONE_TIME_READ_CARD_REQUEST_ACK")
                        }


                        return

                    }else {

                        //TODO check for recharge data and update the on CSA OSA to write back
                    }



                }

                else -> {}
            }

        }else{

            FileLogger.e(TAG,">>>> DEVICE TYPE NOT SET")
            return

        }


        //calculate card effective date
        cardEffectiveDate= rupayUtils.readCardEffectiveDate(rupayUtils.hexStringToByteArray(bF200Data.b.`5F25`!!))

        //set bf200 data in master data
        csaMasterData.bf200Data = bF200Data


        //check reader location
        when (spUtils.getPreference(READER_LOCATION, "ENTRY")) {

            ENTRY_SIDE -> {

                FileLogger.e(TAG, ">>>>ENTRY SIDE CODE EXECUTED")

                processEntryCSA(csaMasterData)
            }

            EXIT_SIDE -> {

                FileLogger.e(TAG, ">>>>EXIT SIDE CODE EXECUTED")
                processExitCSA(csaMasterData)
            }

            ENTRY_EXIT -> {

            }
        }
    }

    /**
     * Handling Rupay NCMC card OSA data, Sent by the Payment Application
     */
    fun handleRupayCardOSAData(bF200Data: BF200Data){

        //get osa master data
        var osaMasterData= rupayUtils.readOSAData(bF200Data)
        osaMasterGlobal=osaMasterData
        rupayUtils.setOsaGlobalData(osaMasterGlobal)

        //check device type to handle data request
        val deviceType= spUtils.getPreference(DEVICE_TYPE,"")

        if(deviceType.isNotEmpty()) {

            //get enum type from device type
            val equipmentType = EquipmentType.fromEquipment(deviceType)

            when (equipmentType) {

                EquipmentType.TOM->{

                    if(ShellInfoLibrary.isForOsaCreate){

                        //reset the flag
                        ShellInfoLibrary.isForOsaCreate=false

                    }else if(ShellInfoLibrary.isForPenalty){

                        //reset the flag
                        ShellInfoLibrary.isForPenalty=false

                    }else if(ShellInfoLibrary.isForOsaRead){

                        //reset the flag
                        ShellInfoLibrary.isForOsaRead=false

                        //create no error rupay
                        osaMasterData.rupayMessage?.returnCode = NO_ERROR
                        osaMasterData.rupayMessage?.returnMessage = "NO_ERROR"

                        //post value to live data
                        sharedDataManager.sendOsaData(osaMasterData)

                        //send back to reader with no update but card read service stop
                        communicationService.sendData(MSG_ID_ONE_TIME_READ_CARD_REQUEST_ACK,"MSG_ID_ONE_TIME_READ_CARD_REQUEST_ACK")

                    }else if(ShellInfoLibrary.isForPassCreate){

                        //reset the flag
                        ShellInfoLibrary.isForPassCreate=false


                        if (ShellInfoLibrary.isForOsaDelete){
                            ShellInfoLibrary.isForOsaDelete=false

                            //delete the passes
                            osaMasterData = passHandler.deletePasses(osaMasterData)
                        }else{
                            //get the pass data
                            osaMasterData = passHandler.createPass(osaMasterData)
                        }


                        if(osaMasterData.rupayMessage!!.isSuccess){

                            //make service active
                            osaMasterData.osaUpdatedBinData!!.generalInfo.setServiceStatus(true)

                            //convert the updated csa bin to byte array
                            val osaSent =  rupayUtils.osaBinToByteArray(osaMasterData.osaUpdatedBinData!!)


                            //add the header and footer for the update
                            val startIndex= osaMasterData.bf200Data?.serviceDataIndex!!
                            val endIndex= osaMasterData.bf200Data?.serviceDataIndex!!+95
                            var updatedDataWithHeaderFooter=osaMasterData.bf200Data?.serviceRelatedData
                            for( i in startIndex..endIndex){
                                updatedDataWithHeaderFooter!!.set(i, osaSent[i-startIndex])
                            }


                            //send back the message
                            communicationService.sendData(MSG_ID_CREATE_PASS_DATA,updatedDataWithHeaderFooter!!.toByteArray().toHexString())

                        }else{

                            //handle pass creation error
                            handleErrorOSA(osaMasterData.rupayMessage!!.returnCode,osaMasterData.rupayMessage!!.returnMessage)

                        }



                    }else if(ShellInfoLibrary.isForDataDelete){
                        ShellInfoLibrary.isForDataDelete=false

                        val eventResponse = byteArrayOf(0x0, 0x2, 0x30, 0x30)
                        //send back the message
                        communicationService.sendData(MSG_ID_CREATE_PASS_DATA,eventResponse.toHexString())
                    }

                }

                EquipmentType.TVM ->{
                     if(ShellInfoLibrary.isForOsaRead){

                        //reset the flag
                        ShellInfoLibrary.isForOsaRead=false

                        //create no error rupay
                        osaMasterData.rupayMessage?.returnCode = NO_ERROR
                        osaMasterData.rupayMessage?.returnMessage = "NO_ERROR"

                        //post value to live data
                        sharedDataManager.sendOsaData(osaMasterData)

                        //send back to reader with no update but card read service stop
                        communicationService.sendData(MSG_ID_ONE_TIME_READ_CARD_REQUEST_ACK,"MSG_ID_ONE_TIME_READ_CARD_REQUEST_ACK")

                    }
                }
                EquipmentType.VALIDATOR->{

                    //calculate card effective date
                    cardEffectiveDate= rupayUtils.readCardEffectiveDate(rupayUtils.hexStringToByteArray(bF200Data.b.`5F25`!!))

                    //set bf200 data in master data
                    osaMasterData.bf200Data = bF200Data

                    //check reader location
                    when (spUtils.getPreference(READER_LOCATION, "EXIT")) {

                        ENTRY_SIDE -> {

                            FileLogger.e(TAG, ">>>>ENTRY SIDE OSA CODE EXECUTED")

                            processEntryOSA(osaMasterData)
                        }

                        EXIT_SIDE -> {

                            FileLogger.e(TAG, ">>>>EXIT SIDE OSA CODE EXECUTED")
                            processExitOSA(osaMasterData)
                        }

                        ENTRY_EXIT -> {

                        }
                    }

                }

                else ->{
                    FileLogger.e(TAG,">>>> WRONG DEVICE TYPE TO HANDLE")
                    return
                }
            }
        }else{
            FileLogger.e(TAG,">>>> DEVICE TYPE NOT SET")
            return
        }




    }


    fun deleteCSAData(){

        val eventResponse = byteArrayOf(0x0, 0x2, 0x30, 0x30)
        //send back the message
        communicationService.sendData(MSG_ID_CREATE_PASS_DATA,eventResponse.toHexString())
    }


    /**
     * processEntryCSA : This function will validate the CSA data to permit Entry at Gate
     */
    private fun processEntryCSA(csaMasterData: CSAMasterData) {

        //double tap threshold
        val DOUBLE_TAP_THRESHOLD = spUtils.getPreference(DOUBLE_TAP_THRESHOLD, 3) // 3 seconds
        isEntryExitOverrideEnable = modeManager.getCurrentMode() == ModeType.SEQ_OVERRIDE_MODE


        //get csa raw data
        val csaRawData = csaMasterData.csaBinData


        var updatedCsaRawData = csaMasterData.csaUpdatedBinData

        //get display data
        val csaDataDisplay = csaMasterData.csaDisplayData

        //check general data for version
        if (csaRawData!!.generalInfo.versionNumber != IPCConstants.VERSION_NUMBER) {
            FileLogger.d(TAG, "Card CSA General Info Version: ${csaRawData.generalInfo.versionNumber}")
        }

        //check general data for language
        if (!isLanguageEnglish(csaRawData.generalInfo.languageInfo)) {
            FileLogger.d(TAG, "Card CSA General Info Language: ${csaRawData.generalInfo.languageInfo}")
        }


        FileLogger.e(TAG,">>>>VALIDATION ERROR CODE: ${csaRawData.validationData.errorCode}")


        //check for existing error
        if (csaRawData.validationData.errorCode!!.toInt() != NO_ERROR) {

            FileLogger.e(TAG, "Existing Error : ${csaDataDisplay?.errorCode}")


            //return error to payment app and UI
            handleError(
                csaRawData.validationData.errorCode!!.toInt(),
                rupayUtils.getError(csaRawData.validationData.errorCode!!.toString())
            )

            return
        }

        val lastTrxStaus = extractTrxStatus(csaRawData.validationData.trxStatusAndRfu)

        //check last transaction is ENTRY
        if (lastTrxStaus == TXN_STATUS_ENTRY) {

            var trxTimeFromCardEffDate: Long = 0
            var entryTime: Long
            val currentTime: Long =
                System.currentTimeMillis() / 1000 // Get current time in seconds from epoch

            // CSA.validationData.trxDateTime is assumed to be a byte array of size 3
            val trxDateTime: ByteArray = csaRawData.validationData.trxDateTime

            // Calculate last transaction time
            for (i in 0 until 3) {
                trxTimeFromCardEffDate = (trxTimeFromCardEffDate * 16 * 16) +
                        (((trxDateTime[i].toInt() and 0xF0) shr 4) * 16) + (trxDateTime[i].toInt() and 0x0F)
            }

            val cardEffectiveDate: Long = rupayUtils.calculateTrxTimeFromCardEffectiveDate(cardEffectiveDate!!,currentTime)


            // Calculate entryTime based on trxTimeFromCardEffDate and cardEffectiveDate
            entryTime = 60 * calculateTrxTimeFromEpoch(cardEffectiveDate, trxTimeFromCardEffDate)

            println("Entry Time: $entryTime")

            if (spUtils.getPreference(LOGGING_ON_OFF, false)) {
                FileLogger.d(
                    TAG,
                    "${currentTime} - ${entryTime} = ${currentTime - entryTime} ?? $ONE_MINUTE"
                )

            }


            if ((currentTime - entryTime) < ONE_MINUTE) {
                // Ignore the tap as it happened within the double-tap threshold

                //set error code and error message
                //return error to payment app and UI
                handleError(
                    CARD_ALREADY_TAPPED,
                    rupayUtils.getError("CARD_ALREADY_TAPPED")
                )

                return
            }


            //check entry exit override is on then , enable flag for emergency on last station
            if (!isEntryExitOverrideEnable) {

                //last entry date time from card validation data
                lastEntryDateTIme = DateUtils.getTimeInYMDHMS(entryTime)

                //last entry terminal id
                lastEntryTerminalId = csaRawData.validationData.terminalID

                //set check emergency mode flag to true (we want to check if on last station emergency mode was activate while validation)
                isCheckEmergencyMode = true

            }

        }


        //API call entry validation API
        val entryValidationRequest = EntryValidationRequest()

        //bin number from tag 5A
        //TODO HAVE TO CHECK BIN NUMBER
        val binNumber = csaMasterData.bf200Data?.b?.`5A`?.let { it.slice(0..5) }
        entryValidationRequest.binNumber = binNumber

        //check if emergency on last station needs to be checked
        if (isCheckEmergencyMode) {

            //get hex value from terminal id
            //val terminalId = Utils.bin2hex(lastEntryTerminalId!!, 3)

            entryValidationRequest.lastStationId = csaDataDisplay!!.lastStationId
            entryValidationRequest.lastTransactionDateTime = lastEntryDateTIme

        }

        //set current equipmentId and equipmentGroupId
        entryValidationRequest.equipmentId = spUtils.getPreference(EQUIPMENT_ID, "4001")
        entryValidationRequest.equipmentGroupId = spUtils.getPreference(EQUIPMENT_GROUP_ID, "4")

        FileLogger.e(TAG,">>>> ENTRY VALIDATION DATA: ${networkCall.toJson(entryValidationRequest,EntryValidationRequest::class)}")

        runBlocking {

            val result = validateEntry(entryValidationRequest)

            if(result.isSuccess){

                val response= result.getOrNull()

                if(response!=null){
                    handleEntryValidationResponse(response,csaMasterData)
                }else{

                    //return error to payment app and UI
                    handleError(
                        FAILURE_ENTRY_VALIDATION,
                        rupayUtils.getError("FAILURE_ENTRY_VALIDATION")
                    )

                    return@runBlocking
                }

            }else{
                return@runBlocking
            }
        }

    }


    /**
     * processEntryOSA : This function will validate the OSA data to permit Entry at Gate
     */
    private fun processEntryOSA(osaMasterData: OSAMasterData) {

        //double tap threshold
        val DOUBLE_TAP_THRESHOLD = spUtils.getPreference(DOUBLE_TAP_THRESHOLD, 3) // 3 seconds
        isEntryExitOverrideEnable = modeManager.getCurrentMode() == ModeType.SEQ_OVERRIDE_MODE


        //get csa raw data
        val osaRawData = osaMasterData.osaBinData

        //check for osa service active or not
        if(!osaMasterData.osaBinData!!.generalInfo.getServiceStatus()){

            //log for
            FileLogger.e(TAG,"OSA Service Inactive go for CSA")

            //if service is not active go for csa deduction
            abortOsaTransaction(osaMasterData)

            return
        }

        //check if all passes expired then make service inactive
        if(passValidator.isAllPassExpired(osaMasterData.osaBinData!!.passes)){

            //update service flag to false
            osaMasterData.osaUpdatedBinData!!.generalInfo.setServiceStatus(false)

            //if service is not active go for csa deduction
            abortOsaTransaction(osaMasterData)

            return
        }


        //get display data
        val osaDataDisplay = osaMasterData.osaDisplayData

        //check general data for version
        if (osaRawData!!.generalInfo.versionNumber != IPCConstants.VERSION_NUMBER) {
            FileLogger.d(TAG, "Card CSA General Info Version: ${osaRawData.generalInfo.versionNumber}")
        }

        //check general data for language
        if (!isLanguageEnglish(osaRawData.generalInfo.languageInfo)) {
            FileLogger.d(TAG, "Card CSA General Info Language: ${osaRawData.generalInfo.languageInfo}")
        }


        FileLogger.e(TAG,">>>>VALIDATION ERROR CODE: ${osaRawData!!.validationData.errorCode}")


        //check for existing error
        if (osaRawData!!.validationData.errorCode!!.toInt() != NO_ERROR) {

            FileLogger.e(TAG, "Existing Error : ${osaDataDisplay?.errorCode}")


            //return error to payment app and UI
            handleErrorOSA(
                osaRawData.validationData.errorCode!!.toInt(),
                rupayUtils.getError(osaRawData.validationData.errorCode!!.toString())
            )

            return
        }

        //get last trx status
        val lastTransactionStatus = extractTrxStatus(osaRawData.validationData.trxStatusAndRfu)

        //check last transaction is ENTRY
        if (lastTransactionStatus == TXN_STATUS_ENTRY) {

            var trxTimeFromCardEffDate: Long = 0
            var entryTime: Long
            val currentTime: Long =
                System.currentTimeMillis() / 1000 // Get current time in seconds from epoch

            // CSA.validationData.trxDateTime is assumed to be a byte array of size 3
            val trxDateTime: ByteArray = osaRawData.validationData.trxDateTime

            // Calculate last transaction time
            for (i in 0 until 3) {
                trxTimeFromCardEffDate = (trxTimeFromCardEffDate * 16 * 16) +
                        (((trxDateTime[i].toInt() and 0xF0) shr 4) * 16) + (trxDateTime[i].toInt() and 0x0F)
            }

            val cardEffectiveDate: Long = rupayUtils.calculateTrxTimeFromCardEffectiveDate(cardEffectiveDate!!,currentTime)


            // Calculate entryTime based on trxTimeFromCardEffDate and cardEffectiveDate
            entryTime = 60 * calculateTrxTimeFromEpoch(cardEffectiveDate, trxTimeFromCardEffDate)

            println("Entry Time: $entryTime")

            if (spUtils.getPreference(LOGGING_ON_OFF, false)) {
                FileLogger.d(
                    TAG,
                    "${currentTime} - ${entryTime} = ${currentTime - entryTime} ?? $ONE_MINUTE"
                )

            }


            if ((currentTime - entryTime) < ONE_MINUTE) {
                // Ignore the tap as it happened within the double-tap threshold

                //set error code and error message
                //return error to payment app and UI
                handleErrorOSA(
                    CARD_ALREADY_TAPPED,
                    rupayUtils.getError("CARD_ALREADY_TAPPED")
                )

                return
            }


            //check entry exit override is on then , enable flag for emergency on last station
            if (!isEntryExitOverrideEnable) {

                //last entry date time from card validation data
                lastEntryDateTIme = DateUtils.getTimeInYMDHMS(entryTime)

                //last entry terminal id
                lastEntryTerminalId = osaRawData.history.get(1).terminalID

                //set check emergency mode flag to true (we want to check if on last station emergency mode was activate while validation)
                isCheckEmergencyMode = true

            }

        }


        //API call entry validation API
        val entryValidationRequest = EntryValidationRequest()

        //bin number from tag 5A
        //TODO HAVE TO CHECK BIN NUMBER
        val binNumber = osaMasterData.bf200Data?.b?.`5A`?.let { it.slice(0..5) }
        entryValidationRequest.binNumber = binNumber

        //check if emergency on last station needs to be checked
        if (isCheckEmergencyMode) {

            //get hex value from terminal id
            entryValidationRequest.lastStationId =osaDataDisplay!!.lastStationId
            entryValidationRequest.lastTransactionDateTime = lastEntryDateTIme

        }

        //set current equipmentId and equipmentGroupId
        entryValidationRequest.equipmentId = spUtils.getPreference(EQUIPMENT_ID, "4001")
        entryValidationRequest.equipmentGroupId = spUtils.getPreference(EQUIPMENT_GROUP_ID, "4")

        FileLogger.e(TAG,">>>> ENTRY VALIDATION DATA: ${networkCall.toJson(entryValidationRequest,EntryValidationRequest::class)}")

        runBlocking {

            val result = validateEntry(entryValidationRequest)

            if(result.isSuccess){

                val response= result.getOrNull()

                if(response!=null){
                    handleEntryValidationResponseOSA(response,osaMasterData)
                }else{

                    //return error to payment app and UI
                    handleErrorOSA(
                        FAILURE_ENTRY_VALIDATION,
                        rupayUtils.getError("FAILURE_ENTRY_VALIDATION")
                    )

                    return@runBlocking
                }

            }else{

                //return error to payment app and UI
                handleErrorOSA(
                    FAILURE_ENTRY_VALIDATION,
                    rupayUtils.getError("FAILURE_ENTRY_VALIDATION")
                )

                return@runBlocking
            }
        }

    }

    /**
     * Method to get the Validation data transaction status first four bits
     */
    fun extractTrxStatus(trxStatusAndRfu: Byte): Int {
        return (trxStatusAndRfu.toInt() and TRX_STATUS_MASK) shr 4
    }


    /**
     * Method to handle the Entry Validation Api Response
     */
    private fun handleEntryValidationResponse(
        entryValidationResponse: EntryValidationResponse,
        csaMasterData: CSAMasterData
    ) {

        //get api error code, replace the 0x prefix
        val apiErrorCode = entryValidationResponse.returnCode.replace("0x","")

        //if logging ON then print Entry Validation Error Code
        if (spUtils.getPreference(LOGGING_ON_OFF, false)) {
            FileLogger.e(TAG, "Entry Validation : errCode :${entryValidationResponse.returnCode}")
        }

        //if api error code is not NO_ERROR then check for the error code
        if (apiErrorCode.toInt() != NO_ERROR) {

            //if error code is Exit Not Found then update the csa validation data txn status
            if (apiErrorCode.toInt() == EXIT_NOT_FOUND) {

                //return error to payment app, not to the UI
                handleError(
                    EXIT_NOT_FOUND,
                    rupayUtils.getError("EXIT_NOT_FOUND_CSA"),
                    false
                )

                csaMasterData.csaUpdatedBinData?.validationData?.trxStatusAndRfu = TXN_STATUS_ENTRY.toByte()

                //write back the error code to csa data
                errorWriteCSA(EXIT_NOT_FOUND,"EXIT_NOT_FOUND_CSA",csaMasterData)

                return
            }
        }

        //last transaction status
        val lastTrxStatus =
            extractTrxStatus(csaMasterData.csaUpdatedBinData!!.validationData.trxStatusAndRfu)

        if (((lastTrxStatus == TXN_STATUS_EXIT) || (lastTrxStatus == TXN_STATUS_ONE_TAP_TICKET)
                    || (lastTrxStatus == TXN_STATUS_PENALTY) || isEntryExitOverrideEnable) ||
            (isCheckEmergencyMode && apiErrorCode.toInt() == NO_ERROR)
        ) {


            //minimum required balance
            val minBalance = spUtils.getPreference(MINIMUM_BALANCE, 0)

            //card balance
            val cardBalance = csaMasterData.csaDisplayData?.cardBalance

            //check sufficient balance present
            if (!checkBalanceSufficient(minBalance.toDouble(), cardBalance!!)) {

                //return error to payment app, not to the UI
                handleError(
                    AMT_NOT_SUFFICIENT,
                    rupayUtils.getError("AMT_NOT_SUFFICIENT"),
                    false
                )

                //write back the error code to csa data
                errorWriteCSA(AMT_NOT_SUFFICIENT,"AMT_NOT_SUFFICIENT",csaMasterData)

               return
            }

            //complete process CSA
            completeProcessCSA(0.0, TXN_STATUS_ENTRY, csaMasterData)


        } else {
            //return error to payment app and UI
            handleError(
                EXIT_NOT_FOUND,
                rupayUtils.getError("EXIT_NOT_FOUND_CSA")
            )

            //write back the error on the card
            errorWriteCSA(EXIT_NOT_FOUND,"EXIT_NOT_FOUND_CSA",csaMasterData)

            return
        }

    }


    /**
     * Method to handle the Entry Validation Api Response
     */
    private fun handleEntryValidationResponseOSA(
        entryValidationResponse: EntryValidationResponse,
        osaMasterData: OSAMasterData
    ) {

        //get api error code, replace the 0x prefix
        val apiErrorCode = entryValidationResponse.returnCode.replace("0x","")

        //if logging ON then print Entry Validation Error Code
        if (spUtils.getPreference(LOGGING_ON_OFF, false)) {
            FileLogger.e(TAG, "Entry Validation : errCode :${entryValidationResponse.returnCode}")
        }

        //if api error code is not NO_ERROR then check for the error code
        if (apiErrorCode.toInt() != NO_ERROR) {

            //if error code is Exit Not Found then update the osa validation data txn status
            if (apiErrorCode.toInt() == EXIT_NOT_FOUND) {

                //return error to payment app, not to the UI
                handleErrorOSA(
                    EXIT_NOT_FOUND,
                    rupayUtils.getError("EXIT_NOT_FOUND_CSA"),
                    false
                )

                osaMasterData.osaUpdatedBinData?.validationData?.trxStatusAndRfu = TXN_STATUS_ENTRY.toByte()

                //write back the error code to csa data
                errorWriteOSA(EXIT_NOT_FOUND,"EXIT_NOT_FOUND_CSA",osaMasterData)

                return
            }
        }

        //last transaction status
        val lastTrxStatus =
            extractTrxStatus(osaMasterData.osaUpdatedBinData!!.validationData.trxStatusAndRfu)

        if (((lastTrxStatus == TXN_STATUS_EXIT) || (lastTrxStatus == TXN_STATUS_ONE_TAP_TICKET)
                    || (lastTrxStatus == TXN_STATUS_PENALTY) || isEntryExitOverrideEnable) ||
            (isCheckEmergencyMode && apiErrorCode.toInt() == NO_ERROR)
        ) {


            //complete process CSA
            completeProcessOSA( TXN_STATUS_ENTRY, osaMasterData)


        } else {



            //return error to payment app and UI
            handleErrorOSA(
                EXIT_NOT_FOUND,
                rupayUtils.getError("EXIT_NOT_FOUND_CSA")
            )

            //write back the error code to csa data
            errorWriteOSA(EXIT_NOT_FOUND,"EXIT_NOT_FOUND_CSA",osaMasterData)

            return
        }

    }

    // Function to check if the language is English
    fun isLanguageEnglish(languageInfo: Byte): Boolean {
        return (languageInfo.toInt() and LANGUAGE_MASK) == LANG_ENGLISH
    }

    /**
     * Method to check the sufficient balance present or not for entry
     */
    private fun checkBalanceSufficient(requiredBalance: Double, cardBalance: Double): Boolean {

        return cardBalance >= requiredBalance
    }

    /**
     * Method to process exit gate csa data
     */
    private fun processExitCSA(csaMasterData: CSAMasterData) {


        //double tap threshold
        val DOUBLE_TAP_THRESHOLD = spUtils.getPreference(DOUBLE_TAP_THRESHOLD, 3) // 3 seconds
        isEntryExitOverrideEnable = spUtils.getPreference(ENTRY_EXIT_OVERRIDE, false)

        //get csa raw data
        val csaRawData = csaMasterData.csaBinData


        var updatedCsaRawData = csaMasterData.csaUpdatedBinData

        //get display data
        val csaDataDisplay = csaMasterData.csaDisplayData

        //check general data for version
        if (csaRawData!!.generalInfo.versionNumber != IPCConstants.VERSION_NUMBER) {
            FileLogger.d(
                TAG,
                "Card CSA General Info Version: ${csaRawData.generalInfo.versionNumber}"
            )
        }

        //check general data for language
        if (!isLanguageEnglish(csaRawData.generalInfo.languageInfo)) {
            FileLogger.d(
                TAG,
                "Card CSA General Info Language: ${csaRawData.generalInfo.languageInfo}"
            )
        }


        FileLogger.e(TAG,">>>>VALIDATION ERROR CODE: ${csaRawData.validationData.errorCode}")


        //check for existing error
        if (csaRawData.validationData.errorCode!!.toInt() != NO_ERROR) {

            FileLogger.e(TAG, "Existing Error : ${csaDataDisplay?.errorCode}")

            //return error to payment app and UI
            handleError(
                csaRawData.validationData.errorCode!!.toInt(),
                rupayUtils.getError(csaRawData.validationData.errorCode!!.toString())
            )

            return
        }


        //code to calculate entry time from card effective date
        var trxTimeFromCardEffDate: Long = 0
        var entryTime: Long=0
        var lastTrxTime:Long=0
        val currentTime: Long = System.currentTimeMillis() / 1000 // Get current time in seconds from epoch

        // CSA.validationData.trxDateTime is assumed to be a byte array of size 3
        val trxDateTime: ByteArray = csaRawData.validationData.trxDateTime

        // Calculate last transaction time
        for (i in 0 until 3) {
            trxTimeFromCardEffDate = (trxTimeFromCardEffDate * 16 * 16) +
                    (((trxDateTime[i].toInt() and 0xF0) shr 4) * 16) + (trxDateTime[i].toInt() and 0x0F)
        }

        //card effective date in
        val cardEffectiveDate: Long = rupayUtils.calculateTrxTimeFromCardEffectiveDate(cardEffectiveDate!!,currentTime)

        // Calculate entryTime based on trxTimeFromCardEffDate and cardEffectiveDate
        entryTime = 60 * calculateTrxTimeFromEpoch(cardEffectiveDate, trxTimeFromCardEffDate)

        lastTrxTime= entryTime

        //extract last transaction status
        val lastTrxStatus = extractTrxStatus(csaMasterData.csaUpdatedBinData!!.validationData.trxStatusAndRfu)

        //check last transaction is ENTRY or EXIT
        if (lastTrxStatus.toInt() == TXN_STATUS_EXIT) {


            //if logging on then print the log and save
            if (spUtils.getPreference(LOGGING_ON_OFF, false)) {
                FileLogger.d(TAG, "${currentTime} - ${entryTime} = ${currentTime - entryTime} ?? $ONE_MINUTE")
            }

            //check current time and entry time difference is less then one minute then show card already tapped
            if ((currentTime - entryTime) < ONE_MINUTE) {
                // Ignore the tap as it happened within the double-tap threshold

                //return error to payment app and UI
                handleError(
                    CARD_ALREADY_TAPPED,
                    rupayUtils.getError("CARD_ALREADY_TAPPED")
                )

                return
            }

        }


        // last transaction status check condition
        if (((lastTrxStatus.toInt() == TXN_STATUS_ENTRY) || (lastTrxStatus.toInt() == TXN_STATUS_PENALTY) || isEntryExitOverrideEnable)) {

            //entry date time in specific date format
            val entryDateTime = DateUtils.getTimeInYMDHMS(entryTime)

            //error code
            csaMasterData.rupayMessage?.returnCode = NO_ERROR
            csaMasterData.rupayMessage?.returnMessage = "NO_ERROR"


            if(modeManager.getCurrentMode() == ModeType.INCIDENT_MODE){
                completeProcessCSA(0.0,TXN_STATUS_EXIT,csaMasterData)
            }else
            {

                //complete the CSA process
                //TODO call fare request api and complete the process
                completeProcessCSA(100.0,TXN_STATUS_EXIT,csaMasterData)

                //create Fare Request with the data
                val fareRequest = GateFareRequest()
                fareRequest.fromStationId=csaMasterData.csaDisplayData!!.lastStationId
                fareRequest.toStationId=spUtils.getPreference(STATION_ID,"0402")
                fareRequest.entryDateTime=entryDateTime
                fareRequest.exitDateTime=DateUtils.getSysDateTime()
                fareRequest.equipmentId=spUtils.getPreference(EQUIPMENT_ID,"5001")
                fareRequest.equipmentGroupId=spUtils.getPreference(EQUIPMENT_GROUP_ID,"5")


                //            runBlocking {
//
//                val result = calculateFare(fareRequest)
//
//                if(result.isSuccess){
//
//                    val response= result.getOrNull()
//
//                    if(response!=null){
//                        handleFareCalculationData(response,csaMasterData)
//                    }else{
//
//                        //return error to payment app and UI
//                        handleError(
//                            FAILURE_FARE_API,
//                            rupayUtils.getError("FAILURE_FARE_API")
//                        )
//                        return@runBlocking
//                    }
//
//                }else{
//                    return@runBlocking
//                }
//            }
            }

        }else{

            //return error to payment app, not to the UI
            handleError(
                ENTRY_NOT_FOUND,
                rupayUtils.getError("ENTRY_NOT_FOUND_CSA"),
                false
            )

            //write back the error on the card
            errorWriteCSA(ENTRY_NOT_FOUND,"ENTRY_NOT_FOUND_CSA",csaMasterData)
        }

    }

    /**
     * Method to process exit gate osa data
     */
    private fun processExitOSA(osaMasterData: OSAMasterData) {


        //double tap threshold
        val DOUBLE_TAP_THRESHOLD = spUtils.getPreference(DOUBLE_TAP_THRESHOLD, 3) // 3 seconds
        isEntryExitOverrideEnable = spUtils.getPreference(ENTRY_EXIT_OVERRIDE, false)

        //get osa raw data
        val osaRawData = osaMasterData.osaBinData

        //get display data
        val osaDataDisplay = osaMasterData.osaDisplayData

        //check general data for version
        if (osaRawData!!.generalInfo.versionNumber != IPCConstants.VERSION_NUMBER) {
            FileLogger.d(
                TAG,
                "Card CSA General Info Version: ${osaRawData.generalInfo.versionNumber}"
            )
        }

        //check general data for language
        if (!isLanguageEnglish(osaRawData.generalInfo.languageInfo)) {
            FileLogger.d(
                TAG,
                "Card CSA General Info Language: ${osaRawData.generalInfo.languageInfo}"
            )
        }


        FileLogger.e(TAG,">>>>VALIDATION ERROR CODE: ${osaRawData.validationData.errorCode}")


        //check for existing error
        if (osaRawData.validationData.errorCode!!.toInt() != NO_ERROR) {

            FileLogger.e(TAG, "Existing Error : ${osaDataDisplay?.errorCode}")

            //return error to payment app and UI
            handleError(
                osaRawData.validationData.errorCode!!.toInt(),
                rupayUtils.getError(osaRawData.validationData.errorCode!!.toString())
            )

            return
        }


        //code to calculate entry time from card effective date
        var trxTimeFromCardEffDate: Long = 0
        var entryTime: Long=0
        var lastTrxTime:Long=0
        val currentTime: Long = System.currentTimeMillis() / 1000 // Get current time in seconds from epoch

        // CSA.validationData.trxDateTime is assumed to be a byte array of size 3
        val trxDateTime: ByteArray = osaRawData.validationData.trxDateTime

        // Calculate last transaction time
        for (i in 0 until 3) {
            trxTimeFromCardEffDate = (trxTimeFromCardEffDate * 16 * 16) +
                    (((trxDateTime[i].toInt() and 0xF0) shr 4) * 16) + (trxDateTime[i].toInt() and 0x0F)
        }

        //card effective date in
        val cardEffectiveDate: Long = rupayUtils.calculateTrxTimeFromCardEffectiveDate(cardEffectiveDate!!,currentTime)

        // Calculate entryTime based on trxTimeFromCardEffDate and cardEffectiveDate
        entryTime = 60 * calculateTrxTimeFromEpoch(cardEffectiveDate, trxTimeFromCardEffDate)

        lastTrxTime= entryTime

        //extract last transaction status
        val lastTrxStatus = extractTrxStatus(osaRawData.validationData.trxStatusAndRfu)

        //check last transaction is ENTRY or EXIT
        if (lastTrxStatus == TXN_STATUS_EXIT) {


            //if logging on then print the log and save
            if (spUtils.getPreference(LOGGING_ON_OFF, false)) {
                FileLogger.d(TAG, "${currentTime} - ${entryTime} = ${currentTime - entryTime} ?? $ONE_MINUTE")
            }

            //check current time and entry time difference is less then one minute then show card already tapped
            if ((currentTime - entryTime) < ONE_MINUTE) {
                // Ignore the tap as it happened within the double-tap threshold

                //return error to payment app and UI
                handleError(
                    CARD_ALREADY_TAPPED,
                    rupayUtils.getError("CARD_ALREADY_TAPPED")
                )

                return
            }

        }


        // last transaction status check condition
        if (((lastTrxStatus == TXN_STATUS_ENTRY) || (lastTrxStatus == TXN_STATUS_PENALTY) || isEntryExitOverrideEnable)) {

            //error code
            osaMasterData.rupayMessage?.returnCode = NO_ERROR
            osaMasterData.rupayMessage?.returnMessage = "NO_ERROR"

            //complete the OSA process
            completeProcessOSA(TXN_STATUS_EXIT,osaMasterData)

        }else{

            //return error to payment app, not to the UI
            handleError(
                ENTRY_NOT_FOUND,
                rupayUtils.getError("ENTRY_NOT_FOUND_CSA"),
                false
            )

            //write back the error on the card
            errorWriteOSA(ENTRY_NOT_FOUND,"ENTRY_NOT_FOUND_CSA",osaMasterData)
        }

    }

    /**
     * Method to handle Fare calculation for CSA
     */
    private fun handleFareCalculationData(response: GateFareResponse,csaMasterData: CSAMasterData){

        if(response.returnCode == TIME_EXCEEDED){

            //set error code and error message
            csaMasterData.rupayMessage?.returnCode = TIME_EXCEEDED
            csaMasterData.rupayMessage?.returnMessage =
                rupayUtils.getError("TIME_EXCEEDED")

            //return error to payment app, not to the UI
            handleError(
                TIME_EXCEEDED,
                rupayUtils.getError("TIME_EXCEEDED"),
                false
            )

            errorWriteCSA(TIME_EXCEEDED,"TIME_EXCEEDED",csaMasterData)

            return

        }else if(response.returnCode == READER_FUNCTIONALITY_DISABLED){

            //return error to payment app and UI
            handleError(
                READER_FUNCTIONALITY_DISABLED,
                rupayUtils.getError("READER_FUNCTIONALITY_DISABLED")
            )

            return

        }else if(response.returnCode == NO_ERROR){

            //card balance
            val cardBalance = csaMasterData.csaDisplayData?.cardBalance


            //check balance is sufficient or not
            if(!balanceIsSufficient(response.fare.toDouble(),cardBalance!!)){

                //set error code and error message
                csaMasterData.rupayMessage?.returnCode = AMT_NOT_SUFFICIENT
                csaMasterData.rupayMessage?.returnMessage =
                    rupayUtils.getError("AMT_NOT_SUFFICIENT")

                //return error to payment app, not to the UI
                handleError(
                    AMT_NOT_SUFFICIENT,
                    rupayUtils.getError("AMT_NOT_SUFFICIENT"),
                    false
                )

                errorWriteCSA(AMT_NOT_SUFFICIENT,"TIME_EXCEEDED",csaMasterData)

                return
            }

            //error code
            csaMasterData.rupayMessage?.returnCode = NO_ERROR
            csaMasterData.rupayMessage?.returnMessage = "NO_ERROR"

            //complete the CSA process
            completeProcessCSA(response.fare.toDouble(),TXN_STATUS_EXIT,csaMasterData)
        }
    }

    /**
     * Method to handle Fare calculation for Zone Pass
     */
    private fun handleZoneFareOSA(response: GateFareResponse,osaMasterData: OSAMasterData,zoneTable: ZoneTable){

        if(response.returnCode == TIME_EXCEEDED){

            //set error code and error message
            osaMasterData.rupayMessage?.returnCode = TIME_EXCEEDED
            osaMasterData.rupayMessage?.returnMessage =
                rupayUtils.getError("TIME_EXCEEDED")

            //return error to payment app, not to the UI
            handleError(
                TIME_EXCEEDED,
                rupayUtils.getError("TIME_EXCEEDED"),
                false
            )

            errorWriteOSA(TIME_EXCEEDED,"TIME_EXCEEDED",osaMasterData)

            return

        }else if(response.returnCode == READER_FUNCTIONALITY_DISABLED){

            //return error to payment app and UI
            handleError(
                READER_FUNCTIONALITY_DISABLED,
                rupayUtils.getError("READER_FUNCTIONALITY_DISABLED")
            )

            return

        }else if(response.returnCode == NO_ERROR){

            val fare= response.fare.toDouble()


            if(fare == zoneTable.zoneAmount){

                //error code
                osaMasterData.rupayMessage?.returnCode = NO_ERROR
                osaMasterData.rupayMessage?.returnMessage = "NO_ERROR"

                //complete the CSA process
                completeProcessOSA(TXN_STATUS_EXIT,osaMasterData)

            }else{
                return
            }


        }
    }

    /**
     * Check if balance is sufficient in the card or not
     */
    private fun balanceIsSufficient(calculatedFare: Double, cardBalance: Double): Boolean {

        if (cardBalance >= calculatedFare) {
            return true
        } else {
            return false
        }
    }


    private fun errorWriteCSA(errorCode:Int, errorMessage: String, csaMasterData: CSAMasterData){

        //set transaction status
        csaMasterData.csaUpdatedBinData!!.validationData.errorCode=errorCode.toByte()


        //convert the updated csa bin to byte array
        val csaSent =  rupayUtils.csaToByteArray(csaMasterData.csaUpdatedBinData!!)


        //add the header and footer for the update
        val startIndex= csaMasterData.bf200Data?.serviceDataIndex!!
        val endIndex= csaMasterData.bf200Data?.serviceDataIndex!!+95
        var updatedDataWithHeaderFooter=csaMasterData.bf200Data?.serviceRelatedData
        for( i in startIndex..endIndex){
            updatedDataWithHeaderFooter!!.set(i, csaSent[i-startIndex])
        }

        //send back the message
        communicationService.sendData(MSG_ID_TRANSIT_VALIDATION_FAIL_RUPAY_NCMC,updatedDataWithHeaderFooter!!.toByteArray().toHexString())
    }

    private fun errorWriteOSA(errorCode:Int, errorMessage: String, osaMasterData: OSAMasterData){

        //set transaction status
        osaMasterData.osaUpdatedBinData!!.validationData.errorCode=errorCode.toByte()


        //convert the updated csa bin to byte array
        val osaSent =  rupayUtils.osaBinToByteArray(osaMasterData.osaUpdatedBinData!!)


        //add the header and footer for the update
        val startIndex= osaMasterData.bf200Data?.serviceDataIndex!!
        val endIndex= osaMasterData.bf200Data?.serviceDataIndex!!+95
        var updatedDataWithHeaderFooter=osaMasterData.bf200Data?.serviceRelatedData
        for( i in startIndex..endIndex){
            updatedDataWithHeaderFooter!!.set(i, osaSent[i-startIndex])
        }

        //send back the message
        communicationService.sendData(MSG_ID_TRANSIT_VALIDATION_FAIL_RUPAY_NCMC,updatedDataWithHeaderFooter!!.toByteArray().toHexString())
    }

    /**
     * Method to complete the CSA process, where
     */
    private fun completeProcessCSA(fare: Double, txnStatus: Int, csaMasterData: CSAMasterData) {

        //error code
        csaMasterData.rupayMessage?.returnCode = NO_ERROR
        csaMasterData.rupayMessage?.returnMessage = "NO_ERROR"

        //set transaction status
        csaMasterData.csaUpdatedBinData!!.validationData.trxStatusAndRfu=rupayUtils.combineToByte(txnStatus,0)

        //calculate card effective date
        cardEffectiveDate= rupayUtils.readCardEffectiveDate(rupayUtils.hexStringToByteArray(csaMasterData.bf200Data!!.b.`5F25`!!))

        //product type
        csaMasterData.csaUpdatedBinData!!.validationData.productType = PROD_TYPE_SINGLE_JOURNEY

        //terminal info
        val acquirerID = spUtils.getPreference(ACQUIRER_ID, "04")
        val operatorID = spUtils.getPreference(OPERATOR_ID, "6014")
        var terminalID = ""

        if(txnStatus == TXN_STATUS_ENTRY) {
             terminalID = spUtils.getPreference(TERMINAL_ID, "401101")
        }else{
            terminalID = spUtils.getPreference(TERMINAL_ID, "402141")
        }

        val acquirerIDBytes = rupayUtils.hexToByte(acquirerID)!!
        val operatorIDBytes = rupayUtils.hexToByteArray(operatorID)!!
        val terminalIDBytes = rupayUtils.hexToByteArray(terminalID)!!

        csaMasterData.csaUpdatedBinData!!.validationData.acquirerID = acquirerIDBytes
        csaMasterData.csaUpdatedBinData!!.validationData.operatorID = operatorIDBytes
        csaMasterData.csaUpdatedBinData!!.validationData.terminalID = terminalIDBytes


        //date and time
        val trxTimeFromEpoch: Long = System.currentTimeMillis() / 1000
        val trxTimeFromCardEffDate = rupayUtils.calculateTrxTimeFromCardEffectiveDate(cardEffectiveDate!!,trxTimeFromEpoch)

        Utils.numToBin(
            csaMasterData.csaUpdatedBinData?.validationData?.trxDateTime!!,
            trxTimeFromCardEffDate.toLong(),
            3
        )

        //fare amount (Penalty amount)
        val penaltyAmount =fare
        Utils.numToBin(
            csaMasterData.csaUpdatedBinData?.validationData?.fareAmt!!,
            (penaltyAmount / 10).toLong(), 2
        )

        //save entry cache data
        if(txnStatus == TXN_STATUS_EXIT) {
            saveEntryCacheCsa(csaMasterData)
        }


        //History Data
        updateHistory(fare, csaMasterData,false, trxStatus = txnStatus)

        //convert the updated csa bin to byte array
        val csaSent =  rupayUtils.csaToByteArray(csaMasterData.csaUpdatedBinData!!)


        //add the header and footer for the update
        val startIndex= csaMasterData.bf200Data?.serviceDataIndex!!
        val endIndex= csaMasterData.bf200Data?.serviceDataIndex!!+95
        var updatedDataWithHeaderFooter=csaMasterData.bf200Data?.serviceRelatedData
        for( i in startIndex..endIndex){
            updatedDataWithHeaderFooter!!.set(i, csaSent[i-startIndex])
        }

        //if exit side fare needs to deduct, so for fare deduction below code work
        if(fare>0) {

            //first remove last two bytes
            updatedDataWithHeaderFooter!!.removeLast()
            updatedDataWithHeaderFooter!!.removeLast()

            // Convert Amount to tag and append to the service data
            val bcdAmount = ByteArray(6)
            rupayUtils.convertAmountToBCD(1, bcdAmount)

            //Add tag 9F02
            updatedDataWithHeaderFooter.add(0x9F.toByte())
            updatedDataWithHeaderFooter.add(0x02.toByte())

            // Append Length 06
            updatedDataWithHeaderFooter.add(0x06.toByte())

            // Append Data (Fare Value)
            for (j in bcdAmount.indices) {
                updatedDataWithHeaderFooter.add(bcdAmount[j])
            }

            // Add two bytes status at the end of the response(which we removed earlier)
            updatedDataWithHeaderFooter.add(0x30.toByte())
            updatedDataWithHeaderFooter.add(0x30.toByte())

            // Change total Length
            var totalLen =
                updatedDataWithHeaderFooter[1].toInt() or (updatedDataWithHeaderFooter[0].toInt() shl 8)
            totalLen += 9  // 2 bytes status + 9 byte 9F02

            // Construct 2 byte header lengths
            updatedDataWithHeaderFooter[0] = ((totalLen shr 8) and 0xFF).toByte()
            updatedDataWithHeaderFooter[1] = (totalLen and 0xFF).toByte()
        }




        //send back the message
        communicationService.sendData(MSG_ID_TRANSIT_VALIDATION_RUPAY_NCMC,updatedDataWithHeaderFooter!!.toByteArray().toHexString())

    }


    /**
     * method to validate pass list
     */
    private fun validatePassList(passList: List<PassBin>):Pair<Boolean, Int?>{

        //Holiday flag
        val isTodayHoliday = spUtils.getPreference(IS_TODAY_HOLIDAY,false)

        //Event flag
        val isTodayEvent = spUtils.getPreference(IS_TODAY_EVENT,false)


        passList.forEachIndexed { index, pass ->

            //set pass for validation
            passValidator.setPass(pass)

            // 1. Check Pass Expiry (endDateTime)
            if (passValidator.isExpired(pass.endDateTime)) {
                return@forEachIndexed // Skip expired pass and move to the next one
            }


            // 2. Check if today is a holiday
            if(pass.productType!!.toInt() ==100 && !isTodayHoliday){
                //if pass type is holiday but today not holiday skip this pass
                return@forEachIndexed // Skip this pass and move to the next one
            }

            // 3.Check if today is Event
            if(pass.productType!!.toInt() ==101 && !isTodayEvent){
                //if pass type is event but today not event skip this pass
                return@forEachIndexed // Skip this pass and move to the next one
            }

            // 4. Check Pass Limit
            val passLimit = pass.passLimit ?: 0
            if (passLimit == 0.toByte()) return@forEachIndexed // Move to next pass if pass limit is 0
            //if (passLimit != 99.toByte() && (pass.tripCount ?: 0) >= passLimit) return@forEachIndexed // Exceed limit, move to next pass

            // 5. Check Daily Limit
            passValidator.validateDailyLimit(pass)
            val dailyLimit = pass.dailyLimit ?: 0
            if (dailyLimit == 0.toByte()) return@forEachIndexed // Move to next pass if daily limit is 0
            if (dailyLimit != 99.toByte() && (pass.tripCount ?: 0) >= dailyLimit) return@forEachIndexed // Exceed limit, move to next pass

            // 6. Check Source Station
            var stationInfo:StationsTable?=null
            val currentStationId= spUtils.getPreference(STATION_ID,"0401")
            val validEntryStationId = pass.validEntryStationId ?: 0

            runBlocking {
                stationInfo= dbRepository.getStationById(validEntryStationId.toInt())
            }

            if(pass.validZoneId == 99.toByte() && stationInfo!=null && (currentStationId!=stationInfo?.stationId)){
                return@forEachIndexed // Move to next pass if source ID mismatch and for non zone pass
            }

            // If none of the conditions fail, we found a valid pass
            return Pair(true, index)
        }
        return Pair(false, null) // No valid pass found
    }

    /**
     * Method for validation at Exit side
     */
    fun checkExitValidation(currentStation:Byte, passBin:PassBin,osaMasterData: OSAMasterData):Boolean{

        //check if pass is without zone
        if(passBin.validZoneId == 99.toByte()){

            if(passBin.validExitStationId != 99.toByte()){
                return currentStation == passBin.validExitStationId
            }else{
                return true
            }

        }else{

            //validate with zone id
            val zoneId= passBin.validZoneId!!.toInt()

            //code to calculate entry time from card effective date
            var trxTimeFromCardEffDate: Long = 0
            var entryTime: Long=0
            var lastTrxTime:Long=0
            val currentTime: Long = System.currentTimeMillis() / 1000 // Get current time in seconds from epoch

            //entry date time in specific date format
            val entryDateTime = rupayUtils.byteArrayToHex(osaMasterData.osaBinData!!.validationData.trxDateTime)

            val exitDateTime =DateUtils.getTimeInYMDHMS(currentTime)

            //get entry terminal id
            val entryTerminalId= rupayUtils.byteArrayToHex(osaMasterData.osaBinData!!.validationData.stationCode)

            //create Fare Request with the data
            val fareRequest = GateFareRequest()
            fareRequest.fromStationId=entryTerminalId
            fareRequest.toStationId=spUtils.getPreference(STATION_ID,"")
            fareRequest.entryDateTime=entryDateTime
            fareRequest.exitDateTime=exitDateTime
            fareRequest.equipmentId=spUtils.getPreference(EQUIPMENT_ID,"")
            fareRequest.equipmentGroupId=spUtils.getPreference(EQUIPMENT_GROUP_ID,"")

            //zone data to get
            var zoneTable:ZoneTable

            runBlocking {

                zoneTable= dbRepository.getZoneById(zoneId)

                val result = calculateFare(fareRequest)

                if(result.isSuccess){

                    val response= result.getOrNull()

                    if(response!=null){

                        if(response.returnCode == TIME_EXCEEDED){

                            //set error code and error message
                            osaMasterData.rupayMessage?.returnCode = TIME_EXCEEDED
                            osaMasterData.rupayMessage?.returnMessage =
                                rupayUtils.getError("TIME_EXCEEDED")

                            //return error to payment app, not to the UI
                            handleError(
                                TIME_EXCEEDED,
                                rupayUtils.getError("TIME_EXCEEDED"),
                                false
                            )

                            errorWriteOSA(TIME_EXCEEDED,"TIME_EXCEEDED",osaMasterData)

                            return@runBlocking

                        }else if(response.returnCode == READER_FUNCTIONALITY_DISABLED){

                            //return error to payment app and UI
                            handleError(
                                READER_FUNCTIONALITY_DISABLED,
                                rupayUtils.getError("READER_FUNCTIONALITY_DISABLED")
                            )

                            return@runBlocking

                        }else if(response.returnCode == NO_ERROR) {

                            val fare = response.fare.toDouble()

                            if(zoneTable.zoneAmount == fare){
                                return@runBlocking
                            }

                            return@runBlocking

                        }
                    }else{

                        //return error to payment app and UI
                        handleError(
                            FAILURE_FARE_API,
                            rupayUtils.getError("FAILURE_FARE_API")
                        )
                        return@runBlocking
                    }

                }else{
                    return@runBlocking
                }
            }

        }

        return false
    }


    /**
     * Method to complete the OSA process, where
     */
    private fun completeProcessOSA(txnStatus: Int, osaMasterData: OSAMasterData) {

        //get pass list
        val passList= osaMasterData.osaUpdatedBinData!!.passes

        //Entry side validation
        if(txnStatus == TXN_STATUS_ENTRY){

            //1. check if osa service is inactive
            if(!osaMasterData.osaBinData!!.generalInfo.getServiceStatus()){

                //abort osa transaction
                abortOsaTransaction(osaMasterData)

                return
            }

            //2. check if all pass expired
            if(passValidator.isAllPassExpired(passList)){

                //set osa service inactive
                osaMasterData.osaBinData!!.generalInfo.setServiceStatus(false)

                //abort osa transaction
                abortOsaTransaction(osaMasterData)

                return
            }

            //3. Get valid pass data
            val passValidData=validatePassList(passList)

            //check if any pass valid
            if(passValidData.first){

                //set pass index
                passValidator.validPassIndex= passValidData.second!!

                //current pass
                val currentPass= passList[passValidData.second!!]

                //error code
                osaMasterData.rupayMessage?.returnCode = NO_ERROR
                osaMasterData.rupayMessage?.returnMessage = "NO_ERROR"

                //set osa validation data
                osaMasterData.osaUpdatedBinData!!.validationData.productType=passValidator.passBin.productType!!
                osaMasterData.osaUpdatedBinData!!.validationData.trxStatusAndRfu = rupayUtils.combineToByte(txnStatus,0)


                //calculate card effective date
                cardEffectiveDate= rupayUtils.readCardEffectiveDate(rupayUtils.hexStringToByteArray(osaMasterData.bf200Data!!.b.`5F25`!!))

                //date and time
                val trxTimeFromEpoch: Long = System.currentTimeMillis() / 1000
                val trxTimeFromCardEffDate = rupayUtils.calculateTrxTimeFromCardEffectiveDate(cardEffectiveDate!!,trxTimeFromEpoch)

                Utils.numToBin(
                    osaMasterData.osaUpdatedBinData?.validationData?.trxDateTime!!,
                    trxTimeFromCardEffDate.toLong(),
                    3
                )

                //station Id
                var stationInfo:StationsTable?=null
                val stationId = spUtils.getPreference(STATION_ID, "0401")

                runBlocking {
                    stationInfo = dbRepository.getStationById(stationId)
                }

                //TODO station id our id we are saving in 1 byte have to check if any thing we can do
                Utils.numToBin(
                    osaMasterData.osaUpdatedBinData?.validationData?.stationCode!!,
                    stationId.toLong(),
                    2
                )

                //terminal info
                val acquirerID = spUtils.getPreference(ACQUIRER_ID, "04")
                val operatorID = spUtils.getPreference(OPERATOR_ID, "6014")
                val terminalID = spUtils.getPreference(TERMINAL_ID, "401101")

                val acquirerIDBytes = rupayUtils.hexToByte(acquirerID)!!
                val operatorIDBytes = rupayUtils.hexToByteArray(operatorID)!!
                val terminalIDBytes = rupayUtils.hexToByteArray(terminalID)!!

                //create history data
                val historyBin = HistoryBinOsa()

                //set Terminal Information
                historyBin.acquirerID = acquirerIDBytes
                historyBin.operatorID = operatorIDBytes
                historyBin.terminalID = terminalIDBytes


                //update sequence number
                val terminateSeq = spUtils.getPreference(TRANSACTION_SEQ_NUMBER,1)
                historyBin.trxSeqNum = rupayUtils.num2bin(terminateSeq.toLong(), 2)
                spUtils.savePreference(TRANSACTION_SEQ_NUMBER,terminateSeq+1)

                //trx date and time
                historyBin.trxDateTime = osaMasterData.osaUpdatedBinData?.validationData?.trxDateTime!!

                //product type
                historyBin.productType= currentPass.productType

                //set previous trips
                Utils.numToBin(historyBin.passLimit!!,currentPass.passLimit!!.toLong(),2)

                //remaining trips
                historyBin.tripCount = (currentPass.tripCount!!.toInt()).toByte()

                //daily limit
                historyBin.dailyLimit = (currentPass.dailyLimit!!.toInt()).toByte()

                //trx status
                historyBin.trxStatus = txnStatus.toByte()

                //history rfu
                historyBin.rfu = 0

                //update the bin data(already we have created Queue Data Structure)
                osaMasterData.osaUpdatedBinData!!.history.add(historyBin)

                //set entry from osa to 1 and pass index in rfu
                osaMasterData.osaUpdatedBinData!!.rfu = byteArrayOf(1.toByte(),passValidator.validPassIndex.toByte(),0,0,0)

                //convert the updated csa bin to byte array
                val osaSent =  rupayUtils.osaBinToByteArray(osaMasterData.osaUpdatedBinData!!)

                //add the header and footer for the update
                val startIndex= osaMasterData.bf200Data?.serviceDataIndex!!
                val endIndex= osaMasterData.bf200Data?.serviceDataIndex!!+95
                var updatedDataWithHeaderFooter=osaMasterData.bf200Data?.serviceRelatedData
                for( i in startIndex..endIndex){
                    updatedDataWithHeaderFooter!!.set(i, osaSent[i-startIndex])
                }

                //send back the message
                communicationService.sendData(MSG_ID_TRANSIT_VALIDATION_RUPAY_NCMC,updatedDataWithHeaderFooter!!.toByteArray().toHexString())


            }else{

                //no valid pass set
                //abort osa transaction
                abortOsaTransaction(osaMasterData)

                return
            }

        }else if(txnStatus == TXN_STATUS_EXIT){

            //pass index from which entry happened
            val passIndex = osaMasterData.osaBinData!!.rfu[1].toInt()

            //get pass list
            val passList= osaMasterData.osaBinData!!.passes

            // 1. get previous pass in which pass was validated at Entry
            val previousPass:PassBin =passList.get(passIndex)

            // 2. check validation data to get the previous pass data
            passValidator.setPass(previousPass)
            passValidator.validPassIndex= passIndex


            // 3. get both stations data
            val exitStationId = spUtils.getPreference(STATION_ID,"0402")
            val entryStationId = Utils.binToNum(osaMasterData.osaBinData!!.validationData.stationCode,2)
            var entryStationIdString= entryStationId.toString()
            if(entryStationId.toString().length==3){
                entryStationIdString = "0"+entryStationIdString
            }

            var exitStationInfo:StationsTable?=null
            var entryStationInfo:StationsTable?=null

            runBlocking {
                entryStationInfo = dbRepository.getStationById(entryStationIdString)
                exitStationInfo = dbRepository.getStationById(exitStationId)
            }


            // 3. do exit  validation if with zone or exit validation with station ids
            if(previousPass.validZoneId != 99.toByte() && previousPass.validZoneId != 0.toByte()){

                //get pass zone amount
                var zoneData:ZoneTable?=null
                runBlocking {
                    zoneData = dbRepository.getZoneById(previousPass.validZoneId!!.toInt())
                }

                if(zoneData!=null){

                    //entry date time in specific date format
                    val currentTime: Long = System.currentTimeMillis() / 1000 // Get current time in seconds from epoch
                    val entryDateTime = rupayUtils.byteArrayToHex(osaMasterData.osaBinData!!.validationData.trxDateTime)
                    val exitDateTime =DateUtils.getTimeInYMDHMS(currentTime)

                    //create Fare Request with the data
                    val fareRequest = GateFareRequest()
                    fareRequest.fromStationId=entryStationInfo!!.stationId
                    fareRequest.toStationId=exitStationInfo!!.stationId
                    fareRequest.entryDateTime=entryDateTime
                    fareRequest.exitDateTime=exitDateTime
                    fareRequest.equipmentId=spUtils.getPreference(EQUIPMENT_ID,"1")
                    fareRequest.equipmentGroupId=spUtils.getPreference(EQUIPMENT_GROUP_ID,"2")
                    fareRequest.terminalId=spUtils.getPreference(TERMINAL_ID,"3")
                    fareRequest.productType = TicketType.PASS.type.toString()
                    fareRequest.passType = previousPass.productType!!.toInt().toString()
                    fareRequest.passStartDate = osaMasterData.osaDisplayData!!.cardPassesList[passIndex].startDateTime
                    fareRequest.passExpiryDate = osaMasterData.osaDisplayData!!.cardPassesList[passIndex].endDate
                    fareRequest.noOfTripRemaining = previousPass.passLimit!!.toInt().toString()


                    runBlocking {

                        val result = calculateFare(fareRequest)

                        if(result.isSuccess){

                            val response= result.getOrNull()

                            if(response!=null){

                                if(response.returnCode == TIME_EXCEEDED){

                                    osaMasterData.osaUpdatedBinData!!.validationData.trxStatusAndRfu = rupayUtils.combineToByte(TXN_STATUS_EXIT,0)

                                    //set entry from osa to 0 and pass index to 0
                                    osaMasterData.osaUpdatedBinData!!.rfu = byteArrayOf(0,0,0,0,0)

                                    //make osa penalty to true
                                    ShellInfoLibrary.isOsaTrxAbortWithPenalty=true

                                    //abort osa transaction
                                    abortOsaTransaction(osaMasterData)

                                    return@runBlocking

                                }else if(response.returnCode == READER_FUNCTIONALITY_DISABLED){



                                    //return error to payment app and UI
                                    handleErrorOSA(
                                        READER_FUNCTIONALITY_DISABLED,
                                        rupayUtils.getError("READER_FUNCTIONALITY_DISABLED")
                                    )

                                    return@runBlocking

                                }else if(response.returnCode == NO_ERROR) {

                                    val fare = response.fare.toDouble()

                                    if(zoneData!!.zoneAmount == fare){

                                        //write on osa data and send back the result
                                        sendExitOsaSuccess(osaMasterData,previousPass,txnStatus,exitStationInfo!!.id,passIndex)

                                    }else{

                                        osaMasterData.osaUpdatedBinData!!.validationData.trxStatusAndRfu = rupayUtils.combineToByte(TXN_STATUS_EXIT,0)

                                        //make osa trx abort to true
                                        ShellInfoLibrary.isOsaTrxAbort=true

                                        //set entry from osa to 0 and pass index to 0
                                        osaMasterData.osaUpdatedBinData!!.rfu = byteArrayOf(0,0,0,0,0)

                                        //abort osa transaction
                                        abortOsaTransaction(osaMasterData)

                                        return@runBlocking
                                    }



                                }
                            }else{

                                //return error to payment app and UI
                                handleError(
                                    FAILURE_FARE_API,
                                    rupayUtils.getError("FAILURE_FARE_API")
                                )
                                return@runBlocking
                            }

                        }else{

                            osaMasterData.osaUpdatedBinData!!.validationData.trxStatusAndRfu = rupayUtils.combineToByte(TXN_STATUS_EXIT,0)

                            //make osa trx abort to true
                            ShellInfoLibrary.isOsaTrxAbort=true

                            //set entry from osa to 0 and pass index to 0
                            osaMasterData.osaUpdatedBinData!!.rfu = byteArrayOf(0,0,0,0,0)


                            //abort osa transaction
                            abortOsaTransaction(osaMasterData)

                            return@runBlocking
                        }
                    }
                }

            }else if(previousPass.validEntryStationId != 99.toByte() && previousPass.validExitStationId != 99.toByte()){



                if(entryStationInfo==null || exitStationInfo==null || previousPass.validEntryStationId!!.toInt() !=entryStationInfo!!.id || previousPass.validExitStationId!!.toInt() !=exitStationInfo!!.id){

                    osaMasterData.osaUpdatedBinData!!.validationData.trxStatusAndRfu = rupayUtils.combineToByte(TXN_STATUS_EXIT,0)

                    //make osa trx abort to true
                    ShellInfoLibrary.isOsaTrxAbort=true

                    //set entry from osa to 0 and pass index to 0
                    osaMasterData.osaUpdatedBinData!!.rfu = byteArrayOf(0,0,0,0,0)

                    //abort osa transaction
                    abortOsaTransaction(osaMasterData)
                }else{
                    //write on osa data and send back the result
                    sendExitOsaSuccess(osaMasterData,previousPass,txnStatus,exitStationInfo!!.id,passIndex)
                }
            }
        }
    }

    fun sendExitOsaSuccess(osaMasterData: OSAMasterData,previousPass:PassBin,txnStatus: Int,stationId:Int,passIndex:Int){

        //update exit validation data
        //error code
        osaMasterData.rupayMessage?.returnCode = NO_ERROR
        osaMasterData.rupayMessage?.returnMessage = "NO_ERROR"

        //set osa validation data
        osaMasterData.osaUpdatedBinData!!.validationData.trxStatusAndRfu = rupayUtils.combineToByte(txnStatus,0)

        //date and time
        val trxTimeFromEpoch: Long = System.currentTimeMillis() / 1000
        val trxTimeFromCardEffDate = rupayUtils.calculateTrxTimeFromCardEffectiveDate(cardEffectiveDate!!,trxTimeFromEpoch)

        Utils.numToBin(
            osaMasterData.osaUpdatedBinData?.validationData?.trxDateTime!!,
            trxTimeFromCardEffDate.toLong(),
            3
        )


        //terminal info
        val acquirerID = spUtils.getPreference(ACQUIRER_ID, "04")
        val operatorID = spUtils.getPreference(OPERATOR_ID, "6014")
        val terminalID = spUtils.getPreference(TERMINAL_ID, "402141")

        val acquirerIDBytes = rupayUtils.hexToByte(acquirerID)!!
        val operatorIDBytes = rupayUtils.hexToByteArray(operatorID)!!
        val terminalIDBytes = rupayUtils.hexToByteArray(terminalID)!!

        //validation data station id
        val stationIdCurrent = spUtils.getPreference(STATION_ID, "0402")
        Utils.numToBin(
            osaMasterData.osaUpdatedBinData?.validationData?.stationCode!!,
            stationIdCurrent.toLong(),
            2
        )


        saveEntryCacheOsa(osaMasterData)

        //create history data
        val historyBin = HistoryBinOsa()

        //set Terminal Information
        historyBin.acquirerID = acquirerIDBytes
        historyBin.operatorID = operatorIDBytes
        historyBin.terminalID = terminalIDBytes

        //trx date time for history
        historyBin.trxDateTime = osaMasterData.osaUpdatedBinData?.validationData?.trxDateTime!!

        //update sequence number
        val terminateSeq = spUtils.getPreference(TRANSACTION_SEQ_NUMBER,1)
        historyBin.trxSeqNum = rupayUtils.num2bin(terminateSeq.toLong(), 2)
        spUtils.savePreference(TRANSACTION_SEQ_NUMBER,terminateSeq+1)


        //product type
        historyBin.productType= previousPass?.productType

        //set previous trips
        Utils.numToBin(historyBin.passLimit!!,(previousPass.passLimit!!.toInt()-1).toLong(),2)

        //remaining trips
        historyBin.tripCount = (previousPass.tripCount!!.toInt()+1).toByte()

        //daily limit
        historyBin.dailyLimit = (previousPass.dailyLimit!!.toInt()).toByte()

        //transaction status change
        historyBin.trxStatus = txnStatus.toByte()

        //history rfu
        historyBin.rfu = 0

        //update the bin data(already we have created Queue Data Structure)
        osaMasterData.osaUpdatedBinData!!.history.add(historyBin)


        //update pass trips and daily limits
        val newPassLimit = osaMasterData.osaUpdatedBinData!!.passes[passIndex].passLimit!!.toInt()-1
        val newTripCount = osaMasterData.osaUpdatedBinData!!.passes[passIndex].tripCount!!.toInt()+1
        osaMasterData.osaUpdatedBinData!!.passes[passIndex].passLimit= newPassLimit.toByte()
        osaMasterData.osaUpdatedBinData!!.passes[passIndex].tripCount= newTripCount.toByte()

        //set entry from osa to 0 and pass index to 0
        osaMasterData.osaUpdatedBinData!!.rfu = byteArrayOf(0,0,0,0,0)


        //convert the updated csa bin to byte array
        val osaSent =  rupayUtils.osaBinToByteArray(osaMasterData.osaUpdatedBinData!!)


        //add the header and footer for the update
        val startIndex= osaMasterData.bf200Data?.serviceDataIndex!!
        val endIndex= osaMasterData.bf200Data?.serviceDataIndex!!+95
        var updatedDataWithHeaderFooter=osaMasterData.bf200Data?.serviceRelatedData
        for( i in startIndex..endIndex){
            updatedDataWithHeaderFooter!!.set(i, osaSent[i-startIndex])
        }

        //send back the message
        communicationService.sendData(MSG_ID_TRANSIT_VALIDATION_RUPAY_NCMC,updatedDataWithHeaderFooter!!.toByteArray().toHexString())
    }


    fun ByteArray.toHexString(): String = joinToString(separator = " ") { "%02x".format(it) }

    fun updateHistory(fareAmount: Double, csaMasterData: CSAMasterData,isPenalty:Boolean?=false,trxStatus:Int) {

        //create history data
        val historyBin = HistoryBin()

        //set Terminal Information
        historyBin.acquirerID = csaMasterData.csaUpdatedBinData?.validationData?.acquirerID
        historyBin.operatorID = csaMasterData.csaUpdatedBinData?.validationData?.operatorID
        historyBin.terminalID = csaMasterData.csaUpdatedBinData?.validationData?.terminalID

        //transaction date time
        historyBin.trxDateTime = csaMasterData.csaUpdatedBinData?.validationData?.trxDateTime

        //update sequence number
        val terminateSeq = spUtils.getPreference(TRANSACTION_SEQ_NUMBER,1)
        historyBin.trxSeqNum = rupayUtils.num2bin(terminateSeq.toLong(), 2)
        spUtils.savePreference(TRANSACTION_SEQ_NUMBER,terminateSeq+1)

        //set transaction amount
        historyBin.trxAmt = rupayUtils.num2bin((fareAmount / 10).toLong(), 2)

        //card global balance
        val cardBalance =
            rupayUtils.num2bin(((csaMasterData.csaDisplayData?.cardBalance)!! / 10).toLong(), 3)
        historyBin.cardBalance1 =
            (((cardBalance!![0].toUByte().toInt() and 0x0F) shl 4) + ((cardBalance[1].toUByte()
                .toInt() and 0xF0) ushr 4)).toByte()
        historyBin.cardBalance2 =
            (((cardBalance[1].toUByte().toInt() and 0x0F) shl 4) + ((cardBalance[2].toUByte()
                .toInt() and 0xF0) ushr 4)).toByte()
        
        
        //as card balance3 and trx status are combined
        val combinedByte = rupayUtils.combineToByte(cardBalance[1].toUByte().toInt(), trxStatus.toUByte().toInt())
        historyBin.cardBalance3 = combinedByte

//        //transaction status
//        if(isPenalty == false) {
//            historyBin.trxStatus =
//                ((trxStatus.toUByte().toInt() shr 4) and 0x0F).toByte()
//        }else{
//            historyBin.trxStatus =
//                ((TXN_STATUS_PENALTY.toUByte().toInt() shr 4) and 0x0F).toByte()
//        }

        //history rfu
        historyBin.rfu = 0

        //update the bin data(already we have created Queue Data Structure)
        csaMasterData.csaUpdatedBinData!!.history.add(historyBin)

    }


    /**
     * Method to calculate the transaction time
     */
    fun calculateTrxTimeFromCardEffectiveDate(
        cardEffectiveDate: String,
        trxTimeFromEpoch: Long
    ): Long {
        // Parse the cardEffectiveDate string
        val dateFormatter = SimpleDateFormat("ddMMyyyy", Locale.US)
        val cardEffDate: Date = dateFormatter.parse(cardEffectiveDate) ?: return -1

        // Get the epoch time of the card effective date
        val cardEffDateFromEpoch = cardEffDate.time / 1000

        // Calculate the difference in minutes
        val trxTimeFromCardEffDate = (trxTimeFromEpoch / 60) - (cardEffDateFromEpoch / 60)

        return trxTimeFromCardEffDate
    }

    // Sample usage:
    fun calculateTrxTimeFromEpoch(cardEffectiveDate: Long, trxTimeFromCardEffDate: Long): Long {
        // Replace this with the actual logic for calculating transaction time from epoch
        return cardEffectiveDate + trxTimeFromCardEffDate
    }

    /**
     * Set penalty amount
     */
    fun setPenaltyAmount(amount:Double){
        this.penaltyAmount= amount
    }

    /**
     * Api call to validate entry data
     */
    suspend fun validateEntry(entryValidationRequest: EntryValidationRequest): Result<EntryValidationResponse> {
        // Use CompletableDeferred to return the result once it's set
        val result = CompletableDeferred<Result<EntryValidationResponse>>()

        try {
            CoroutineScope(Dispatchers.IO).launch {
                apiRepository.doEntryValidation(entryValidationRequest).collect {
                    when (it) {
                        is ApiResponse.Loading -> {
                            // Loading state, you can handle it if needed
                        }

                        is ApiResponse.Success -> {

                            result.complete(Result.success(it.data))
                        }

                        is ApiResponse.Error -> {
                            handleError(FAILURE_ENTRY_VALIDATION, rupayUtils.getError("FAILURE_ENTRY_VALIDATION"))
                            logErrorDetails()
                            result.complete(Result.failure(Exception("Entry Validation API Error")))
                        }

                        else -> {
                            logErrorDetails()
                            result.complete(Result.failure(Exception("Entry Validation API ELSE Block")))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logCatchErrorDetails()
            result.complete(Result.failure(e))
        }

        // Await the result from the coroutine
        return result.await()
    }


    /**
     * Api call to validate entry data
     */
    suspend fun calculateFare(gateFareRequest: GateFareRequest): Result<GateFareResponse> {
        // Use CompletableDeferred to return the result once it's set
        val result = CompletableDeferred<Result<GateFareResponse>>()

        try {
            CoroutineScope(Dispatchers.IO).launch {
                apiRepository.doFareCalculation(gateFareRequest).collect {
                    when (it) {
                        is ApiResponse.Loading -> {
                            // Loading state, you can handle it if needed
                        }

                        is ApiResponse.Success -> {

                            result.complete(Result.success(it.data))
                        }

                        is ApiResponse.Error -> {
                            handleError(FAILURE_FARE_API, rupayUtils.getError("FAILURE_FARE_API"))
                            result.complete(Result.failure(Exception("FARE API Error")))
                        }

                        else -> {
                            result.complete(Result.failure(Exception("FARE API ELSE Block")))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            result.complete(Result.failure(e))
        }

        // Await the result from the coroutine
        return result.await()
    }

    fun logErrorDetails() {
        if (spUtils.getPreference(LOGGING_ON_OFF, false)) {
            FileLogger.e(TAG, "Entry Validation API Error Block Code Executed")
            FileLogger.e(TAG, "Entry Validation : Entry Exit Override :${isEntryExitOverrideEnable}")
            FileLogger.e(TAG, "Entry Validation : Check Emg Mode :${isCheckEmergencyMode}")
        }
    }

    fun logCatchErrorDetails() {
        if (spUtils.getPreference(LOGGING_ON_OFF, false)) {
            FileLogger.e(TAG, "Entry Validation API CATCH Block Code Executed")
            FileLogger.e(TAG, "Entry Validation : Entry Exit Override :${isEntryExitOverrideEnable}")
            FileLogger.e(TAG, "Entry Validation : Check Emg Mode :${isCheckEmergencyMode}")
        }
    }

    /**
     * Save pass in local database
     */
    fun savePassPurchaseData(){

        //get pass creation request
        val passCreationData= ShellInfoLibrary.passCreateRequest

        //operator id
        val operatorId = spUtils.getPreference(OPERATOR_ID,"6014")

        //get current date and time
        val currentDateTime= DateUtils.getDateInSpecificFormat("yyMMddHHmmss")

        //merchant id
        val merchantId= spUtils.getPreference(SpConstants.MERCHANT_ID,1000)

        //station id
        val stationId= spUtils.getPreference(STATION_ID,"0401")

        //equipment id
        val euipId= spUtils.getPreference(EQUIPMENT_ID,"4001")

        //pass type id
        val passTypeId= passCreationData.productType

        //unique pass id
        val passId= currentDateTime+merchantId+stationId+euipId+passTypeId

        //line id
        val lineId= spUtils.getPreference(SpConstants.LINE_ID,"04")

        //create Purchase pass table request for local database
        val passPurchaseTable = PurchasePassTable(
            passId = passId,
            operatorId = operatorId.toString(),
            merchantOrderId = passId,
            merchantId = merchantId.toString(),
            passTypeId = passCreationData.productType,
            passTypeCode = passCreationData.productCode!!,
            totalAmount = passCreationData.amount!!,
            passStatusId = 110,
            passStatusCode = "ACTIVE",
            purchaseDate = passCreationData.startDateTime!!,
            expiryDate = passCreationData.expiryDate!!,
            stationId = stationId,
            equipmentId = euipId,
            fromStation = passCreationData.sourceStationId.toString(),
            toStation = passCreationData.destStationId.toString(),
            zone = passCreationData.zoneId!!,
            lines = lineId.toInt(),
            tripLimit = passCreationData.passLimitValue!!,
            dailyLimit = passCreationData.dailyLimitValue!!,
            paymentMethodId=passCreationData.bankDetail.paymentMethodId,
            bankStan=passCreationData.bankDetail.bankStan!!,
            bankRrn=passCreationData.bankDetail.bankRrn!!,
            bankResponseCode=passCreationData.bankDetail.bankResponseCode!!,
            bankAid=passCreationData.bankDetail.bankAid!!,
            bankCardNumber=passCreationData.bankDetail.bankCardNumber!!,
            bankCardType=passCreationData.bankDetail.bankCardType!!,
            bankMid=passCreationData.bankDetail.bankMid!!,
            bankTid=passCreationData.bankDetail.bankTid!!,
            bankTransactionId=passCreationData.bankDetail.bankTransactionId!!,
            bankReferenceNumber=passCreationData.bankDetail.bankReferenceNumber!!,
            bankIssuerId=passCreationData.bankDetail.bankIssuerId!!,
            acquierBank=passCreationData.bankDetail.acquierBank!!,
            cardScheme=passCreationData.bankDetail.cardScheme!!,
        )

        //request for api call
        val request = PurchasePassRequest(
            operatorId = operatorId,
            passId= passId,
            merchantOrderId = passId,
            merchantId = merchantId,
            passTypeId = passCreationData.productType,
            passTypeCode = passCreationData.productCode!!,
            totalAmount = passCreationData.amount!!,
            passStatusId = 110,
            passStatusCode = "ACTIVE",
            purchaseDate = passCreationData.startDateTime!!,
            expiryDate = passCreationData.expiryDate!!,
            stationId = stationId,
            equipmentId = euipId,
            fromStation = passCreationData.sourceStationId.toString(),
            toStation = passCreationData.destStationId.toString(),
            zone = passCreationData.zoneId!!,
            lines = lineId.toInt(),
            tripLimit = passCreationData.passLimitValue!!,
            dailyLimit = passCreationData.dailyLimitValue!!,
            paymentMethodId=passCreationData.bankDetail.paymentMethodId,
            bankStan=passCreationData.bankDetail.bankStan!!,
            bankRrn=passCreationData.bankDetail.bankRrn!!,
            bankResponseCode=passCreationData.bankDetail.bankResponseCode!!,
            bankAid=passCreationData.bankDetail.bankAid!!,
            bankCardNumber=passCreationData.bankDetail.bankCardNumber!!,
            bankCardType=passCreationData.bankDetail.bankCardType!!,
            bankMid=passCreationData.bankDetail.bankMid!!,
            bankTid=passCreationData.bankDetail.bankTid!!,
            bankTransactionId=passCreationData.bankDetail.bankTransactionId!!,
            bankReferenceNumber=passCreationData.bankDetail.bankReferenceNumber!!,
            bankIssuerId=passCreationData.bankDetail.bankIssuerId!!,
            acquierBank=passCreationData.bankDetail.acquierBank!!,
            cardScheme=passCreationData.bankDetail.cardScheme!!
        )

        runBlocking {
            dbRepository.insertPurchasePassData(passPurchaseTable)
            apiRepository.syncPurchaseData(request)
        }
    }

    /**
     * Method to initiate saving ncmc transction data
     */
    fun saveNcmcTransaction(type:NcmcDataType){

        if(spUtils.getPreference(READER_LOCATION, "EXIT") == ENTRY_SIDE){
            saveEntryTransaction(type)
        }else if(spUtils.getPreference(READER_LOCATION, "EXIT") == EXIT_SIDE){
            saveExitTransaction(type)
        }else if(spUtils.getPreference(READER_LOCATION, "ENTRY_EXIT") == ENTRY_EXIT){
            //TODO change entry exit type gate , make a global variable to handle it
        }
    }

    /**
     * Method to save the entry transaction
     */
    fun saveEntryTransaction(type:NcmcDataType){

        //operator id
        val operatorId = spUtils.getPreference(OPERATOR_ID,"6014")

        //operator name id
        val operatorNameId = spUtils.getPreference(OPERATOR_NAME_ID,1000)

        //station id
        val stationId= spUtils.getPreference(STATION_ID,"0401")

        //equipment id
        val euipId= spUtils.getPreference(EQUIPMENT_ID,"4001")

        //equipment group id
        val equipGroupId= spUtils.getPreference(EQUIPMENT_GROUP_ID,"4")

        //acquirer id
        val acquirerId= spUtils.getPreference(ACQUIRER_ID,"04")

        //terminal id
        val terminalId= spUtils.getPreference(TERMINAL_ID,"401101")

        //line id
        val lineId= spUtils.getPreference(SpConstants.LINE_ID,"04")

        //sequence number
        val seqNo = spUtils.getPreference(SpConstants.TRANSACTION_SEQ_NUMBER,1)

        //trx id
        val trxId:String

        //pan sha
        var panSha:String

        //var card bin
        val cardBin:String

        //product type
        var productType:Int

        //pass start date
        var passStartDate:String = ""

        //pass end date
        var passExpiryDate = ""

        //pass entry station id
        var passEntryStationId =""

        //pass exit station id
        var passExitStationId = ""

        //pass balance
        var passBalance = ""


        //check ncmc trx type
        if(type == NcmcDataType.CSA){

            val operatorIdHexValue= rupayUtils.byteArrayToHex(csaMasterGlobal.csaUpdatedBinData!!.history.getLast()!!.operatorID!!)
            val acquirerIdHexValue= rupayUtils.byteToHex(csaMasterGlobal.csaUpdatedBinData!!.history.getLast()!!.acquirerID!!)
            val terminalIdHexValue= rupayUtils.byteArrayToHex(csaMasterGlobal.csaUpdatedBinData!!.history.getLast()!!.terminalID!!)


            panSha = Utils.bin2hex(rupayUtils.hexStringToByteArray(csaMasterGlobal.bf200Data?.b?.`5A`!!),8)
            trxId = rupayUtils.byteArrayToHex(csaMasterGlobal.csaUpdatedBinData!!.history.getLast()!!.trxDateTime!!)+operatorIdHexValue+acquirerIdHexValue+terminalIdHexValue
            cardBin = csaMasterGlobal.bf200Data?.b?.`5A`!!.slice(0..5)
            productType = PROD_TYPE_SINGLE_JOURNEY.toInt()

        }else{

            val operatorIdHexValue= rupayUtils.byteArrayToHex(osaMasterGlobal.osaUpdatedBinData!!.history.getLast()!!.operatorID!!)
            val acquirerIdHexValue= rupayUtils.byteToHex(osaMasterGlobal.osaUpdatedBinData!!.history.getLast()!!.acquirerID!!)
            val terminalIdHexValue= rupayUtils.byteArrayToHex(osaMasterGlobal.osaUpdatedBinData!!.history.getLast()!!.terminalID!!)

            panSha = Utils.bin2hex(rupayUtils.hexStringToByteArray(osaMasterGlobal.bf200Data?.b?.`5A`!!),8)
            trxId = rupayUtils.byteArrayToHex(osaMasterGlobal.osaUpdatedBinData!!.history.getLast()!!.trxDateTime!!)+operatorIdHexValue+acquirerIdHexValue+terminalIdHexValue
            cardBin = osaMasterGlobal.bf200Data?.b?.`5A`!!.slice(0..5)
            productType = osaMasterGlobal.osaBinData!!.validationData.productType.toInt()

            var passTable:PassTable?=null

            runBlocking {
                passTable = dbRepository.getPassById(productType)
            }

            if(passTable!=null){
                val passList = osaMasterGlobal.osaDisplayData!!.cardPassesList
                for(pass in passList){
                    if(pass.passType.equals(passTable!!.passName)){
                        passStartDate = pass.startDateTime
                        passExpiryDate = pass.endDate
                        passEntryStationId = pass.validEntryStationId
                        passExitStationId = pass.validExitStationId
                        passBalance = pass.passLimit
                    }
                }
            }

        }






        //TODO make values dynamic if require
        //api request for entry trx
        val entryTrxRequest = EntryTrxRequest(
            transactionId = trxId,
            transactionType = BoTrxType.METRO_ENTRY.type,
            transactionSeqNum = euipId.toInt()+seqNo,
            lineId=lineId,
            stationId=stationId,
            equipmentGroupId=equipGroupId,
            equipmentId=euipId,
            aquirerId=acquirerId,
            operatorId=operatorId,
            operatorNameId=operatorNameId,
            terminalId=terminalId,
            cardType=1,
            panSha=panSha,
            productType=productType,
            cardBin=cardBin,
            peakNonPeakTypeId=2,
            businessDate=DateUtils.getDateInSpecificFormat("yyyy-MM-dd")!!,
            transactionDateTime=DateUtils.getDateInSpecificFormat("yyyy-MM-dd HH:mm:ss")!!,
            passStartDate=passStartDate,
            passEndDate=passExpiryDate,
            passStationOne=passEntryStationId,
            passStationTwo=passExitStationId,
            passBalance=passBalance

        )

        //db request for entry trx
        val entryTrxTable = EntryTrxTable(
            transactionId = trxId,
            transactionType =BoTrxType.METRO_ENTRY.type,
            trxSeqNumber = euipId.toInt()+seqNo.toLong(),
            lineId=lineId,
            stationId=stationId,
            equipmentGroupId=equipGroupId,
            equipmentId=euipId,
            aquirerId=acquirerId,
            operatorId=operatorId,
            operatorNameId = operatorNameId,
            terminalId=terminalId,
            cardType=1,
            panSha=panSha,
            productType=productType,
            cardBin=cardBin,
            peakNonPeakTypeId=2,
            businessDate=DateUtils.getDateInSpecificFormat("yyyy-MM-dd")!!,
            transactionDateTime=DateUtils.getDateInSpecificFormat("yyyy-MM-dd HH:mm:ss")!!,
            passStartDate=passStartDate,
            passEndDate=passExpiryDate,
            passStationOne=passEntryStationId,
            passStationTwo=passExitStationId,
            passBalance=passBalance

        )

        runBlocking {
            dbRepository.insertEntryTrx(entryTrxTable)
            apiRepository.syncEntryTrxData(entryTrxRequest)
        }

    }


    /**
     * Method to save the entry transaction
     */
    fun saveExitTransaction(type:NcmcDataType){

        //operator id
        val operatorId = spUtils.getPreference(OPERATOR_ID,"6014")

        //operator name id
        val operatorNameId = spUtils.getPreference(OPERATOR_NAME_ID,1000)

        //station id
        val stationId= spUtils.getPreference(STATION_ID,"0402")

        //equipment id
        val euipId= spUtils.getPreference(EQUIPMENT_ID,"5001")

        //equipment group id
        val equipGroupId= spUtils.getPreference(EQUIPMENT_GROUP_ID,"4")

        //acquirer id
        val acquirerId= spUtils.getPreference(ACQUIRER_ID,"4")

        //terminal id
        val terminalId= spUtils.getPreference(TERMINAL_ID,"402141")

        //line id
        val lineId= spUtils.getPreference(SpConstants.LINE_ID,"04")

        //sequence number
        val seqNo = spUtils.getPreference(TRANSACTION_SEQ_NUMBER,1)

        //bank mid
        val bankMid = spUtils.getPreference(BANK_MID,"Metr33790038971459")

        //bank tid
        val bankTid = spUtils.getPreference(BANK_TID,"11009297")

        //trx id
        val trxId:String

        //pan sha
        var panSha:String

        //var card bin
        val cardBin:String

        //product type
        var productType:Int

        //pass start date
        var passStartDate:String = ""

        //pass end date
        var passExpiryDate = ""

        //pass entry station id
        var passEntryStationId =""

        //pass exit station id
        var passExitStationId = ""

        //pass balance
        var passBalance = ""

        //amount
        var amount:Double
        var cardBalance:Double

        //entry acquirer
        var entryAcquirerId:String =""

        //entry operator id
        var entryOperatorId = ""

        //entry station id
        var entryStationId = ""

        //entry date time
        var entryDateTimeHex =""
        var entryDateTime =""

        //entry terminal id
        var entryTerminalId =""

        //check ncmc trx type
        if(type == NcmcDataType.CSA){

            panSha = Utils.bin2hex(rupayUtils.hexStringToByteArray(csaMasterGlobal.bf200Data?.b?.`5A`!!),8)
            cardBin = csaMasterGlobal.bf200Data?.b?.`5A`!!.slice(0..5)
            productType = PROD_TYPE_SINGLE_JOURNEY.toInt()
            cardBalance = csaMasterGlobal.csaDisplayData!!.cardBalance
            amount = csaMasterGlobal.csaDisplayData!!.cardHistory.get(0).txnAmount.toDouble()

            if(entryDataCache.entryDateTime!=null){
                entryDateTimeHex= entryDataCache.entryDateTime!!
                entryTerminalId= entryDataCache.entryTerminalId!!
                entryAcquirerId= entryDataCache.entryAcquirerId!!
                entryOperatorId= entryDataCache.entryOperatorId!!
                entryDateTime = entryDataCache.entryDateTimeActual!!
            }

            trxId = entryDateTimeHex+entryOperatorId+entryAcquirerId+entryTerminalId

            resetEntryCache(entryDataCache)
        }else{

            panSha = Utils.bin2hex(rupayUtils.hexStringToByteArray(osaMasterGlobal.bf200Data?.b?.`5A`!!),8)
            cardBin = osaMasterGlobal.bf200Data?.b?.`5A`!!.slice(0..5)
            productType = osaMasterGlobal.osaUpdatedBinData!!.history.getLast()!!.productType!!.toInt()
            passBalance = Utils.binToNum(osaMasterGlobal.osaUpdatedBinData!!.history.getLast()!!.passLimit!!,2).toString()

            if(entryDataCache.entryDateTime!=null){
                entryDateTimeHex= entryDataCache.entryDateTime!!
                entryTerminalId= entryDataCache.entryTerminalId!!
                entryAcquirerId= entryDataCache.entryAcquirerId!!
                entryOperatorId= entryDataCache.entryOperatorId!!
                entryDateTime = entryDataCache.entryDateTimeActual!!
            }

            trxId = entryDateTimeHex+entryOperatorId+entryAcquirerId+entryTerminalId

            resetEntryCache(entryDataCache)

            var passTable:PassTable?=null

            runBlocking {
                passTable = dbRepository.getPassById(productType)
            }

            if(passTable!=null){
                val passList = osaMasterGlobal.osaDisplayData!!.cardPassesList
                for(pass in passList){
                    if(pass.passType.equals(passTable!!.passName)){
                        passStartDate = pass.startDateTime
                        passExpiryDate = pass.endDate
                        passEntryStationId = pass.validEntryStationId
                        passExitStationId = pass.validExitStationId
                    }
                }
            }
            cardBalance = 0.0
            amount =0.0

        }






        //TODO make values dynamic if require
        //api request for entry trx
        val exitTrxRequest = ExitTrxRequest(
            transactionId = trxId,
            transactionType = BoTrxType.METRO_EXIT.type,
            txnSequenceNo = euipId.toInt()+seqNo,
            lineId=lineId.toString(),
            stationId=stationId,
            equipmentGroupId=equipGroupId,
            equipmentId=euipId,
            aquirerId=acquirerId,
            operatorId=operatorId,
            operatorNameId= operatorNameId,
            terminalId=terminalId,
            cardType=1,
            panSha=panSha,
            productType=productType,
            cardBin=cardBin,
            businessDate=DateUtils.getDateInSpecificFormat("yyyy-MM-dd")!!,
            transactionDateTime=DateUtils.getDateInSpecificFormat("yyyy-MM-dd HH:mm:ss")!!,
            amount= amount,
            cardBalance = cardBalance,
            passStartDate = passStartDate,
            passEndDate = passExpiryDate,
            passStationOne=passEntryStationId,
            passStationTwo=passExitStationId,
            passBalance=passBalance,
            bankMid =bankTid,
            bankTid =bankMid,
            peakNonPeakTypeId=2,
            entryAquirerId = entryAcquirerId,
            entryDateTime = entryDateTime,
            entryOperatorId = entryOperatorId,
            entryTerminalId = entryTerminalId
            )

        //db request for entry trx
        val exitTrxTable = ExitTrxTable(
            transactionId = trxId,
            transactionType =BoTrxType.METRO_EXIT.type,
            trxSeqNumber = euipId.toInt()+seqNo.toLong(),
            lineId=lineId,
            stationId=stationId,
            equipmentGroupId=equipGroupId,
            equipmentId=euipId,
            aquirerId=acquirerId,
            operatorId=operatorId,
            operatorNameId = operatorNameId,
            terminalId=terminalId,
            cardType=1,
            panSha=panSha,
            productType=1,
            cardBin=cardBin,
            businessDate=DateUtils.getDateInSpecificFormat("yyyy-MM-dd")!!,
            transactionDateTime=DateUtils.getDateInSpecificFormat("yyyy-MM-dd HH:mm:ss")!!,
            amount= amount,
            cardBalance = cardBalance,
            passStartDate = passStartDate,
            passEndDate = passExpiryDate,
            passStationOne=passEntryStationId,
            passStationTwo=passExitStationId,
            passBalance=passBalance,
            bankMid="",
            bankTid="",
            peakNonPeakTypeId=2,
            entryAcquirerId = entryAcquirerId,
            entryDateTime = entryDateTime,
            entryOperatorId = entryOperatorId,
            entryTerminalId = entryTerminalId
        )

        runBlocking {
            dbRepository.insertExitTrx(exitTrxTable)
            apiRepository.syncExitTrxData(exitTrxRequest)
        }

    }

    /**
     * Abort OSA transaction and go for csa transaction
     */
    fun abortOsaTransaction(osaMasterData: OSAMasterData){

        //convert the updated csa bin to byte array
        val osaSent =  rupayUtils.osaBinToByteArray(osaMasterData.osaUpdatedBinData!!)

        //add the header and footer for the update
        val startIndex= osaMasterData.bf200Data?.serviceDataIndex!!
        val endIndex= osaMasterData.bf200Data?.serviceDataIndex!!+95
        var updatedDataWithHeaderFooter=osaMasterData.bf200Data?.serviceRelatedData
        for( i in startIndex..endIndex){
            updatedDataWithHeaderFooter!!.set(i, osaSent[i-startIndex])
        }


        //send back the message
        communicationService.sendData(IPCConstants.MSG_ID_ABORT_OSA_TRANSACTION,updatedDataWithHeaderFooter!!.toByteArray().toHexString())
    }

    /**
     * if OSA transaction aborted at EXIT then set some values before exit
     */
    fun changeCsaValidationAndHistory(csaMasterData: CSAMasterData){

        if(osaMasterGlobal!=null){

        }
    }

    /**
     * method to save previous entry data to create trx id for exit for CSA
     */
    fun saveEntryCacheCsa(csaMasterData: CSAMasterData){
        //save data
        entryDataCache.entryDateTime = rupayUtils.byteArrayToHex(csaMasterData.csaBinData!!.history.getLast()!!.trxDateTime!!)
        entryDataCache.entryTerminalId = rupayUtils.byteArrayToHex(csaMasterData.csaBinData!!.history.getLast()!!.terminalID!!)
        entryDataCache.entryOperatorId = rupayUtils.byteArrayToHex(csaMasterData.csaBinData!!.history.getLast()!!.operatorID!!)
        entryDataCache.entryAcquirerId = rupayUtils.byteToHex(csaMasterData.csaBinData!!.history.getLast()!!.acquirerID!!)
        entryDataCache.entryDateTimeActual = rupayUtils.getDateTimeFromHex(rupayUtils.byteArrayToHex(csaMasterData.csaBinData!!.history.getLast()!!.trxDateTime!!),csaMasterData.bf200Data!!.b.`5F25`!!)
    }

    /**
     * method to save previous entry data to create trx id for exit for CSA
     */
    fun saveEntryCacheOsa(osaMasterData: OSAMasterData){
        //save data
        entryDataCache.entryDateTime = rupayUtils.byteArrayToHex(osaMasterData.osaBinData!!.history.getLast()!!.trxDateTime!!)
        entryDataCache.entryTerminalId = rupayUtils.byteArrayToHex(osaMasterData.osaBinData!!.history.getLast()!!.terminalID!!)
        entryDataCache.entryOperatorId = rupayUtils.byteArrayToHex(osaMasterData.osaBinData!!.history.getLast()!!.operatorID!!)
        entryDataCache.entryAcquirerId = rupayUtils.byteToHex(osaMasterData.osaBinData!!.history.getLast()!!.acquirerID!!)
        entryDataCache.entryDateTimeActual = rupayUtils.getDateTimeFromHex(rupayUtils.byteArrayToHex(osaMasterData.osaBinData!!.history.getLast()!!.trxDateTime!!),osaMasterData.bf200Data!!.b.`5F25`!!)
    }

    /**
     * method to reset entry cache
     */
    fun resetEntryCache(entryDataCache: EntryDataCache){
        entryDataCache.entryDateTime=null
        entryDataCache.entryTerminalId=null
        entryDataCache.entryOperatorId=null
        entryDataCache.entryAcquirerId=null
        entryDataCache.entryDateTimeActual=null
    }

}