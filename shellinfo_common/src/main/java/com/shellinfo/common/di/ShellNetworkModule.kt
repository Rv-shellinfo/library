package com.shellinfo.common.di

import android.content.Context
import com.shellinfo.common.BuildConfig
import com.shellinfo.common.code.NetworkCall
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.data.remote.NetworkUtils
import com.shellinfo.common.data.remote.repository.ApiRepository
import com.shellinfo.common.data.local.db.repository.DbRepository
import com.shellinfo.common.data.remote.services.ApiService
import com.shellinfo.common.data.remote.services.provider.ApiServiceProvider
import com.shellinfo.common.data.remote.services.provider.RetrofitClientProvider
import com.squareup.moshi.Moshi
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

    @Singleton
    @Provides
    fun provideMoshi(): Moshi{
        return Moshi.Builder().build()
    }


    @Singleton
    @Provides
    fun provideApiRepository(apiService:ApiService,networkUtils: NetworkUtils,spUtils: SharedPreferenceUtil) : ApiRepository
    {
        return ApiRepository(apiService,networkUtils,spUtils)
    }


    @Singleton
    @Provides
    fun provideNetworkCall(apiRepository: ApiRepository, dbRepository: DbRepository, moshi: Moshi) : NetworkCall{
        return NetworkCall(apiRepository,dbRepository, moshi)
    }

}