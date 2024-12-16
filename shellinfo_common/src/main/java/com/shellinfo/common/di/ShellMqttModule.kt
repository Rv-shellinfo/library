package com.shellinfo.common.di

import android.content.Context
import com.shellinfo.common.code.ConfigMaster
import com.shellinfo.common.code.mqtt.MQTTManager
import com.shellinfo.common.code.mqtt.MqttMessageHandler
import com.shellinfo.common.code.mqtt.topic_handler.MqttConfigHandler
import com.shellinfo.common.code.mqtt.topic_handler.modes.MqttDeviceControlHandler
import com.shellinfo.common.code.mqtt.topic_handler.MqttFirmwareHandler
import com.shellinfo.common.code.mqtt.topic_handler.MqttKeyInjectionHandler
import com.shellinfo.common.code.mqtt.topic_handler.MqttLogHandler
import com.shellinfo.common.code.mqtt.topic_handler.MqttOtaHandler
import com.shellinfo.common.code.mqtt.topic_handler.MqttParamsHandler
import com.shellinfo.common.code.mqtt.topic_handler.modes.ModeManager
import com.shellinfo.common.code.mqtt.topic_handler.modes.MqttSpecialModesHandler
import com.shellinfo.common.code.ota.ApkDownloadWorkerStarter
import com.shellinfo.common.code.ota.OtaInstaller
import com.shellinfo.common.data.local.data.mqtt.BaseMessageMqtt
import com.shellinfo.common.data.local.data.mqtt.ConfigUpdateMessage
import com.shellinfo.common.data.local.data.mqtt.DeviceControlMessage
import com.shellinfo.common.data.local.data.mqtt.FirmwareUpdateMessage
import com.shellinfo.common.data.local.data.mqtt.KeyInjectionMessage
import com.shellinfo.common.data.local.data.mqtt.LogStatusMessage
import com.shellinfo.common.data.local.data.mqtt.MqttData
import com.shellinfo.common.data.local.data.mqtt.OtaUpdateMessage
import com.shellinfo.common.data.local.data.mqtt.ParameterMessage
import com.shellinfo.common.data.local.data.mqtt.SleDatabaseMessage
import com.shellinfo.common.data.local.data.mqtt.SleDynamicMessage
import com.shellinfo.common.data.local.data.mqtt.SpecialModeMessage
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.data.shared.SharedDataManager
import com.shellinfo.common.utils.FtpUtils
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ShellMqttModule {


    @MqttMoshi
    @Singleton
    @Provides
    fun provideMqttMoshi(): Moshi {
        return Moshi.Builder()
            .add(
                PolymorphicJsonAdapterFactory.of(MqttData::class.java,"type")
                .withSubtype(OtaUpdateMessage::class.java,"OTA_UPDATE")
                .withSubtype(LogStatusMessage::class.java,"LOG_STATUS")
                .withSubtype(ConfigUpdateMessage::class.java,"CONFIG_UPDATE")
                .withSubtype(FirmwareUpdateMessage::class.java,"FIRMWARE_UPDATE")
                .withSubtype(KeyInjectionMessage::class.java,"KEY_INJECTION")
                .withSubtype(DeviceControlMessage::class.java,"DEVICE_CONTROL_COMMAND")
                .withSubtype(SpecialModeMessage::class.java,"SPECIAL_MODE_COMMAND")
                .withSubtype(ParameterMessage::class.java,"PARAMETER")
                .withSubtype(SleDynamicMessage::class.java,"SLE_MESSAGE")
                .withSubtype(SleDatabaseMessage::class.java,"SLE_DATABASE_STATUS")
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideBaseMessageMqttAdapter(@MqttMoshi moshi: Moshi): JsonAdapter<BaseMessageMqtt<MqttData>> {
        val type = Types.newParameterizedType(BaseMessageMqtt::class.java, MqttData::class.java)
        return moshi.adapter(type)
    }

    @Provides
    @Singleton
    fun provideModeManager(sharedPreferenceUtil: SharedPreferenceUtil) = ModeManager(sharedPreferenceUtil)


    @Singleton
    @Provides
    fun provideMqttConfigHandler(sharedDataManager: SharedDataManager)= MqttConfigHandler(sharedDataManager)

    @Singleton
    @Provides
    fun provideMqttDeviceControlHandler(sharedDataManager: SharedDataManager,modeManager: ModeManager)= MqttDeviceControlHandler(sharedDataManager,modeManager)

    @Singleton
    @Provides
    fun provideMqttFirmwareHandler(sharedDataManager: SharedDataManager)= MqttFirmwareHandler(sharedDataManager)

    @Singleton
    @Provides
    fun provideMqttKeyInjectionHandler()= MqttKeyInjectionHandler()

    @Singleton
    @Provides
    fun provideMqttLogHandler(sharedDataManager: SharedDataManager)= MqttLogHandler(sharedDataManager)

    @Singleton
    @Provides
    fun provideMqttOtaHandler(ftpUtils: FtpUtils,otaInstaller: OtaInstaller,
                              apkDownloadWorkerStarter: ApkDownloadWorkerStarter,
                              sharedPreferenceUtil: SharedPreferenceUtil
    )= MqttOtaHandler(ftpUtils,otaInstaller,apkDownloadWorkerStarter,sharedPreferenceUtil)

    @Singleton
    @Provides
    fun provideMqttParamsHandler()= MqttParamsHandler()

    @Singleton
    @Provides
    fun provideMqttSpecialModesHandler(sharedDataManager: SharedDataManager,modeManager: ModeManager)= MqttSpecialModesHandler(sharedDataManager,modeManager)

    @Singleton
    @Provides
    fun provideMqttMessageHandler(sharedPreferenceUtil: SharedPreferenceUtil, mqttOtaHandler: MqttOtaHandler,
                                  mqttSpecialModesHandler: MqttSpecialModesHandler, mqttDeviceControlHandler: MqttDeviceControlHandler,
                                  mqttConfigHandler: MqttConfigHandler, mqttFirmwareHandler: MqttFirmwareHandler,
                                  mqttLogHandler: MqttLogHandler, mqttParamsHandler: MqttParamsHandler,
                                  sharedDataManager: SharedDataManager) =
        MqttMessageHandler(sharedPreferenceUtil,mqttOtaHandler,mqttSpecialModesHandler,mqttDeviceControlHandler,mqttConfigHandler,mqttFirmwareHandler,mqttLogHandler,mqttParamsHandler,sharedDataManager)

    @Singleton
    @Provides
    fun provideMqttManager(configMaster: ConfigMaster,
                           @ApplicationContext context: Context,
                           mqttMessageHandler: MqttMessageHandler,
                           mqttMessageAdapter: JsonAdapter<BaseMessageMqtt<MqttData>>,
                           sharedPreferenceUtil: SharedPreferenceUtil)= MQTTManager(configMaster,context,mqttMessageHandler,mqttMessageAdapter,sharedPreferenceUtil)




}