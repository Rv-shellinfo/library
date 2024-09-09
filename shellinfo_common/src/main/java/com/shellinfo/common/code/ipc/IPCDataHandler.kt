package com.shellinfo.common.code.ipc

import abbasi.android.filelogger.FileLogger
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import com.shellinfo.IRemoteCallback
import com.shellinfo.IRemoteService
import com.shellinfo.common.data.local.data.ipc.BF200Data
import com.shellinfo.common.data.local.data.ipc.base.BaseMessage
import com.shellinfo.common.di.DefaultMoshi
import com.shellinfo.common.utils.IPCConstants
import com.shellinfo.common.utils.IPCConstants.PAYMENT_APP_MESSAGE
import com.shellinfo.common.utils.IPCConstants.PAYMENT_MESSAGE
import com.shellinfo.common.utils.IPCConstants.STYL_NO_ERROR
import com.shellinfo.common.utils.IPCConstants.TRANSIT_APP_MESSAGE
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IPCDataHandler @Inject constructor(
    private val rupayDataHandler: RupayDataHandler,
    @DefaultMoshi private val moshi: Moshi
){


    private var communicationService: IRemoteService? = null

    val TAG =  TransitIPCService::class.java.simpleName

    private var bound =false

    fun startIPCService(context: Context){

        //binding ipc service from payment app
        // Bind to the service in App A
        val intent = Intent().apply {
            component = ComponentName("com.shell.paymentapp", "com.shell.paymentapp.ui.services.RemoteService")
        }
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

    }

    private val callback = object : IRemoteCallback.Stub() {
        override fun onMessageReceived(message: String) {
            FileLogger.d(TAG, "Received message from payment service: $message")

            //handling the message
            handleMessage(message)

        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            communicationService = IRemoteService.Stub.asInterface(binder)
            bound=true
            try {
                // Register the callback to receive messages
                communicationService?.registerCallback(callback)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound=false
            communicationService = null
        }
    }

    // Method to send a message to Payment App's service
    fun sendMessageToService(messageId:Int, baseMessage: BaseMessage<*>) {

        //TODO need to convert the data

        communicationService?.sendData(1,"RAVI")
    }



    fun stopIpcService(context: Context){
        if (bound) {
            context.unbindService(serviceConnection)
            bound = false
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


    fun handleMessage(message:String){

        // Convert JSON string back to BaseMessage object
        val baseMessage: BaseMessage<*>? = convertFromJson<BF200Data>(message,moshi)

        if (baseMessage != null) {
            handlePaymentAppMessage(baseMessage)
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

                //parse STYL reader data
                val bF200Data = baseMessage.data as BF200Data

                //get the card type
                val cardType= bF200Data.b.ci.cardType

                //based on card type start the flow
                when(cardType){

                    IPCConstants.RUPAY_PREPAID->{

                        //handle ncmc rupay card data
                        rupayDataHandler.handleRupayCardData(bF200Data)
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


}