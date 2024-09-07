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
import com.shellinfo.common.BaseMessage
import com.shellinfo.common.utils.IPCConstants
import javax.inject.Inject

class TransitIPCService: Service() {

    val TAG =  TransitIPCService::class.java.simpleName

    companion object {
        const val MSG_FROM_PAYMENT_APP = 1
        const val MSG_FROM_TRANSIT_APP = 2
    }

    private val messenger = Messenger(IncomingHandler())

    @Inject
    lateinit var ipcDataHandler: IPCDataHandler

    private inner class IncomingHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {

                IPCConstants.PAYMENT_MESSAGE->{

                    // Message received from service
                    val bundle = msg.data
                    bundle.classLoader= BaseMessage::class.java.classLoader

                    try {

                        val baseMessage: BaseMessage<*>? = bundle.getParcelable(IPCConstants.PAYMENT_APP_MESSAGE)
                        if (baseMessage != null) {
                            ipcDataHandler.handlePaymentAppMessage(baseMessage)
                        }
                    }catch (ex:Exception){

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
}