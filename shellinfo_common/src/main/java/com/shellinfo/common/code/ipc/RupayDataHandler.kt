package com.shellinfo.common.code.ipc

import abbasi.android.filelogger.FileLogger
import android.util.Log
import com.shellinfo.IRemoteService
import com.shellinfo.common.code.NetworkCall
import com.shellinfo.common.code.enums.EquipmentType
import com.shellinfo.common.code.enums.TicketType
import com.shellinfo.common.data.local.data.emv_rupay.CSAMasterData
import com.shellinfo.common.data.local.data.emv_rupay.binary.csa_bin.HistoryBin
import com.shellinfo.common.data.local.data.emv_rupay.display.CSADataDisplay
import com.shellinfo.common.data.local.data.ipc.BF200Data
import com.shellinfo.common.data.local.data.ipc.RupayTrxData
import com.shellinfo.common.data.local.data.ipc.base.BaseMessage
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.data.remote.repository.ApiRepository
import com.shellinfo.common.data.remote.response.ApiResponse
import com.shellinfo.common.data.remote.response.model.entry_validation.EntryValidationRequest
import com.shellinfo.common.data.remote.response.model.entry_validation.EntryValidationResponse
import com.shellinfo.common.data.remote.response.model.fare.FareRequest
import com.shellinfo.common.data.remote.response.model.fare.FareResponse
import com.shellinfo.common.data.remote.response.model.gate_fare.GateFareRequest
import com.shellinfo.common.data.remote.response.model.gate_fare.GateFareResponse
import com.shellinfo.common.data.shared.SharedDataManager
import com.shellinfo.common.utils.DateUtils
import com.shellinfo.common.utils.IPCConstants
import com.shellinfo.common.utils.IPCConstants.AMT_NOT_SUFFICIENT
import com.shellinfo.common.utils.IPCConstants.CARD_ALREADY_TAPPED
import com.shellinfo.common.utils.SpConstants.DOUBLE_TAP_THRESHOLD
import com.shellinfo.common.utils.IPCConstants.ENTRY_NOT_FOUND_CSA
import com.shellinfo.common.utils.IPCConstants.EXIT_NOT_FOUND_CSA
import com.shellinfo.common.utils.IPCConstants.FAILURE_ENTRY_VALIDATION
import com.shellinfo.common.utils.IPCConstants.FAILURE_FARE_CALC
import com.shellinfo.common.utils.IPCConstants.LANGUAGE_MASK
import com.shellinfo.common.utils.IPCConstants.LANG_ENGLISH
import com.shellinfo.common.utils.IPCConstants.MSG_ID_CSA_REQUEST
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
import com.shellinfo.common.utils.SpConstants.ACQUIRER_ID
import com.shellinfo.common.utils.SpConstants.DEVICE_TYPE
import com.shellinfo.common.utils.SpConstants.ENTRY_EXIT
import com.shellinfo.common.utils.SpConstants.ENTRY_EXIT_OVERRIDE
import com.shellinfo.common.utils.SpConstants.ENTRY_SIDE
import com.shellinfo.common.utils.SpConstants.EQUIPMENT_GROUP_ID
import com.shellinfo.common.utils.SpConstants.EQUIPMENT_ID
import com.shellinfo.common.utils.SpConstants.EXIT_SIDE
import com.shellinfo.common.utils.SpConstants.LOGGING_ON_OFF
import com.shellinfo.common.utils.SpConstants.MERCHANT_ID
import com.shellinfo.common.utils.SpConstants.MINIMUM_BALANCE
import com.shellinfo.common.utils.SpConstants.OPERATOR_ID
import com.shellinfo.common.utils.SpConstants.PENALTY_AMOUNT
import com.shellinfo.common.utils.SpConstants.READER_LOCATION
import com.shellinfo.common.utils.SpConstants.STATION_ID
import com.shellinfo.common.utils.Utils
import com.shellinfo.common.utils.ipc.RupayUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset
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
    private val networkCall: NetworkCall
) {

    lateinit var communicationService: IRemoteService

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
    private var entryExitOverride = false

    //card effective date
    private var cardEffectiveDate:String? =null


    /**
     * Method to set the Remote Service
     */
    fun setRemoteService(communicationService: IRemoteService) {
        this.communicationService = communicationService
    }


    /**
     * Method to handle style error
     */
    fun handleStylError(errorCode:Int, errorMessage:String){

        //create csa master data error
        val csaMasterData = CSAMasterData()

        //set error
        csaMasterData.rupayError!!.errorCode= errorCode
        csaMasterData.rupayError!!.errorMessage= errorMessage

        //post value to live data to application
        sharedDataManager.updateCardData(csaMasterData)
    }


    /**
     * Handling Rupay NCMC card data, Sent by the Payment Application
     */
    fun handleRupayCardCSAData(bF200Data: BF200Data) {

        //parsing csa master data
        val csaMasterData = rupayUtils.readCSAData(bF200Data)


        //check device type to handle data request
        val deviceType= spUtils.getPreference(DEVICE_TYPE,"")

        if(deviceType.isNotEmpty()){

            //get enum type from device type
            val equipmentType= EquipmentType.fromEquipment(deviceType)

            when(equipmentType){

                EquipmentType.TR->{

                    //create no error rupay
                    csaMasterData.rupayError?.errorCode = NO_ERROR
                    csaMasterData.rupayError?.errorMessage = "NO_ERROR"

                    //post value to live data
                    sharedDataManager.updateCardData(csaMasterData)

                    //send back to reader with no update
                    communicationService.sendData(MSG_ID_CSA_REQUEST,"ONLY_READ")

                    return
                }
                EquipmentType.TOM->{}
                EquipmentType.VALIDATOR->{

                }
                EquipmentType.TVM->{}
                EquipmentType.PTD->{}
                EquipmentType.ALL->{}
                else -> {}
            }

        }else{

            Log.e(TAG,">>>> DEVICE TYPE NOT SET")
            return

        }


        //calculate card effective date
        cardEffectiveDate= rupayUtils.readCardEffectiveDate(rupayUtils.hexStringToByteArray(bF200Data.b.`5F25`!!))

        //set bf200 data in master data
        csaMasterData.bf200Data = bF200Data


        //check reader location
        when (spUtils.getPreference(READER_LOCATION, "EXIT")) {

            ENTRY_SIDE -> {

                Log.e(TAG, ">>>>ENTRY SIDE CODE EXECUTED")

                processEntryCSA(csaMasterData)
            }

            EXIT_SIDE -> {

                Log.e(TAG, ">>>>EXIT SIDE CODE EXECUTED")
                processExitCSA(csaMasterData)
            }

            ENTRY_EXIT -> {

            }
        }
    }


    /**
     * processEntryCSA : This function will validate the CSA data to permit Entry at Gate
     */
    private fun processEntryCSA(csaMasterData: CSAMasterData) {

        //double tap threshold
        val DOUBLE_TAP_THRESHOLD = spUtils.getPreference(DOUBLE_TAP_THRESHOLD, 3) // 3 seconds
        entryExitOverride = spUtils.getPreference(ENTRY_EXIT_OVERRIDE, false)

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


        Log.e(TAG,">>>>VALIDATION ERROR CODE: ${csaRawData.validationData.errorCode}")


        //check for existing error
        if (csaRawData.validationData.errorCode!!.toInt() != NO_ERROR) {

            FileLogger.e(TAG, "Existing Error : ${csaDataDisplay?.errorCode}")

            //set error code and error message
            csaMasterData.rupayError?.errorCode = csaRawData.validationData.errorCode!!.toInt()
            csaMasterData.rupayError?.errorMessage =
                rupayUtils.getError(csaRawData.validationData.errorCode!!.toString())

            //publish error message
            sharedDataManager.updateCardData(csaMasterData)

            return
        }


        //check last transaction is ENTRY
        if (csaRawData.validationData.trxStatusAndRfu.toInt() == TXN_STATUS_ENTRY) {

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
                csaMasterData.rupayError?.errorCode = CARD_ALREADY_TAPPED
                csaMasterData.rupayError?.errorMessage = rupayUtils.getError("CARD_ALREADY_TAPPED")

                //publish error message
                sharedDataManager.updateCardData(csaMasterData)

                return
            }


            //check entry exit override is off then , enable flag for emergency on last station
            if (!entryExitOverride) {

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
            val terminalId = Utils.bin2hex(lastEntryTerminalId!!, 3)
            entryValidationRequest.lastStationId = terminalId
            entryValidationRequest.lastTransactionDateTime = lastEntryDateTIme

        }

        //set current equipmentId and equipmentGroupId
        entryValidationRequest.equipmentId = spUtils.getPreference(EQUIPMENT_ID, "")
        entryValidationRequest.equipmentGroupId = spUtils.getPreference(EQUIPMENT_GROUP_ID, "")

        Log.e(TAG,">>>> ENTRY VALIDATION DATA: ${networkCall.toJson(entryValidationRequest,EntryValidationRequest::class)}")


        //TODO call ENTRY VALIDATION API
        job?.cancel() // Cancel the previous job if it's still running

        job = CoroutineScope(Dispatchers.IO).launch {
            try {

                //TODO Call SC or CC API call needs to be change here, right now only cloud call we are doing
                apiRepository.doEntryValidation(entryValidationRequest).collect {

                    when (it) {

                        is ApiResponse.Loading -> {

                        }

                        is ApiResponse.Success -> {
                            handleEntryValidationResponse(it.data, csaMasterData)
                        }

                        is ApiResponse.Error -> {

                            //set error code and error message
                            csaMasterData.rupayError?.errorCode = FAILURE_ENTRY_VALIDATION
                            csaMasterData.rupayError?.errorMessage =
                                rupayUtils.getError("FAILURE_ENTRY_VALIDATION")

                            //publish error message
                            sharedDataManager.updateCardData(csaMasterData)

                            if (spUtils.getPreference(LOGGING_ON_OFF, false)) {
                                FileLogger.e(TAG, "Entry Validation API Error Block Code Executed")
                                FileLogger.e(
                                    TAG,
                                    "Entry Validation : trx Status :${extractTrxStatus(csaRawData?.validationData?.trxStatusAndRfu!!)}"
                                )
                                FileLogger.e(
                                    TAG,
                                    "Entry Validation : Entry Exit Override :${entryExitOverride}"
                                )
                                FileLogger.e(
                                    TAG,
                                    "Entry Validation : Check Emg Mode :${isCheckEmergencyMode}"
                                )
                            }
                        }

                        else -> {

                            if (spUtils.getPreference(LOGGING_ON_OFF, false)) {
                                FileLogger.e(TAG, "Entry Validation API ELSE Block Code Executed")
                                FileLogger.e(
                                    TAG,
                                    "Entry Validation : trx Status :${extractTrxStatus(csaRawData?.validationData?.trxStatusAndRfu!!)}"
                                )
                                FileLogger.e(
                                    TAG,
                                    "Entry Validation : Entry Exit Override :${entryExitOverride}"
                                )
                                FileLogger.e(
                                    TAG,
                                    "Entry Validation : Check Emg Mode :${isCheckEmergencyMode}"
                                )
                            }

                            //set error code and error message
                            csaMasterData.rupayError?.errorCode = FAILURE_ENTRY_VALIDATION
                            csaMasterData.rupayError?.errorMessage =
                                rupayUtils.getError("FAILURE_ENTRY_VALIDATION")

                            //publish error message
                            sharedDataManager.updateCardData(csaMasterData)
                        }
                    }
                }
            } catch (e: Exception) {


                if (spUtils.getPreference(LOGGING_ON_OFF, false)) {
                    FileLogger.e(TAG, "Entry Validation API CATCH Block Code Executed")
                    FileLogger.e(
                        TAG,
                        "Entry Validation : trx Status :${csaRawData?.validationData?.trxStatusAndRfu}"
                    )
                    FileLogger.e(
                        TAG,
                        "Entry Validation : Entry Exit Override :${entryExitOverride}"
                    )
                    FileLogger.e(TAG, "Entry Validation : Check Emg Mode :${isCheckEmergencyMode}")
                }

                //set error code and error message
                csaMasterData.rupayError?.errorCode = FAILURE_ENTRY_VALIDATION
                csaMasterData.rupayError?.errorMessage =
                    rupayUtils.getError("FAILURE_ENTRY_VALIDATION")

                //publish error message
                sharedDataManager.updateCardData(csaMasterData)

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
     * Method to SET the Validation data transaction status first four bits
     */
//    fun setTrxStatus(trxStatusAndRfu: Byte, newTrxStatus: Int): Byte {
//        // Mask out the 4 most significant bits (trxStatus) by ANDing with 0x0F (to keep RFU bits intact)
//        val rfuBits = trxStatusAndRfu.toInt() and 0x0F
//
//        // Shift newTrxStatus left by 4 to occupy the 4 most significant bits, and OR with RFU bits
//        return ((newTrxStatus shl 4) or rfuBits).toByte()
//    }

//    fun setTrxStatus(byteValue: Byte): Byte {
//        // Clear the lower 4 bits and set them to 0x10 (00010000)
//        val newByte = (byteValue.toInt() and 0xF0) or 0x01
//        return newByte.toByte()
//    }

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
            if (apiErrorCode.toInt() == EXIT_NOT_FOUND_CSA) {

                //updating the txn status
                csaMasterData.csaUpdatedBinData!!.validationData.trxStatusAndRfu=EXIT_NOT_FOUND_CSA.toByte()
                //setTrxStatus(csaMasterData.csaUpdatedBinData!!.validationData.trxStatusAndRfu,EXIT_NOT_FOUND_CSA)


                //set error code and error message
                csaMasterData.rupayError?.errorCode = EXIT_NOT_FOUND_CSA
                csaMasterData.rupayError?.errorMessage = rupayUtils.getError("EXIT_NOT_FOUND_CSA")

                //publish error message
                sharedDataManager.updateCardData(csaMasterData)

                return
            }
        }

        //last transaction status
        val lastTrxStatus =
            extractTrxStatus(csaMasterData.csaUpdatedBinData!!.validationData.trxStatusAndRfu)

        if (((lastTrxStatus == TXN_STATUS_EXIT) || (lastTrxStatus == TXN_STATUS_ONE_TAP_TICKET)
                    || (lastTrxStatus == TXN_STATUS_PENALTY) || entryExitOverride || isCheckEmergencyMode)
            && apiErrorCode.toInt() == NO_ERROR
        ) {


            //minimum required balance
            val minBalance = spUtils.getPreference(MINIMUM_BALANCE, 0)

            //card balance
            val cardBalance = csaMasterData.csaDisplayData?.cardBalance

            //check sufficient balance present
            if (!checkBalanceSufficient(minBalance.toDouble(), cardBalance!!)) {

                //set error code and error message
                csaMasterData.rupayError?.errorCode = AMT_NOT_SUFFICIENT
                csaMasterData.rupayError?.errorMessage = rupayUtils.getError("AMT_NOT_SUFFICIENT")

                //publish error message
                sharedDataManager.updateCardData(csaMasterData)
            }

            //complete process CSA
            completeProcessCSA(0.0, TXN_STATUS_ENTRY, csaMasterData)


        } else {
            csaMasterData.csaUpdatedBinData!!.validationData.trxStatusAndRfu=EXIT_NOT_FOUND_CSA.toByte()

            //setTrxStatus(csaMasterData.csaUpdatedBinData!!.validationData.trxStatusAndRfu,EXIT_NOT_FOUND_CSA)


            //set error code and error message
            csaMasterData.rupayError?.errorCode = EXIT_NOT_FOUND_CSA
            csaMasterData.rupayError?.errorMessage = rupayUtils.getError("EXIT_NOT_FOUND_CSA")

            //publish error message
            sharedDataManager.updateCardData(csaMasterData)
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
        entryExitOverride = spUtils.getPreference(ENTRY_EXIT_OVERRIDE, false)

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


        Log.e(TAG,">>>>VALIDATION ERROR CODE: ${csaRawData.validationData.errorCode}")


        //check for existing error
        if (csaRawData.validationData.errorCode!!.toInt() != NO_ERROR) {

            FileLogger.e(TAG, "Existing Error : ${csaDataDisplay?.errorCode}")

            //set error code and error message
            csaMasterData.rupayError?.errorCode = csaRawData.validationData.errorCode!!.toInt()
            csaMasterData.rupayError?.errorMessage =
                rupayUtils.getError(csaRawData.validationData.errorCode!!.toString())

            //publish error message
            sharedDataManager.updateCardData(csaMasterData)

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
        val lastTrxStatus = csaMasterData.csaBinData!!.validationData.trxStatusAndRfu

        //check last transaction is ENTRY or EXIT
        if (lastTrxStatus.toInt() == TXN_STATUS_EXIT) {


            //if logging on then print the log and save
            if (spUtils.getPreference(LOGGING_ON_OFF, false)) {
                FileLogger.d(TAG, "${currentTime} - ${entryTime} = ${currentTime - entryTime} ?? $ONE_MINUTE")
            }

            //check current time and entry time difference is less then one minute then show card already tapped
            if ((currentTime - entryTime) < ONE_MINUTE) {
                // Ignore the tap as it happened within the double-tap threshold

                //set error code and error message
                csaMasterData.rupayError?.errorCode = CARD_ALREADY_TAPPED
                csaMasterData.rupayError?.errorMessage = rupayUtils.getError("CARD_ALREADY_TAPPED")

                //publish error message
                sharedDataManager.updateCardData(csaMasterData)

                return
            }

        }


        // last transaction status check condition
        if (((lastTrxStatus.toInt() == TXN_STATUS_ENTRY) || (lastTrxStatus.toInt() == TXN_STATUS_PENALTY) || entryExitOverride)) {

            //entry date time in specific date format
            val entryDateTime = DateUtils.getTimeInYMDHMS(entryTime)

            //get entry terminal id
            val entryTerminalId= rupayUtils.byteArrayToHex(csaMasterData.csaBinData!!.validationData.terminalID)


            //TODO REMOVE HARDCODED VALUE FOR EXIT TRANSACTION
            completeProcessCSA(1.00, TXN_STATUS_EXIT,csaMasterData)

//            //create Fare Request with the data
//            val fareRequest = GateFareRequest()
//            fareRequest.fromStationId=entryTerminalId
//            fareRequest.toStationId=spUtils.getPreference(STATION_ID,"")
//            fareRequest.entryDateTime=entryDateTime
//            fareRequest.exitDateTime=entryDateTime
//            fareRequest.equipmentId=spUtils.getPreference(EQUIPMENT_ID,"")
//            fareRequest.equipmentGroupId=spUtils.getPreference(EQUIPMENT_GROUP_ID,"")
//
//
//            job?.cancel() // Cancel the previous job if it's still running
//
//            job = CoroutineScope(Dispatchers.IO).launch {
//                try {
//
//                    //TODO Call SC or CC API call needs to be change here, right now only cloud call we are doing
//                    apiRepository.doFareCalculation(fareRequest).collect {
//
//                        when (it) {
//
//                            is ApiResponse.Loading -> {
//
//                            }
//
//                            is ApiResponse.Success -> {
//                                handleFareCalculationData(it.data, csaMasterData)
//                            }
//
//                            is ApiResponse.Error -> {
//
//                                //set error code and error message
//                                csaMasterData.rupayError?.errorCode = FAILURE_FARE_CALC
//                                csaMasterData.rupayError?.errorMessage =
//                                    rupayUtils.getError("FAILURE_FARE_CALC")
//
//                                //publish error message
//                                sharedDataManager.updateCardData(csaMasterData)
//
//                                if (spUtils.getPreference(LOGGING_ON_OFF, false)) {
//                                    FileLogger.e(TAG, "Entry Validation API Error Block Code Executed")
//                                    FileLogger.e(TAG, "Entry Validation : trx Status :${extractTrxStatus(csaRawData?.validationData?.trxStatusAndRfu!!)}")
//                                    FileLogger.e(TAG, "Entry Validation : Entry Exit Override :${entryExitOverride}")
//                                    FileLogger.e(TAG, "Entry Validation : Check Emg Mode :${isCheckEmergencyMode}")
//                                }
//                            }
//
//                            else -> {
//
//                                if (spUtils.getPreference(LOGGING_ON_OFF, false)) {
//                                    FileLogger.e(TAG, "Entry Validation API ELSE Block Code Executed")
//                                    FileLogger.e(TAG, "Entry Validation : trx Status :${extractTrxStatus(csaRawData?.validationData?.trxStatusAndRfu!!)}")
//                                    FileLogger.e(TAG, "Entry Validation : Entry Exit Override :${entryExitOverride}")
//                                    FileLogger.e(TAG, "Entry Validation : Check Emg Mode :${isCheckEmergencyMode}")
//                                }
//
//                                //set error code and error message
//                                csaMasterData.rupayError?.errorCode = FAILURE_FARE_CALC
//                                csaMasterData.rupayError?.errorMessage = rupayUtils.getError("FAILURE_FARE_CALC")
//
//                                //publish error message
//                                sharedDataManager.updateCardData(csaMasterData)
//                            }
//                        }
//                    }
//                } catch (e: Exception) {
//
//
//                    if (spUtils.getPreference(LOGGING_ON_OFF, false)) {
//                        FileLogger.e(TAG, "Entry Validation API CATCH Block Code Executed")
//                        FileLogger.e(TAG, "Entry Validation : trx Status :${csaRawData?.validationData?.trxStatusAndRfu}")
//                        FileLogger.e(TAG, "Entry Validation : Entry Exit Override :${entryExitOverride}")
//                        FileLogger.e(TAG, "Entry Validation : Check Emg Mode :${isCheckEmergencyMode}")
//                    }
//
//                    //set error code and error message
//                    csaMasterData.rupayError?.errorCode = FAILURE_FARE_CALC
//                    csaMasterData.rupayError?.errorMessage = rupayUtils.getError("FAILURE_FARE_CALC")
//
//                    //publish error message
//                    sharedDataManager.updateCardData(csaMasterData)
//
//                }
//            }

        }

    }

    private fun handleFareCalculationData(response: GateFareResponse,csaMasterData: CSAMasterData){

        if(response.returnCode == TIME_EXCEEDED){

            //set error code and error message
            csaMasterData.rupayError?.errorCode = TIME_EXCEEDED
            csaMasterData.rupayError?.errorMessage =
                rupayUtils.getError("TIME_EXCEEDED")

            //update the updated csa object for write as well
            csaMasterData.csaUpdatedBinData!!.validationData.errorCode= TIME_EXCEEDED.toByte()

            //publish error message
            sharedDataManager.updateCardData(csaMasterData)

            return
        }else if(response.returnCode == READER_FUNCTIONALITY_DISABLED){

            //set error code and error message
            csaMasterData.rupayError?.errorCode = READER_FUNCTIONALITY_DISABLED
            csaMasterData.rupayError?.errorMessage =
                rupayUtils.getError("READER_FUNCTIONALITY_DISABLED")

            //publish error message
            sharedDataManager.updateCardData(csaMasterData)
            return
        }else if(response.returnCode == NO_ERROR){

            //card balance
            val cardBalance = csaMasterData.csaDisplayData?.cardBalance


            //check balance is sufficient or not
            if(!balanceIsSufficient(response.fare.toDouble(),cardBalance!!)){

                csaMasterData.rupayError?.errorCode = AMT_NOT_SUFFICIENT
                csaMasterData.rupayError?.errorMessage =
                    rupayUtils.getError("AMT_NOT_SUFFICIENT")

                //update the updated csa object for write as well
                csaMasterData.csaUpdatedBinData!!.validationData.errorCode= AMT_NOT_SUFFICIENT.toByte()

                //publish error message
                sharedDataManager.updateCardData(csaMasterData)

                return
            }

            //complete the CSA process
            completeProcessCSA(response.fare.toDouble(),TXN_STATUS_EXIT,csaMasterData)
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


    private fun completeProcessCSA(fare: Double, txnStatus: Int, csaMasterData: CSAMasterData) {

        //error code
        csaMasterData.rupayError?.errorCode = NO_ERROR
        csaMasterData.rupayError?.errorMessage = "NO_ERROR"

        //set transaction status
        csaMasterData.csaUpdatedBinData!!.validationData.trxStatusAndRfu=txnStatus.toByte()


        //product type
        csaMasterData.csaUpdatedBinData!!.validationData.productType = PROD_TYPE_SINGLE_JOURNEY

        //terminal info
        val acquirerID = spUtils.getPreference(ACQUIRER_ID, "01")
        val operatorID = spUtils.getPreference(OPERATOR_ID, "6014")
        val terminalID = spUtils.getPreference(TERMINAL_ID, "001081")

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
        val penaltyAmount = spUtils.getPreference(PENALTY_AMOUNT, 0)
        Utils.numToBin(
            csaMasterData.csaUpdatedBinData?.validationData?.fareAmt!!,
            (penaltyAmount / 10).toLong(), 2
        )


        //History Data
        if (fare > 0) {
            updateHistory(fare, csaMasterData)
        } else {

            //For Entry just copy the existing csa history yo updated one
            csaMasterData.csaUpdatedBinData?.history = csaMasterData.csaBinData!!.history
        }


        //convert the updated csa bin to byte array
        val csaSent =  rupayUtils.csaToByteArray(csaMasterData.csaUpdatedBinData!!)


        //add the header and footer for the update
        val startIndex= csaMasterData.bf200Data?.serviceDataIndex!!
        val endIndex= csaMasterData.bf200Data?.serviceDataIndex!!+95
        var updatedDataWithHeaderFooter=csaMasterData.bf200Data?.serviceRelatedData
        for( i in startIndex..endIndex){
            updatedDataWithHeaderFooter!!.set(i, csaSent[i-startIndex])
        }

        //first remove last two bytes
        updatedDataWithHeaderFooter!!.removeLast()
        updatedDataWithHeaderFooter!!.removeLast()

        // Convert Amount to tag and append to the service data
        val bcdAmount = ByteArray(6)
        rupayUtils.convertAmountToBCD(fare.toLong(), bcdAmount)

        //Add tag 9F02
        updatedDataWithHeaderFooter.add(0x9F.toByte())
        updatedDataWithHeaderFooter.add(0x02.toByte())

        // Append Length 06
        updatedDataWithHeaderFooter.add(0x06.toByte())

        // Append Data (Fare Value)
        for (j in bcdAmount.indices) {
            updatedDataWithHeaderFooter. add(bcdAmount[j])
        }

        // Add two bytes status at the end of the response(which we removed earlier)
        updatedDataWithHeaderFooter.add(0x30.toByte())
        updatedDataWithHeaderFooter.add(0x30.toByte())

        // Change total Length
        var totalLen = updatedDataWithHeaderFooter[1].toInt() or (updatedDataWithHeaderFooter[0].toInt() shl 8)
        totalLen += 9 + 2 // 2 bytes status + 9 byte 9F02

        // Construct 2 byte header lengths
        updatedDataWithHeaderFooter[0] = ((totalLen shr 8) and 0xFF).toByte()
        updatedDataWithHeaderFooter[1] = (totalLen and 0xFF).toByte()


        //send back the message
        communicationService.sendData(MSG_ID_TRANSIT_VALIDATION_RUPAY_NCMC,updatedDataWithHeaderFooter!!.toByteArray().toHexString())

    }


    fun ByteArray.toHexString(): String = joinToString(separator = " ") { "%02x".format(it) }

    fun updateHistory(fareAmount: Double, csaMasterData: CSAMasterData) {

        //create history data
        val historyBin = HistoryBin()

        //set Terminal Information
        historyBin.acquirerID = csaMasterData.csaUpdatedBinData?.validationData?.acquirerID
        historyBin.operatorID = csaMasterData.csaUpdatedBinData?.validationData?.operatorID
        historyBin.terminalID = csaMasterData.csaUpdatedBinData?.validationData?.terminalID

        //transaction date time
        historyBin.trxDateTime = csaMasterData.csaUpdatedBinData?.validationData?.trxDateTime

        //update sequence number
        //TODO transaction sequence number needs to be dynamic
        historyBin.trxSeqNum = rupayUtils.num2bin(1, 2)

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
        historyBin.cardBalance3 = ((cardBalance[2].toUByte().toInt() and 0x0F) shl 4).toByte()

        //transaction status
        historyBin.trxStatus =
            (csaMasterData.csaUpdatedBinData!!.validationData.trxStatusAndRfu.toInt() shr 4).toByte()

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
     * Handling Rupay Card Transaction data
     */
    fun handleRupayTrxStatus(rupayTrxData: RupayTrxData) {

    }


}