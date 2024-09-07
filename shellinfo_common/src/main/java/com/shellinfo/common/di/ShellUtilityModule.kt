package com.shellinfo.common.di

import android.content.Context
import com.shell.transitapp.utils.workers.LogWorkerStarter
import com.shellinfo.common.code.ConfigMaster
import com.shellinfo.common.code.logs.LoggerImpl
import com.shellinfo.common.code.mqtt.MQTTManager
import com.shellinfo.common.code.mqtt.MqttMessageHandler
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.data.shared.SharedDataManager
import com.shellinfo.common.utils.FtpUtils
import com.shellinfo.common.utils.PermissionsUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilityModule {

    @Singleton
    @Provides
    fun provideConfigMaster()= ConfigMaster()


    @Singleton
    @Provides
    fun provideWorkerStarter(
        @ApplicationContext context: Context,
        sharedPreferenceUtil: SharedPreferenceUtil,
        master: ConfigMaster
    ) = LogWorkerStarter(context,sharedPreferenceUtil,master)



    @Provides
    @Singleton
    fun provideLoggerUtil(sharedPreferenceUtil: SharedPreferenceUtil,logWorkerStarter: LogWorkerStarter,master: ConfigMaster): LoggerImpl {
        return LoggerImpl(sharedPreferenceUtil,logWorkerStarter,master)
    }


    @Provides
    @Singleton
    fun provideFtpUtility(sharedPreferenceUtil: SharedPreferenceUtil,loggerImpl: LoggerImpl, master: ConfigMaster): FtpUtils {
        return FtpUtils(sharedPreferenceUtil,loggerImpl,master)
    }

    @Singleton
    @Provides
    fun provideMqttMessageHandler() = MqttMessageHandler()


    @Singleton
    @Provides
    fun provideMqttManager(configMaster: ConfigMaster,@ApplicationContext context: Context,mqttMessageHandler: MqttMessageHandler)= MQTTManager(configMaster,context,mqttMessageHandler)


    @Singleton
    @Provides
    fun providePermissionUtils():PermissionsUtils{
        return PermissionsUtils()
    }

    @Provides
    fun provideSharedDataManager():SharedDataManager{
        return SharedDataManager()
    }
}