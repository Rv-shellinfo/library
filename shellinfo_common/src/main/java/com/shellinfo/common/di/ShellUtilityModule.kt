package com.shellinfo.common.di

import android.content.Context
import com.shell.transitapp.utils.workers.LogWorkerStarter
import com.shellinfo.common.code.ConfigMaster
import com.shellinfo.common.code.logs.LoggerImpl
import com.shellinfo.common.code.mqtt.MQTTManager
import com.shellinfo.common.code.mqtt.MqttMessageHandler
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.utils.FtpUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object UtilityModule {

    @Singleton
    @Provides
    fun provideConfigMaster()= ConfigMaster()


    @Singleton
    @Provides
    fun provideWorkerStarter(
        @ApplicationContext context: Context,
        sharedPreferenceUtil: SharedPreferenceUtil
    ) = LogWorkerStarter(context,sharedPreferenceUtil)



    @Provides
    @Singleton
    fun provideLoggerUtil(sharedPreferenceUtil: SharedPreferenceUtil,logWorkerStarter: LogWorkerStarter): LoggerImpl {
        return LoggerImpl(sharedPreferenceUtil,logWorkerStarter)
    }


    @Provides
    @Singleton
    fun provideFtpUtility(sharedPreferenceUtil: SharedPreferenceUtil,loggerImpl: LoggerImpl): FtpUtils {
        return FtpUtils(sharedPreferenceUtil,loggerImpl)
    }

    @Singleton
    @Provides
    fun provideMqttMessageHandler() = MqttMessageHandler()


    @Singleton
    @Provides
    fun provideMqttManager(configMaster: ConfigMaster,@ApplicationContext context: Context,mqttMessageHandler: MqttMessageHandler)= MQTTManager(configMaster,context,mqttMessageHandler)



}