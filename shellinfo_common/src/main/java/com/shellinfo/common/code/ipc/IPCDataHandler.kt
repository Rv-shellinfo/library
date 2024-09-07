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
import com.shellinfo.common.data.local.data.ipc.BF200Data
import com.shellinfo.common.BaseMessage
import com.shellinfo.common.utils.IPCConstants
import com.shellinfo.common.utils.IPCConstants.PAYMENT_APP_MESSAGE
import com.shellinfo.common.utils.IPCConstants.PAYMENT_MESSAGE
import com.shellinfo.common.utils.IPCConstants.STYL_NO_ERROR
import com.shellinfo.common.utils.IPCConstants.TRANSIT_APP_MESSAGE
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IPCDataHandler @Inject constructor(
    private val rupayDataHandler: RupayDataHandler
){

    /** IPC DATA **/
    private lateinit var messengerService: Messenger
    private var bound = false

    val TAG =  TransitIPCService::class.java.simpleName

    fun startIPCService(context: Context){

        //binding ipc service from payment app
        val intent = Intent()
        intent.setComponent(ComponentName("com.shell.paymentapp", "com.shell.paymentapp.ui.services.PaymentIPCService"))
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            messengerService = Messenger(service)
            bound = true

            // Send a message to the service
            //sendMessageToService("Hello from AppB!")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }
    }


    fun stopIpcService(context: Context){
        if (bound) {
            context.unbindService(connection)
            bound = false
        }
    }


    inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                IPCConstants.TRANSIT_MESSAGE -> {
                    // Message received from service
                    val bundle = msg.data
                    val baseMessage: BaseMessage<*>? = bundle.getParcelable(TRANSIT_APP_MESSAGE)
                    if (baseMessage != null) {
                        //handlePaymentAppMessage(baseMessage)
                    }

                    //Log.d("AppB MainActivity", "Received response from service: $serviceResponse")
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    /**
     * Method to send the message to Payment Application
     */
    fun sendMessageToPaymentApp(baseMessage: BaseMessage<*>) {
        if (!bound) return

        val msg = Message.obtain(null, PAYMENT_MESSAGE)
        val bundle = Bundle().apply {
            putParcelable(PAYMENT_APP_MESSAGE,baseMessage)
        }
        msg.data = bundle
        msg.replyTo = Messenger(IncomingHandler())
        try {
            messengerService.send(msg)
        } catch (e: RemoteException) {
            Log.e("AppB MainActivity", "Failed to send message to service: ${e.message}")
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