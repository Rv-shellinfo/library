package com.shellinfo.common.di

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Messenger
import com.shellinfo.common.code.NetworkCall
import com.shellinfo.common.code.ipc.IPCDataHandler
import com.shellinfo.common.code.ipc.RupayDataHandler
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.data.remote.repository.ApiRepository
import com.shellinfo.common.data.shared.SharedDataManager
import com.shellinfo.common.utils.ipc.CSAUtils
import com.shellinfo.common.utils.ipc.EMVUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ShellIPCModule {

    @Singleton
    @Provides
    fun provideEmvUtils():EMVUtils{
        return EMVUtils()
    }

    @Singleton
    @Provides
    fun provideCSAUtils(emvUtils: EMVUtils):CSAUtils{
        return CSAUtils(emvUtils)
    }

    @Singleton
    @Provides
    fun provideRupayDataHandler(csaUtils: CSAUtils,sharedPreferenceUtil: SharedPreferenceUtil,sharedDataManager: SharedDataManager,
                                apiRepository: ApiRepository,networkCall: NetworkCall)
    = RupayDataHandler(csaUtils,sharedPreferenceUtil,sharedDataManager,apiRepository,networkCall)

    @Singleton
    @Provides
    fun provideIPCDataHandler(rupayDataHandler: RupayDataHandler)
        = IPCDataHandler(rupayDataHandler)

    @Singleton
    @Provides
    fun providePaymentMessenger(@ApplicationContext context: Context): Messenger {
        lateinit var messenger: Messenger

        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                messenger = Messenger(service)
            }

            override fun onServiceDisconnected(name: ComponentName) {
                // Handle service disconnection
            }
        }

        val intent = Intent().apply {
            component = ComponentName("com.shell.paymentapp", "com.shell.paymentapp.ui.services.PaymentMessengerService")
        }
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)

        // Return the messenger instance
        // Note: This example assumes synchronous binding for simplicity. Handle async properly in production.
        return messenger
    }

}