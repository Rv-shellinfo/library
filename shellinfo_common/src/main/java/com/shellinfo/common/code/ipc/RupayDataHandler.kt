package com.shellinfo.common.code.ipc

import abbasi.android.filelogger.FileLogger
import com.shellinfo.common.code.NetworkCall
import com.shellinfo.common.data.local.data.emv_rupay.CSAMasterData
import com.shellinfo.common.data.local.data.emv_rupay.display.CSADataDisplay
import com.shellinfo.common.data.local.data.ipc.BF200Data
import com.shellinfo.common.data.local.data.ipc.RupayHistory
import com.shellinfo.common.data.local.data.ipc.RupayTrxData
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.data.remote.repository.ApiRepository
import com.shellinfo.common.data.remote.response.ApiResponse
import com.shellinfo.common.data.remote.response.model.entry_validation.EntryValidationRequest
import com.shellinfo.common.data.remote.response.model.entry_validation.EntryValidationResponse
import com.shellinfo.common.data.shared.SharedDataManager
import com.shellinfo.common.utils.DateUtils
import com.shellinfo.common.utils.IPCConstants
import com.shellinfo.common.utils.IPCConstants.AMT_NOT_SUFFICIENT
import com.shellinfo.common.utils.IPCConstants.CARD_ALREADY_TAPPED
import com.shellinfo.common.utils.SpConstants.DOUBLE_TAP_THRESHOLD
import com.shellinfo.common.utils.IPCConstants.ENTRY_NOT_FOUND_CSA
import com.shellinfo.common.utils.IPCConstants.EXIT_NOT_FOUND_CSA
import com.shellinfo.common.utils.IPCConstants.FAILURE_ENTRY_VALIDATION
import com.shellinfo.common.utils.IPCConstants.NO_ERROR
import com.shellinfo.common.utils.IPCConstants.PROD_TYPE_SINGLE_JOURNEY
import com.shellinfo.common.utils.SpConstants.TERMINAL_ID
import com.shellinfo.common.utils.IPCConstants.TIME_EXCEEDED
import com.shellinfo.common.utils.IPCConstants.TXN_STATUS_ENTRY
import com.shellinfo.common.utils.IPCConstants.TXN_STATUS_EXIT
import com.shellinfo.common.utils.IPCConstants.TXN_STATUS_ONE_TAP_TICKET
import com.shellinfo.common.utils.IPCConstants.TXN_STATUS_PENALTY
import com.shellinfo.common.utils.SpConstants.ACQUIRER_ID
import com.shellinfo.common.utils.SpConstants.ENTRY_EXIT
import com.shellinfo.common.utils.SpConstants.ENTRY_EXIT_OVERRIDE
import com.shellinfo.common.utils.SpConstants.ENTRY_SIDE
import com.shellinfo.common.utils.SpConstants.EQUIPMENT_GROUP_ID
import com.shellinfo.common.utils.SpConstants.EQUIPMENT_ID
import com.shellinfo.common.utils.SpConstants.EXIT_SIDE
import com.shellinfo.common.utils.SpConstants.LOGGING_ON_OFF
import com.shellinfo.common.utils.SpConstants.MINIMUM_BALANCE
import com.shellinfo.common.utils.SpConstants.OPERATOR_ID
import com.shellinfo.common.utils.SpConstants.PENALTY_AMOUNT
import com.shellinfo.common.utils.SpConstants.READER_LOCATION
import com.shellinfo.common.utils.Utils
import com.shellinfo.common.utils.ipc.CSAUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
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
    private val csaUtils: CSAUtils,
    private val spUtils: SharedPreferenceUtil,
    private val sharedDataManager: SharedDataManager,
    private val apiRepository: ApiRepository,
    private val networkCall: NetworkCall
){

    private val TAG = RupayDataHandler::class.java.simpleName

    private var lastTapTime: Long = 0

    private val ONE_MINUTE = 60

    //flag to check emergency mode at last station for validation API
    private var isCheckEmergencyMode =false

    // CSA,OSA last entry terminal id
    private var lastEntryTerminalId: String? = null

    // CSA,OSA last entry date time
    private var lastEntryDateTIme: String? = null

    //job to call the api
    private var job: Job? = null

    //Entry exit override
    private var entryExitOverride = false




            /**
     * Handling Rupay NCMC card data, Sent by the Payment Application
     */
    fun handleRupayCardData(bF200Data: BF200Data){

        //parsing csa master data
        var csaMasterData= csaUtils.readCSAData(bF200Data.b.ci.df33!!,bF200Data.b.ci.`5F25`!!)

        //set bf200 data in master data
        csaMasterData.bf200Data = bF200Data

        //check reader location
        when(spUtils.getPreference(READER_LOCATION,"ENTRY")){

            ENTRY_SIDE ->{
                processEntryCSA(csaMasterData)
            }

            EXIT_SIDE ->{
                processExitCSA(csaMasterData)
            }

            ENTRY_EXIT ->{

            }
        }
    }


    /**
     * processEntryCSA : This function will validate the CSA data to permit Entry at Gate
     */
    private fun processEntryCSA(csaMasterData: CSAMasterData){

        //double tap threshold
        val DOUBLE_TAP_THRESHOLD = spUtils.getPreference(DOUBLE_TAP_THRESHOLD,3) // 3 seconds
        entryExitOverride = spUtils.getPreference(ENTRY_EXIT_OVERRIDE,false)

        //get csa raw data
        val csaRawData = csaMasterData.csaRawData

        //get display data
        val csaDataDisplay= csaMasterData.csaDisplayData

        //check general data for version
        if(csaMasterData.csaRawData?.generalData?.version?.toInt()  != IPCConstants.VERSION_NUMBER){
            FileLogger.d(TAG, "Card CSA General Info Version: ${csaMasterData.csaRawData?.generalData?.version}")
        }

        //check general data for language
        if(csaMasterData.csaRawData?.generalData?.langRfu?.toInt()  != IPCConstants.LANG_ENGLISH){
            FileLogger.d(TAG, "Card CSA General Info Language: ${csaMasterData.csaRawData?.generalData?.langRfu}")
        }


        //check for existing error
        if(csaDataDisplay?.errorCode != NO_ERROR){

            FileLogger.e(TAG,"Existing Error : ${csaDataDisplay?.errorCode}")

            //set error code and error message
            csaMasterData.rupayError?.errorCode = csaDataDisplay!!.errorCode
            csaMasterData.rupayError?.errorMessage = csaUtils.getError(csaDataDisplay.errorCode.toString())

            //publish error message
            sharedDataManager.updateCardData(csaMasterData)

            return
        }


        if(csaDataDisplay.txnStatus == TXN_STATUS_ENTRY){

            var trxTimeFromCardEffDate = 0
            var entryTime: Long = 0L
            val currentTime = System.currentTimeMillis() / 1000 // Current time in seconds since epoch


            // Calculate last transaction time from CSA.validationData.trxDateTime
            for (i in 0..2) {
                // Convert each hex character
                val part = csaRawData!!.validationData!!.txnDateTime.substring(i * 2, i * 2 + 2).toInt(16)
                trxTimeFromCardEffDate = (trxTimeFromCardEffDate * 16 * 16) + part
            }

            // Calculate entryTime based on trxTimeFromCardEffDate and cardEffectiveDate
            entryTime = 60 * calculateTrxTimeFromEpoch(csaDataDisplay.cardEffectiveDate, trxTimeFromCardEffDate)
            println("Entry Time: $entryTime")

            if(spUtils.getPreference(LOGGING_ON_OFF,false)){
                FileLogger.d(TAG, "${currentTime} - ${entryTime} = ${currentTime - entryTime} ?? $ONE_MINUTE")

            }


            if (currentTime - lastTapTime < DOUBLE_TAP_THRESHOLD) {
                // Ignore the tap as it happened within the double-tap threshold

                //set error code and error message
                csaMasterData.rupayError?.errorCode = CARD_ALREADY_TAPPED
                csaMasterData.rupayError?.errorMessage = csaUtils.getError("CARD_ALREADY_TAPPED")

                //publish error message
                sharedDataManager.updateCardData(csaMasterData)

                return
            }


            //check entry exit override is off then , enable flag for emergency on last station
            if(!entryExitOverride){

                //last entry date time from card validation data
                lastEntryDateTIme = DateUtils.getTimeInYMDHMS(entryTime)

                //last entry terminal id
                lastEntryTerminalId =  csaRawData?.validationData?.terminalInfo?.terminalId

                //set check emergency mode flag to true (we want to check if on last station emergency mode was activate while validation)
                isCheckEmergencyMode= true

            }

        }


        //API call entry validation API
        val entryValidationRequest = EntryValidationRequest()

        //bin number from tag 5A
        val binNumber = csaMasterData.bf200Data?.b?.ci?.`5A`?.let { Utils.bin2hex(it,8) }
        entryValidationRequest.binNumber= binNumber

        //check if emergency on last station needs to be checked
        if(isCheckEmergencyMode){

            //get hex value from terminal id
            val terminalId= "0"+Utils.bin2hex(lastEntryTerminalId!!,3)
            entryValidationRequest.lastStationId = terminalId
            entryValidationRequest.lastTransactionDateTime = lastEntryDateTIme

        }

        //set current equipmentId and equipmentGroupId
        entryValidationRequest.equipmentId = spUtils.getPreference(EQUIPMENT_ID,"")
        entryValidationRequest.equipmentGroupId = spUtils.getPreference(EQUIPMENT_GROUP_ID,"")


        //TODO call ENTRY VALIDATION API
        job?.cancel() // Cancel the previous job if it's still running

        job = CoroutineScope(Dispatchers.IO).launch {
            try {


                //TODO Call SC or CC API call needs to be change here, right now only cloud call we are doing
                apiRepository.doEntryValidation(entryValidationRequest).collect{

                    when(it){

                        is ApiResponse.Loading->{

                        }

                        is ApiResponse.Success ->{
                            handleEntryValidationResponse(it.data,csaMasterData)
                        }

                        is ApiResponse.Error ->{

                            //set error code and error message
                            csaMasterData.rupayError?.errorCode = FAILURE_ENTRY_VALIDATION
                            csaMasterData.rupayError?.errorMessage = csaUtils.getError("FAILURE_ENTRY_VALIDATION")

                            //publish error message
                            sharedDataManager.updateCardData(csaMasterData)

                            if(spUtils.getPreference(LOGGING_ON_OFF,false)){
                                FileLogger.e(TAG, "Entry Validation API Error Block Code Executed")
                                FileLogger.e(TAG, "Entry Validation : trx Status :${csaRawData?.validationData?.txnStatus}")
                                FileLogger.e(TAG, "Entry Validation : Entry Exit Override :${entryExitOverride}")
                                FileLogger.e(TAG, "Entry Validation : Check Emg Mode :${isCheckEmergencyMode}")
                            }
                        }

                        else ->{

                            if(spUtils.getPreference(LOGGING_ON_OFF,false)){
                                FileLogger.e(TAG, "Entry Validation API ELSE Block Code Executed")
                                FileLogger.e(TAG, "Entry Validation : trx Status :${csaRawData?.validationData?.txnStatus}")
                                FileLogger.e(TAG, "Entry Validation : Entry Exit Override :${entryExitOverride}")
                                FileLogger.e(TAG, "Entry Validation : Check Emg Mode :${isCheckEmergencyMode}")
                            }

                            //set error code and error message
                            csaMasterData.rupayError?.errorCode = FAILURE_ENTRY_VALIDATION
                            csaMasterData.rupayError?.errorMessage = csaUtils.getError("FAILURE_ENTRY_VALIDATION")

                            //publish error message
                            sharedDataManager.updateCardData(csaMasterData)
                        }
                    }
                }
            }catch (e: Exception){



                if(spUtils.getPreference(LOGGING_ON_OFF,false)){
                    FileLogger.e(TAG, "Entry Validation API CATCH Block Code Executed")
                    FileLogger.e(TAG, "Entry Validation : trx Status :${csaRawData?.validationData?.txnStatus}")
                    FileLogger.e(TAG, "Entry Validation : Entry Exit Override :${entryExitOverride}")
                    FileLogger.e(TAG, "Entry Validation : Check Emg Mode :${isCheckEmergencyMode}")
                }

                //set error code and error message
                csaMasterData.rupayError?.errorCode = FAILURE_ENTRY_VALIDATION
                csaMasterData.rupayError?.errorMessage = csaUtils.getError("FAILURE_ENTRY_VALIDATION")

                //publish error message
                sharedDataManager.updateCardData(csaMasterData)

            }
        }

    }

    /**
     * Method to handle the Entry Validation Api Response
     */
    private fun handleEntryValidationResponse(entryValidationResponse: EntryValidationResponse,csaMasterData: CSAMasterData){

        //get api error code
        val apiErrorCode= entryValidationResponse.errorCode

        //if logging ON then print Entry Validation Error Code
        if(spUtils.getPreference(LOGGING_ON_OFF,false)){
            FileLogger.e(TAG, "Entry Validation : errCode :${entryValidationResponse.errorCode}")
        }

        //if api error code is not NO_ERROR then check for the error code
        if(apiErrorCode != NO_ERROR){

            //if error code is Exit Not Found then update the csa validation data txn status
            if(apiErrorCode == EXIT_NOT_FOUND_CSA){

                //updating the txn status
                csaMasterData.csaUpdatedRawData?.validationData?.txnStatus = EXIT_NOT_FOUND_CSA.toString()

                //set error code and error message
                csaMasterData.rupayError?.errorCode = EXIT_NOT_FOUND_CSA
                csaMasterData.rupayError?.errorMessage = csaUtils.getError("EXIT_NOT_FOUND_CSA")

                //publish error message
                sharedDataManager.updateCardData(csaMasterData)
            }
        }

        val lastTrxStatus = csaMasterData.csaUpdatedRawData?.validationData?.txnStatus?.toInt()

        if(((lastTrxStatus == TXN_STATUS_EXIT) || (lastTrxStatus == TXN_STATUS_ONE_TAP_TICKET)
            || (lastTrxStatus == TXN_STATUS_PENALTY) || entryExitOverride || isCheckEmergencyMode)
            &&  apiErrorCode == NO_ERROR){


            //minimum required balance
            val minBalance = spUtils.getPreference(MINIMUM_BALANCE,0)

            //card balance
            val cardBalance= csaMasterData.csaDisplayData?.cardBalance

            //check sufficient balance present
            if(!checkBalanceSufficient(minBalance.toDouble(),cardBalance!!)){

                //set error code and error message
                csaMasterData.rupayError?.errorCode = AMT_NOT_SUFFICIENT
                csaMasterData.rupayError?.errorMessage = csaUtils.getError("AMT_NOT_SUFFICIENT")

                //publish error message
                sharedDataManager.updateCardData(csaMasterData)
            }

            //complete process CSA
            completeProcessCSA(0.0,TXN_STATUS_ENTRY,csaMasterData)


        }else{

            csaMasterData.csaUpdatedRawData?.validationData?.txnStatus = EXIT_NOT_FOUND_CSA.toString()

            //set error code and error message
            csaMasterData.rupayError?.errorCode = EXIT_NOT_FOUND_CSA
            csaMasterData.rupayError?.errorMessage = csaUtils.getError("EXIT_NOT_FOUND_CSA")

            //publish error message
            sharedDataManager.updateCardData(csaMasterData)
        }

    }

    /**
     * Method to check the sufficient balance present or not for entry
     */
    private fun checkBalanceSufficient(requiredBalance:Double,cardBalance: Double):Boolean{

        return cardBalance >= requiredBalance
    }

    /**
     * Method to process exit gate csa data
     */
    private fun processExitCSA(csaMasterData: CSAMasterData){

        val DOUBLE_TAP_THRESHOLD = spUtils.getPreference(DOUBLE_TAP_THRESHOLD,3) // 3 seconds
        val entryExitOverride = spUtils.getPreference(ENTRY_EXIT_OVERRIDE,false)

        var calculatedFare:Double= 0.0
        var entryTime=0
        var errorCode = NO_ERROR

        //get display data
        val csaDataDisplay= csaMasterData.csaDisplayData

        //check for existing error
        if(csaDataDisplay?.errorCode != NO_ERROR){
            //set error code and error message
            csaMasterData.rupayError?.errorCode = csaDataDisplay!!.errorCode
            csaMasterData.rupayError?.errorMessage = csaUtils.getError(csaDataDisplay.errorCode.toString())

            //publish error message
            sharedDataManager.updateCardData(csaMasterData)

            return
        }

        //check double tap for exit transaction
        if(csaDataDisplay.txnStatus == TXN_STATUS_EXIT){

            //handle double tap
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastTapTime < DOUBLE_TAP_THRESHOLD) {
                // Ignore the tap as it happened within the double-tap threshold
                errorCode = CARD_ALREADY_TAPPED

                //set error code and error message
                csaMasterData.rupayError?.errorCode = CARD_ALREADY_TAPPED
                csaMasterData.rupayError?.errorMessage = csaUtils.getError("CARD_ALREADY_TAPPED")

                //publish error message
                sharedDataManager.updateCardData(csaMasterData)
                return
            }

            if(entryExitOverride){

                //TODO check for last station emergency mode as well,
                //TODO check c code as well , no implementation there as well
            }

            // Update the last tap time
            lastTapTime = currentTime

        }

        //check previous transaction status from card
        // if last transaction is entry or penalty or entry exit override mode is on then execute below method
        if(csaDataDisplay.txnStatus == TXN_STATUS_ENTRY || csaDataDisplay.txnStatus == TXN_STATUS_PENALTY || entryExitOverride){

            var result = 0

            //TODO Fare API call

            //TODO Fare API RESULT

            if(result == TIME_EXCEEDED){

                //TODO handle time exceed error and show
                errorCode= TIME_EXCEEDED

            }else if(result == NO_ERROR){

                if(balanceIsSufficient(calculatedFare,csaDataDisplay.cardBalance)){

                    errorCode= AMT_NOT_SUFFICIENT
                    return

                }else{

                    errorCode = NO_ERROR
                }

            }else {

                errorCode = result

            }

        }else{

            errorCode= ENTRY_NOT_FOUND_CSA

            return

        }

    }

    /**
     * Check if balance is sufficient in the card or not
     */
    private fun balanceIsSufficient(calculatedFare:Double,cardBalance:Double):Boolean{

        if(cardBalance >= calculatedFare){
            return true
        }else{
            return false
        }
    }


    private fun completeProcessCSA(fare:Double, txnStatus:Int, csaMasterData: CSAMasterData){

        //error code
        csaMasterData.rupayError?.errorCode = NO_ERROR
        csaMasterData.rupayError?.errorMessage = "NO_ERROR"
        csaMasterData.csaUpdatedRawData?.validationData?.txnStatus = NO_ERROR.toString()

        //product type
        csaMasterData.csaUpdatedRawData?.validationData?.productType = PROD_TYPE_SINGLE_JOURNEY.toString()

        //terminal info
        val acquirerID = spUtils.getPreference(ACQUIRER_ID,"")
        val operatorID = spUtils.getPreference(OPERATOR_ID,"")
        val terminalID = spUtils.getPreference(TERMINAL_ID,"")

        csaMasterData.csaUpdatedRawData?.validationData?.terminalInfo?.acquirerId = acquirerID
        csaMasterData.csaUpdatedRawData?.validationData?.terminalInfo?.terminalId = terminalID
        csaMasterData.csaUpdatedRawData?.validationData?.terminalInfo?.operatorId = operatorID

        //date and time
        val trxTimeFromEpoch: Long = System.currentTimeMillis() / 1000
        val trxTimeFromCardEffDate = calculateTrxTimeFromCardEffectiveDate(csaMasterData.csaDisplayData?.cardEffectiveDate!!,trxTimeFromEpoch)
        Utils.numToBin(csaMasterData.csaUpdatedRawData?.validationData?.txnDateTime!!.toByteArray(),trxTimeFromCardEffDate,3)

        //fare amount (Penalty amount)
        val penaltyAmount= spUtils.getPreference(PENALTY_AMOUNT,0)
        Utils.numToBin(csaMasterData.csaUpdatedRawData?.validationData?.fareAmount!!.toByteArray(),
            (penaltyAmount/10).toLong(),2)

        //transaction status
        csaMasterData.csaUpdatedRawData?.validationData?.txnStatus = txnStatus.toString()

        //History Data
        if(fare>0){

        }else{

        }

    }

    fun updateHistory(fareAmount:Double,globalBalance:Double, csaMasterData: CSAMasterData){


    }


    /**
     * Method to calculate the transaction time
     */
    fun calculateTrxTimeFromCardEffectiveDate(cardEffectiveDate: String, trxTimeFromEpoch: Long): Long {
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
    fun calculateTrxTimeFromEpoch(cardEffectiveDate: String, trxTimeFromCardEffDate: Int): Long {
        val year = cardEffectiveDate.substring(0, 4).toInt()
        val month = cardEffectiveDate.substring(4, 6).toInt()
        val day = cardEffectiveDate.substring(6, 8).toInt()

        // Use LocalDate from ThreeTenABP
        val cardEffDate = LocalDate.of(year, month, day)

        // Convert the date to epoch seconds using ZoneOffset.UTC
        val cardEffDateFromEpoch = cardEffDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC)

        // Calculation in minutes
        return (cardEffDateFromEpoch / 60) + trxTimeFromCardEffDate
    }

    /**
     * Handling Rupay Card Transaction data
     */
    fun handleRupayTrxStatus(rupayTrxData: RupayTrxData){

    }
}