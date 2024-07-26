package com.shellinfo.common.di

import android.content.Context
import com.shellinfo.common.code.mqtt.topic_handler.MqttConfigHandler
import com.shellinfo.common.code.mqtt.topic_handler.MqttDeviceControlHandler
import com.shellinfo.common.code.mqtt.topic_handler.MqttFirmwareHandler
import com.shellinfo.common.code.mqtt.topic_handler.MqttKeyInjectionHandler
import com.shellinfo.common.code.mqtt.topic_handler.MqttLogHandler
import com.shellinfo.common.code.mqtt.topic_handler.MqttOtaHandler
import com.shellinfo.common.code.mqtt.topic_handler.MqttParamsHandler
import com.shellinfo.common.code.mqtt.topic_handler.MqttSpecialModesHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ShellMqttModule {

    @Singleton
    @Provides
    fun provideMqttConfigHandler()= MqttConfigHandler()

    @Singleton
    @Provides
    fun provideMqttDeviceControlHandler()= MqttDeviceControlHandler()

    @Singleton
    @Provides
    fun provideMqttFirmwareHandler()= MqttFirmwareHandler()

    @Singleton
    @Provides
    fun provideMqttKeyInjectionHandler()= MqttKeyInjectionHandler()

    @Singleton
    @Provides
    fun provideMqttLogHandler()= MqttLogHandler()

    @Singleton
    @Provides
    fun provideMqttOtaHandler()= MqttOtaHandler()

    @Singleton
    @Provides
    fun provideMqttParamsHandler()= MqttParamsHandler()

    @Singleton
    @Provides
    fun provideMqttSpecialModesHandler()= MqttSpecialModesHandler()
}