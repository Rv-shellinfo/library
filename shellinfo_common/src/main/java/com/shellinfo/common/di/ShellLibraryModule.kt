package com.shellinfo.common.di

import android.content.Context
import com.shellinfo.common.code.DatabaseCall
import com.shellinfo.common.code.NetworkCall
import com.shellinfo.common.code.ShellInfoLibrary
import com.shellinfo.common.code.ipc.IPCDataHandler
import com.shellinfo.common.code.logs.LoggerImpl
import com.shellinfo.common.code.mqtt.MQTTManager
import com.shellinfo.common.code.mqtt.topic_handler.modes.ModeManager
import com.shellinfo.common.code.worker.CustomWorkerFactory
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.data.shared.SharedDataManager
import com.shellinfo.common.utils.BarcodeUtils
import com.shellinfo.common.utils.PermissionsUtils
import com.shellinfo.common.utils.UsbDeviceConnectionHandler
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LibraryModule {

    @Provides
    @Singleton
    fun provideLibrary(
        @ApplicationContext context: Context,
        spUtils: SharedPreferenceUtil,
        networkCall: NetworkCall,
        databaseCall: DatabaseCall,
        barcodeUtils: BarcodeUtils,
        loggerImpl: LoggerImpl,
        mqttManager: MQTTManager,
        ipcDataHandler: IPCDataHandler,
        permissionsUtils: PermissionsUtils,
        sharedDataManager: SharedDataManager,
        modeManager: ModeManager,
        factory: CustomWorkerFactory,
        usbDeviceConnectionHandler: UsbDeviceConnectionHandler
    ):ShellInfoLibrary{
        return ShellInfoLibrary(context,spUtils,networkCall,databaseCall,barcodeUtils,loggerImpl,mqttManager,ipcDataHandler,permissionsUtils,sharedDataManager,modeManager,factory,usbDeviceConnectionHandler)
    }


    @Provides
    @Singleton
    fun provideBarCodeUtils(@DefaultMoshi moshi: Moshi):BarcodeUtils{
        return BarcodeUtils(moshi)
    }
}