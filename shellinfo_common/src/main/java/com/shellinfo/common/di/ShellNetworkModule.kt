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
import com.shellinfo.common.data.remote.services.provider.NullOrMissingToEmptyStringAdapter
import com.shellinfo.common.data.remote.services.provider.RetrofitClientProvider
import com.shellinfo.common.data.shared.SharedDataManager
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
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

    @LibraryRetrofit
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
    fun provideApiService(@LibraryRetrofit retrofit: Retrofit): ApiService {
        return ApiServiceProvider.getApiService(retrofit)
    }

    @DefaultMoshi
    @Singleton
    @Provides
    fun provideMoshi(): Moshi{
        return Moshi.Builder().add(NullOrMissingToEmptyStringAdapter()).build()
    }




    @Singleton
    @Provides
    fun provideApiRepository(apiService:ApiService,networkUtils: NetworkUtils,spUtils: SharedPreferenceUtil,dbRepository: DbRepository) : ApiRepository
    {
        return ApiRepository(apiService,networkUtils,spUtils,dbRepository)
    }


    @Singleton
    @Provides
    fun provideNetworkCall(apiRepository: ApiRepository, dbRepository: DbRepository,@DefaultMoshi moshi: Moshi, sharedDataManager: SharedDataManager) : NetworkCall{
        return NetworkCall(apiRepository,dbRepository, moshi,sharedDataManager)
    }

}