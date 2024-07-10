package com.shellinfo.common.data.remote.services.provider

import com.shellinfo.common.data.remote.services.ApiService
import retrofit2.Retrofit

object ApiServiceProvider {
    /**
     *  <T> will be ApiService class
     * */
    inline fun <reified T> getGenericApiService(service: T, baseUrl: String, domainName: String): T {
        return RetrofitClientProvider.getRetrofitBuilder(baseUrl, domainName).build().create(T::class.java)
    }

    fun getApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}