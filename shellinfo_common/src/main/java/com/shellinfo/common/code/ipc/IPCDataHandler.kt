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
import com.shellinfo.IRemoteCallback
import com.shellinfo.IRemoteService
import com.shellinfo.common.code.enums.NcmcDataType
import com.shellinfo.common.data.local.data.ipc.BF200Data
import com.shellinfo.common.data.local.data.ipc.base.BaseMessage
import com.shellinfo.common.di.DefaultMoshi
import com.shellinfo.common.utils.IPCConstants
import com.shellinfo.common.utils.IPCConstants.MSG_ID_AMOUNT_REQUEST
import com.shellinfo.common.utils.IPCConstants.MSG_ID_ICC_DATA
import com.shellinfo.common.utils.IPCConstants.MSG_ID_PAYMENT_APP_VERSION_DATA
import com.shellinfo.common.utils.IPCConstants.MSG_ID_TRANSIT_VALIDATION_RUPAY_NCMC
import com.shellinfo.common.utils.IPCConstants.MSG_ID_TRX_DATA_EMV
import com.shellinfo.common.utils.IPCConstants.MSG_ID_TRX_DATA_RUPAY_NCMC
import com.shellinfo.common.utils.IPCConstants.MSG_ID_TRX_STATUS_RUPAY_NCMC
import com.shellinfo.common.utils.IPCConstants.STYL_NO_ERROR
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


    private var communicationService: IRemoteService? = null

    val TAG =  IPCDataHandler::class.java.simpleName

    private var bound =false

    private lateinit var context: Context

    private var isFirstTime =true

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

        Log.e(TAG,">>>>IPC SERVICE STARTING")

    }

    private val callback = object : IRemoteCallback.Stub() {
        override fun onMessageReceived(messageId:Int,message: String) {
            FileLogger.d(TAG, "Received message from payment service: $message")

            Log.e(TAG,">>>>MESSAGE RECEIVED FROM THE PAYMENT APPLICATION")

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

                Log.e(TAG,">>>>REMOTE SERVICE CONNECTION -> SUCCESSFUL")
                // Register the callback to receive messages
                communicationService?.registerCallback(callback)
            } catch (e: RemoteException) {
                Log.e(TAG,">>>>REMOTE SERVICE CONNECTION ERROR -> REMOTE EXCEPTION")
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound=false
            communicationService = null
            rupayDataHandler.setRemoteService(communicationService!!)
            stopIpcService(context)
            Log.e(TAG,">>>>REMOTE SERVICE CONNECTION -> DISCONNECTED")
            startConnection(context)
        }

        override fun onBindingDied(name: ComponentName?) {
            super.onBindingDied(name)
            bound=false
            communicationService = null
            Log.e(TAG,">>>>REMOTE SERVICE CONNECTION BINDING DIED-> DISCONNECTED")
            startConnection(context)
        }

        override fun onNullBinding(name: ComponentName?) {
            super.onNullBinding(name)
            bound=false
            communicationService = null
            Log.e(TAG,">>>>REMOTE SERVICE CONNECTION NULL BINDING-> DISCONNECTED")
            startConnection(context)
        }
    }

    // Method to send a message to Payment App's service
    fun sendMessageToService(messageId:Int, baseMessage: BaseMessage<*>) {

        try {

            val jsonString = when (val data = baseMessage.data) {
                is BF200Data -> convertToJson(moshi,baseMessage as BaseMessage<BF200Data>)
                is String -> convertToJson(moshi,baseMessage as BaseMessage<String>)
                is Int -> convertToJson(moshi,baseMessage as BaseMessage<Int>)
                else -> throw IllegalArgumentException("Unsupported data type: ${data?.javaClass}")
            }

            Log.e(TAG,">>>>MESSAGE SENDING TO PAYMENT APPLICATION")

            try {
                communicationService?.sendData(messageId,jsonString)
            } catch (e: RemoteException) {

                Log.e(TAG,">>>>MESSAGE SENDING REMOTE EXCEPTION")
                FileLogger.e("AppB MainActivity", "Failed to send message to service: ${e.message}")
            }

        }catch (ex:Exception){
            Log.e(TAG,">>>>MESSAGE SENDING OTHER EXCEPTION")
            FileLogger.e("AppB MainActivity", "Failed to send message to service: ${ex.message}")
        }
    }



    fun stopIpcService(context: Context){
        try{

            if(bound) {
                Log.e(TAG, ">>>>IPC SERVICE STOPPED")
                context.unbindService(serviceConnection)
                bound = false
            }
        }catch (ex:Exception){

            Log.e(TAG, ">>>>IPC SERVICE STOPPED with EXCEPTION")
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


    fun handleMessage(messageId:Int,message:String){

        Log.e(TAG,">>>>RECEIVED MESSAGE HANDLING STARTED FROM HERE WITH MESSAGE: $messageId")

        when(messageId){

            MSG_ID_TRX_DATA_RUPAY_NCMC->{

                Log.e(TAG,">>>>MESSAGE ID RECEIVED: MSG_ID_TRX_DATA_RUPAY_NCMC")

                // Convert JSON string back to BaseMessage object
                val baseMessage: BaseMessage<BF200Data>? = convertFromJson<BF200Data>(message,moshi)

                Log.e(TAG,">>>>MESSAGE SENDING FOR :MSG_ID_TRX_DATA_RUPAY_NCMC")
                Log.e(TAG,">>>>MESSAGE TRANSIT VALIDATION START")


//                //TESTING CSA WRITE
//                var csaData= baseMessage?.data?.serviceRelatedData!!
//
//                Timber.tag("PREVIOUS VALUE>>>>>").e(csaData[27].toString())
//                Timber.tag("PREVIOUS VALUE>>>>>").e(csaData[28].toString())
//
//                baseMessage.data.serviceRelatedData = csaData
//
//                for (i in 35..120) {
//                    csaData[i] = 0x01
//                }
//                Timber.tag("AFTER VALUE>>>>>").e(baseMessage.data.serviceRelatedData!![27].toString())
//                Timber.tag("AFTER VALUE>>>>>").e(baseMessage.data.serviceRelatedData!![28].toString())


                //sendMessageToService(MSG_ID_TRANSIT_VALIDATION_RUPAY_NCMC,baseMessage)

                if (baseMessage != null) {
                    handlePaymentAppMessage(baseMessage)
                }



            }

            MSG_ID_TRX_STATUS_RUPAY_NCMC->{

                Toast.makeText(context,"GOOD JOB",Toast.LENGTH_LONG).show()
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

                Log.e(TAG,">>>>STYL NO ERROR BLOCK EXECUTED")

                //parse STYL reader data
                val bF200Data = baseMessage.data as BF200Data

                //get the card type
                //TODO for now harcoded value make it dynamic
                //val cardType= bF200Data.b.ci.cardType
                val cardType= IPCConstants.RUPAY_PREPAID

                //based on card type start the flow
                when(cardType){

                    IPCConstants.RUPAY_PREPAID->{

                        Log.e(TAG,">>>>RUPAY PREPAID BLOCK EXECUTED")

                        //get Rupay data type
                        val dataType = baseMessage.dataType

                        //based on card data type execute
                        when(dataType){
                            NcmcDataType.CSA->{

                                //handle ncmc rupay card csa data
                                rupayDataHandler.handleRupayCardCSAData(bF200Data)
                            }

                            NcmcDataType.OSA->{

                            }
                        }


                    }

                    IPCConstants.MASTER_CARD->{

                    }

                    IPCConstants.VISA_CARD ->{

                    }
                }

            }

            else ->{

                //TODO handle error message, and display error
            }

        }


//        MSG_ID_TRX_DATA_RUPAY_NCMC->{
//            val rupayData: RupayCardDataRead = baseMessage.data as RupayCardDataRead
//
//            when(rupayData.errorCode){
//                STYL_NO_ERROR ->{
//                    rupayDataHandler.handleRupayCardData(rupayData)
//                }
//
//                else ->{
//                    //TODO show error on the screen based on the error code
//                }
//            }
//
//        }
//
//        MSG_ID_TRX_STATUS_RUPAY_NCMC -> {
//
//        }
//
//        MSG_ID_TRX_DATA_EMV-> {
//
//        }
//
//        MSG_ID_PAYMENT_APP_VERSION_DATA->{
//
//        }
//
//        MSG_ID_ICC_DATA ->{
//
//        }
//
//        MSG_ID_AMOUNT_REQUEST-> {
//
//        }
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
        this.context=context

        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                // Check condition to stop calling
                if (bound) {
                    handler.removeCallbacks(this)
                    return // Exit the runnable
                }

                // Your method to call every 5 seconds
                startIPCService(context)

                // Call again after 5 seconds
                handler.postDelayed(this, 2000)
            }
        }

        handler.post(runnable)
    }
}