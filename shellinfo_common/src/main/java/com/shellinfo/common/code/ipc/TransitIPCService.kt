package com.shellinfo.common.code.ipc

import abbasi.android.filelogger.FileLogger
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import com.shellinfo.common.data.local.data.ipc.BF200Data
import com.shellinfo.common.data.local.data.ipc.base.BaseMessage
import com.shellinfo.common.di.DefaultMoshi
import com.shellinfo.common.utils.IPCConstants
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TransitIPCService: Service() {

    val TAG =  TransitIPCService::class.java.simpleName



    companion object {
        const val MSG_FROM_PAYMENT_APP = 1
        const val MSG_FROM_TRANSIT_APP = 2
    }

    private val messenger = Messenger(IncomingHandler())

    @Inject
    lateinit var ipcDataHandler: IPCDataHandler

    @Inject
    @DefaultMoshi
    lateinit var moshi: Moshi

    private inner class IncomingHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {

                IPCConstants.PAYMENT_MESSAGE->{

                    // Message received from service
                    val bundle = msg.data

                    try {


                        val receivedJson= bundle.getString(IPCConstants.PAYMENT_APP_MESSAGE)

                        // Convert JSON string back to BaseMessage object
                        val baseMessage: BaseMessage<*>? = convertFromJson<BF200Data>(receivedJson!!)


                        if (baseMessage != null) {
                            ipcDataHandler.handlePaymentAppMessage(baseMessage)
                        }
                    }catch (ex:Exception){
                        FileLogger.e(TAG,"Error found in receive message ${ex.message}")
                    }


                }
                MSG_FROM_PAYMENT_APP -> {
                    // Message received from client
                    val clientMessage = msg.data.getString("message")

                    FileLogger.d(TAG, "Received message from payment app: $clientMessage")

                    // Send response back to client
                    val reply = Message.obtain(null, MSG_FROM_TRANSIT_APP)
                    val bundle = Bundle().apply {
                        putString("response", "Hello from AppA!")
                    }
                    reply.data = bundle
                    try {
                        msg.replyTo.send(reply)
                    } catch (e: RemoteException) {
                        FileLogger.d(TAG,"Failed to send reply to client: ${e.message}")
                    }
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        FileLogger.d(TAG, "Shell Lib IPC Service bound")
        return messenger.binder
    }

    // Generic function to convert JSON string back to BaseMessage<T>
    inline fun <reified T> convertFromJson(json: String): BaseMessage<T>? {
        return try {
            val type = Types.newParameterizedType(BaseMessage::class.java, T::class.java)
            val adapter = moshi.adapter<BaseMessage<T>>(type)
            adapter.fromJson(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}