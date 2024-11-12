package com.shellinfo.common.data.remote.services.provider

import com.shellinfo.common.BuildConfig
import com.shellinfo.common.utils.Constants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClientProvider {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    fun getRetrofitBuilder(
        baseUrl: String,
        domainName: String,
    ): Retrofit.Builder {

        val certificatePinner: CertificatePinner = CertificatePinner.Builder()
            .add(domainName, "sha256/${BuildConfig.SSL_FINGERPRINT}")
            .build()

        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(Constants.CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .connectTimeout(Constants.CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .certificatePinner(certificatePinner)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(
                MoshiConverterFactory.create(
                Moshi.Builder()
                    .add(NullOrMissingToEmptyStringAdapter())
                    .add(KotlinJsonAdapterFactory())

                    .build()))
            .client(okHttpClient)
    }
}