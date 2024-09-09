package com.shellinfo.common.code.ipc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.shellinfo.IRemoteCallback
import com.shellinfo.IRemoteService
import com.shellinfo.common.di.DefaultMoshi
import com.squareup.moshi.Moshi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IPCCommunicationHandler @Inject constructor(
    private val ipcDataHandler: IPCDataHandler,
    @DefaultMoshi private val moshi: Moshi
){

    private var communicationService: IRemoteService? = null

    fun bindIpcService(context: Context){

        // Bind to the service in App A
        val intent = Intent().apply {
            component = ComponentName("com.shell.app_a", "com.shell.app_a.RemoteService")
        }
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

    }

    fun unBindIpcService(context: Context){
        context.unbindService(serviceConnection)
    }

    private val callback = object : IRemoteCallback.Stub() {
        override fun onMessageReceived(message: String) {
            Log.d("APP_B", "Received message from service: $message")

            sendMessageToService("Shoumik")
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            communicationService = IRemoteService.Stub.asInterface(binder)
            try {
                // Register the callback to receive messages
                communicationService?.registerCallback(callback)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            communicationService = null
        }
    }

    // Example method to send a message to App A's service
    fun sendMessageToService(message: String) {
        communicationService?.sendData(1,"RAVI")
    }
}