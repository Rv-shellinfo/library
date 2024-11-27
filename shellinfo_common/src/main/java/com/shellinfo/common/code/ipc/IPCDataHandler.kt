package com.shellinfo.common.code.ipc

import abbasi.android.filelogger.FileLogger
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shellinfo.IRemoteCallback
import com.shellinfo.IRemoteService
import com.shellinfo.common.code.ShellInfoLibrary
import com.shellinfo.common.code.enums.NcmcDataType
import com.shellinfo.common.data.local.data.emv_rupay.CSAMasterData
import com.shellinfo.common.data.local.data.ipc.BF200Data
import com.shellinfo.common.data.local.data.ipc.ServiceInfo
import com.shellinfo.common.data.local.data.ipc.base.BaseMessage
import com.shellinfo.common.data.local.db.entity.PurchasePassTable
import com.shellinfo.common.di.DefaultMoshi
import com.shellinfo.common.utils.DateUtils
import com.shellinfo.common.utils.IPCConstants
import com.shellinfo.common.utils.IPCConstants.MSG_ID_AMOUNT_REQUEST
import com.shellinfo.common.utils.IPCConstants.MSG_ID_CREATE_OSA_ACK
import com.shellinfo.common.utils.IPCConstants.MSG_ID_CREATE_PASS_ACK
import com.shellinfo.common.utils.IPCConstants.MSG_ID_ICC_DATA
import com.shellinfo.common.utils.IPCConstants.MSG_ID_NO_DATA_ERROR
import com.shellinfo.common.utils.IPCConstants.MSG_ID_PAYMENT_APP_VERSION_DATA
import com.shellinfo.common.utils.IPCConstants.MSG_ID_REMOVE_PENALTY
import com.shellinfo.common.utils.IPCConstants.MSG_ID_STYL_ERROR
import com.shellinfo.common.utils.IPCConstants.MSG_ID_TRX_DATA_EMV
import com.shellinfo.common.utils.IPCConstants.MSG_ID_TRX_DATA_RUPAY_NCMC
import com.shellinfo.common.utils.IPCConstants.MSG_ID_TRX_STATUS_RUPAY_NCMC
import com.shellinfo.common.utils.IPCConstants.STYL_CARD_READ_ERROR
import com.shellinfo.common.utils.IPCConstants.STYL_COMMAND_EXE_FAILED
import com.shellinfo.common.utils.IPCConstants.STYL_EXPIRED_CARD
import com.shellinfo.common.utils.IPCConstants.STYL_INVALID_COMMAND_PARAM
import com.shellinfo.common.utils.IPCConstants.STYL_NOT_ACCEPTED_OUTCOME
import com.shellinfo.common.utils.IPCConstants.STYL_NO_CARD_DETECTED
import com.shellinfo.common.utils.IPCConstants.STYL_NO_ERROR
import com.shellinfo.common.utils.IPCConstants.STYL_NO_RESPONSE
import com.shellinfo.common.utils.IPCConstants.STYL_NO_USB_PERMISSION
import com.shellinfo.common.utils.IPCConstants.STYL_ODA_ERROR_1
import com.shellinfo.common.utils.IPCConstants.STYL_ODA_ERROR_2
import com.shellinfo.common.utils.IPCConstants.STYL_READER_BUSY
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IPCDataHandler @Inject constructor(
    private val rupayDataHandler: RupayDataHandler,
    @DefaultMoshi private val moshi: Moshi
){

    private val _isIpcConnected = MutableLiveData<Boolean>()
    val isIpcConnected: LiveData<Boolean> get() = _isIpcConnected


    //communinaction service stub AIDL
    private var communicationService: IRemoteService? = null

    //TAG Name for logging purpose
    val TAG: String =  IPCDataHandler::class.java.simpleName

    //to check service bounded or not
    private var bound =false

    //application context
    private lateinit var context: Context

    //handler
    private var handler: Handler? = null
    private var connectionRunnable: Runnable? = null
    private var isConnecting = false // Flag to check if the process is already running

    //flag to store requested messageId for NCMC data to make sure handle data accordingly
    private var requestedMsgId = -1



    fun startIPCService(context: Context){

        this.context = context

        //unbind the service if already binded first
        stopIpcService(context)

        //binding ipc service from payment app
        val intent = Intent().apply {
            component = ComponentName("com.shellinfo.paymentapp", "com.shellinfo.paymentapp.ui.services.RemoteService")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE or Context.BIND_ALLOW_ACTIVITY_STARTS)
        }else{
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        Timber.e(TAG,">>>>IPC SERVICE STARTING")

    }

    private val callback = object : IRemoteCallback.Stub() {
        override fun onMessageReceived(messageId:Int,message: String) {
            FileLogger.d(TAG, "Received message from payment service: $message")

            Timber.e(TAG,">>>>MESSAGE RECEIVED FROM THE PAYMENT APPLICATION")

            //handling the message
            handleMessage(messageId,message)

        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            communicationService = IRemoteService.Stub.asInterface(binder)
            rupayDataHandler.setRemoteService(communicationService!!)
            bound=true
            try {

                _isIpcConnected.postValue(true)

                Timber.e(TAG,">>>>REMOTE SERVICE CONNECTION -> SUCCESSFUL")
                // Register the callback to receive messages
                communicationService?.registerCallback(callback)
            } catch (e: RemoteException) {
                Timber.e(TAG,">>>>REMOTE SERVICE CONNECTION ERROR -> REMOTE EXCEPTION")
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound=false
            communicationService = null
            _isIpcConnected.postValue(false)
            stopIpcService(context)
            Timber.e(TAG,">>>>REMOTE SERVICE CONNECTION -> DISCONNECTED")
            startConnection(context)
        }

        override fun onBindingDied(name: ComponentName?) {
            super.onBindingDied(name)
            bound=false
            communicationService = null
            _isIpcConnected.postValue(false)
            Timber.e(TAG,">>>>REMOTE SERVICE CONNECTION BINDING DIED-> DISCONNECTED")
            startConnection(context)
        }

        override fun onNullBinding(name: ComponentName?) {
            super.onNullBinding(name)
            bound=false
            communicationService = null
            _isIpcConnected.postValue(false)
            Timber.e(TAG,">>>>REMOTE SERVICE CONNECTION NULL BINDING-> DISCONNECTED")
            startConnection(context)
        }
    }

    // Method to send a message to Payment App's service
    fun sendMessageToService(messageId:Int, baseMessage: BaseMessage<*>) {

        try {

            val jsonString = when (val data = baseMessage.data) {
                is BF200Data -> convertToJson(moshi,baseMessage as BaseMessage<BF200Data>)
                is ServiceInfo -> convertToJson(moshi,baseMessage as BaseMessage<ServiceInfo>)
                is String -> convertToJson(moshi,baseMessage as BaseMessage<String>)
                is Int -> convertToJson(moshi,baseMessage as BaseMessage<Int>)
                else -> throw IllegalArgumentException("Unsupported data type: ${data?.javaClass}")
            }

            Timber.e(TAG,">>>>MESSAGE SENDING TO PAYMENT APPLICATION")

            try {

                //set requested msg id
                requestedMsgId= messageId

                //send data to payment application
                communicationService?.sendData(messageId,jsonString)
            } catch (e: RemoteException) {

                Timber.e(TAG,">>>>MESSAGE SENDING REMOTE EXCEPTION")
                FileLogger.e("AppB MainActivity", "Failed to send message to service: ${e.message}")
            }

        }catch (ex:Exception){
            Timber.e(TAG,">>>>MESSAGE SENDING OTHER EXCEPTION")
            FileLogger.e("AppB MainActivity", "Failed to send message to service: ${ex.message}")
        }
    }



    fun stopIpcService(context: Context){
        try{

            if(bound) {
                Timber.e(TAG, ">>>>IPC SERVICE STOPPED")
                context.unbindService(serviceConnection)
                bound = false
            }
        }catch (ex:Exception){

            Timber.e(TAG, ">>>>IPC SERVICE STOPPED with EXCEPTION")
        }
    }

    /**
     * Inline method to convert the string to generic type
     */
    inline fun <reified T> convertFromJson(json: String,moshi:Moshi): BaseMessage<T>? {
        return try {
            val type = Types.newParameterizedType(BaseMessage::class.java, T::class.java)
            val adapter = moshi.adapter<BaseMessage<T>>(type)
            adapter.fromJson(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Method to set penalty amount
     */
     fun setPenalty(amount:Double){
        rupayDataHandler.setPenaltyAmount(amount)
    }


    fun handleMessage(messageId:Int,message:String){

        Timber.e(TAG,">>>>RECEIVED MESSAGE HANDLING STARTED FROM HERE WITH MESSAGE: $messageId")

        when(messageId){

            MSG_ID_TRX_DATA_RUPAY_NCMC->{

                Timber.e(TAG,">>>>MESSAGE ID RECEIVED: MSG_ID_TRX_DATA_RUPAY_NCMC")

                // Convert JSON string back to BaseMessage object
                val baseMessage: BaseMessage<BF200Data>? = convertFromJson<BF200Data>(message,moshi)

                Timber.e(TAG,">>>>MESSAGE SENDING FOR :MSG_ID_TRX_DATA_RUPAY_NCMC")
                Timber.e(TAG,">>>>MESSAGE TRANSIT VALIDATION START")


                if (baseMessage != null) {

                    handlePaymentAppMessage(baseMessage)
                }

            }

            MSG_ID_TRX_STATUS_RUPAY_NCMC->{

                // Convert JSON string back to BaseMessage object
                val baseMessage: BaseMessage<String>? = convertFromJson<String>(message,moshi)

                if(baseMessage!!.messageId == STYL_NO_ERROR){
                    rupayDataHandler.saveNcmcTransaction(baseMessage.dataType)

                    //TODO send success to app
                }else{
                    //TODO send error to app
                }
            }

            MSG_ID_NO_DATA_ERROR ->{

                rupayDataHandler.handleError(MSG_ID_NO_DATA_ERROR,"MSG_ID_NO_DATA_ERROR")
            }


            MSG_ID_CREATE_OSA_ACK->{

                // Convert JSON string back to BaseMessage object
                val baseMessage: BaseMessage<String>? = convertFromJson<String>(message,moshi)

                //send the response back to the application
                rupayDataHandler.sendMessageToApp(baseMessage?.messageId,baseMessage?.data)

            }

            MSG_ID_CREATE_PASS_ACK->{

                // Convert JSON string back to BaseMessage object
                val baseMessage: BaseMessage<String>? = convertFromJson<String>(message,moshi)

                if(baseMessage!!.messageId == STYL_NO_ERROR){
                    rupayDataHandler.savePassPurchaseData()

                    //TODO send success to app
                }else{
                    //TODO send error to app
                }
            }

            MSG_ID_STYL_ERROR->{


                // Convert JSON string back to BaseMessage object
                val baseMessage: BaseMessage<String>? = convertFromJson<String>(message,moshi)

                val errorCode= baseMessage?.messageId
                var errorName =""

                when(errorCode){

                    STYL_COMMAND_EXE_FAILED->{
                        errorName="STYL_COMMAND_EXE_FAILED"
                    }

                    STYL_INVALID_COMMAND_PARAM->{
                        errorName="STYL_INVALID_COMMAND_PARAM"
                    }

                    STYL_NO_CARD_DETECTED->{
                        errorName="STYL_NO_CARD_DETECTED"
                    }

                    STYL_NO_RESPONSE->{
                        errorName="STYL_NO_RESPONSE"
                    }

                    STYL_NO_USB_PERMISSION->{
                        errorName="STYL_NO_USB_PERMISSION"
                    }

                    STYL_ODA_ERROR_1->{
                        errorName="STYL_ODA_ERROR_1"
                    }

                    STYL_ODA_ERROR_2->{
                        errorName="STYL_ODA_ERROR_2"
                    }

                    STYL_CARD_READ_ERROR->{
                        errorName="STYL_CARD_READ_ERROR"
                    }

                    STYL_EXPIRED_CARD->{
                        errorName="STYL_EXPIRED_CARD"
                    }

                    STYL_READER_BUSY->{
                        errorName="STYL_READER_BUSY"
                    }

                    STYL_NOT_ACCEPTED_OUTCOME->{
                        errorName="STYL_NOT_ACCEPTED_OUTCOME"
                    }

                    else->{
                        errorName = "UNKNOWN_ERROR"
                    }
                }

                rupayDataHandler.handleError(errorCode!!,errorName)
            }



            MSG_ID_TRX_DATA_EMV->{

            }

            MSG_ID_PAYMENT_APP_VERSION_DATA->{

            }

            MSG_ID_ICC_DATA->{

            }

            MSG_ID_AMOUNT_REQUEST->{

            }

        }


    }


    /**
     * Method to handle the Payment Application message
     */
    fun handlePaymentAppMessage(baseMessage: BaseMessage<*>){

        //log payment message id
        FileLogger.i(TAG, "PAYMENT APP MESSAGE ID :>> ${baseMessage.messageId}")


        when(baseMessage.messageId){

            STYL_NO_ERROR ->{

                Timber.e(TAG,">>>>STYL NO ERROR BLOCK EXECUTED")

                //parse STYL reader data
                val bF200Data = baseMessage.data as BF200Data

                //get the card type
                //TODO for now harcoded value make it dynamic
                //val cardType= bF200Data.b.ci.cardType
                val cardType= IPCConstants.RUPAY_PREPAID

                //based on card type start the flow
                when(cardType){

                    IPCConstants.RUPAY_PREPAID->{

                        Timber.e(TAG,">>>>RUPAY PREPAID BLOCK EXECUTED")

                        //get Rupay data type
                        val dataType = baseMessage.dataType

                        //based on card data type execute
                        when(dataType){
                            NcmcDataType.CSA->{

                                if(ShellInfoLibrary.isForPenalty){

                                    //set flag to false
                                    ShellInfoLibrary.isForPenalty=false

                                    //remove penalty and send back the data to write
                                    rupayDataHandler.removePenalty(bF200Data)

                                }else if(ShellInfoLibrary.isForDataDelete){
                                    ShellInfoLibrary.isForDataDelete=false

                                    rupayDataHandler.deleteCSAData()
                                }else{
                                    //handle ncmc rupay card csa data
                                    rupayDataHandler.handleRupayCardCSAData(bF200Data)
                                }
                            }

                            NcmcDataType.OSA->{

                                //method to handle the NCMC OSA Data
                                rupayDataHandler.handleRupayCardOSAData(bF200Data)

                            }
                            else ->{

                            }
                        }


                    }

                    IPCConstants.MASTER_CARD->{

                    }

                    IPCConstants.VISA_CARD ->{

                    }
                }

            }

        }
    }


    // Generic function to convert BaseMessage<T> to JSON
    inline fun <reified T> convertToJson(moshi: Moshi,baseMessage: BaseMessage<T>): String? {
        return try {
            val type = Types.newParameterizedType(BaseMessage::class.java, T::class.java)
            val adapter = moshi.adapter<BaseMessage<T>>(type)
            adapter.toJson(baseMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Method to reconnect or bound IPC service ,
     * Continues checking in every 5 seconds
     */

     fun startConnection(context: Context){
        this.context = context

        // Check if the process is already running
        if (isConnecting) {
            return // Skip if already running
        }

        isConnecting = true

        // Initialize Handler and Runnable if not already initialized
        if (handler == null) {
            handler = Handler(Looper.getMainLooper())
        }

        if (connectionRunnable == null) {
            connectionRunnable = object : Runnable {
                override fun run() {
                    // Check condition to stop calling
                    if (bound) {
                        handler?.removeCallbacks(this)
                        isConnecting = false // Reset the flag when done
                        return // Exit the runnable
                    }

                    // Your method to call every 5 seconds
                    startIPCService(context)

                    // Call again after 2 seconds
                    handler?.postDelayed(this, 2000)
                }
            }
        }

        // Start the runnable if it's not already running
        handler?.post(connectionRunnable!!)
    }



}