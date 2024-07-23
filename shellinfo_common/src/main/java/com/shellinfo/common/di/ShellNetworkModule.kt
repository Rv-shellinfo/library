package com.shellinfo.common.di

import android.content.Context
import com.shellinfo.common.BuildConfig
import com.shellinfo.common.code.NetworkCall
import com.shellinfo.common.data.local.data.mqtt.BaseMessageMqtt
import com.shellinfo.common.data.local.data.mqtt.ConfigUpdateMessage
import com.shellinfo.common.data.local.data.mqtt.DeviceControlMessage
import com.shellinfo.common.data.local.data.mqtt.FirmwareUpdateMessage
import com.shellinfo.common.data.local.data.mqtt.KeyInjectionMessage
import com.shellinfo.common.data.local.data.mqtt.LogStatusMessage
import com.shellinfo.common.data.local.data.mqtt.MqttData
import com.shellinfo.common.data.local.data.mqtt.OtaUpdateMessage
import com.shellinfo.common.data.local.data.mqtt.ParameterMessage
import com.shellinfo.common.data.local.data.mqtt.SpecialModeMessage
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.data.remote.NetworkUtils
import com.shellinfo.common.data.remote.repository.ApiRepository
import com.shellinfo.common.data.local.db.repository.DbRepository
import com.shellinfo.common.data.remote.services.ApiService
import com.shellinfo.common.data.remote.services.provider.ApiServiceProvider
import com.shellinfo.common.data.remote.services.provider.RetrofitClientProvider
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ShellNetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit = RetrofitClientProvider.getRetrofitBuilder(
        BuildConfig.BASE_API_URL, BuildConfig.API_DOMAIN).build()


    @Singleton
    @Provides
    fun provideNetworkUtils(@ApplicationContext context: Context):NetworkUtils{
        return NetworkUtils(context)
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return ApiServiceProvider.getApiService(retrofit)
    }

    @DefaultMoshi
    @Singleton
    @Provides
    fun provideMoshi(): Moshi{
        return Moshi.Builder().build()
    }

    @MqttMoshi
    @Singleton
    @Provides
    fun provideMqttMoshi(): Moshi{
        return Moshi.Builder()
            .add(PolymorphicJsonAdapterFactory.of(MqttData::class.java,"message_id")
                .withSubtype(OtaUpdateMessage::class.java,"OTA_UPDATE")
                .withSubtype(LogStatusMessage::class.java,"LOG_STATUS")
                .withSubtype(ConfigUpdateMessage::class.java,"CONFIG_UPDATE")
                .withSubtype(FirmwareUpdateMessage::class.java,"FIRMWARE_UPDATE")
                .withSubtype(KeyInjectionMessage::class.java,"KEY_INJECTION")
                .withSubtype(DeviceControlMessage::class.java,"DEVICE_CONTROL_COMMANDS")
                .withSubtype(SpecialModeMessage::class.java,"SPECIAL_MODES_COMMANDS")
                .withSubtype(ParameterMessage::class.java,"PARAMETER_TOPICS")
                )
            .build()
    }

    @Provides
    @Singleton
    fun provideMqttMessageAdapter(@MqttMoshi moshi: Moshi): JsonAdapter<BaseMessageMqtt<*>> {
        return moshi.adapter(BaseMessageMqtt::class.java)
    }


    @Singleton
    @Provides
    fun provideApiRepository(apiService:ApiService,networkUtils: NetworkUtils,spUtils: SharedPreferenceUtil) : ApiRepository
    {
        return ApiRepository(apiService,networkUtils,spUtils)
    }


    @Singleton
    @Provides
    fun provideNetworkCall(apiRepository: ApiRepository, dbRepository: DbRepository,@DefaultMoshi moshi: Moshi) : NetworkCall{
        return NetworkCall(apiRepository,dbRepository, moshi)
    }

}