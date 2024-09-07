package com.shellinfo.common.code.ipc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.shellinfo.common.IpcInterface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IpcConnectionHandler @Inject constructor(){

    private var aidlService: IpcInterface? = null
    private var aidlServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i("aidlServiceConnection", "onServiceConnected")
            try {
                aidlService = IpcInterface.Stub.asInterface(service)

                val json = aidlService?.getData()

                aidlService?.sendData(1,null)

                //Log.i("aidlServiceConnection", "Response: $response")
            }catch (e: Exception) {
                Log.i("aidlServiceConnection", "onServiceConnected error: $e")
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i("aidlServiceConnection", "onServiceDisconnected")
            aidlService = null
        }
    }

    fun bindToIpcService(activity: AppCompatActivity){
        val intent = Intent()
        intent.setClassName("com.shell.paymentapp", "com.shell.paymentapp.ui.ipc.IpcService")
        activity.bindService(intent, aidlServiceConnection, Context.BIND_AUTO_CREATE)
    }
}