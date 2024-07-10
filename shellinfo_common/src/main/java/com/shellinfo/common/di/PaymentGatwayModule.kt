package com.shellinfo.common.di

import com.shellinfo.common.code.NetworkCall
import com.shellinfo.common.code.payment_gateway.CashFreePaymentGateway
import com.shellinfo.common.data.remote.repository.ApiRepository
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PaymentGatewayModule {

    @Provides
    @Singleton
    fun provideCashFreePaymentGateway(apiRepository: ApiRepository,networkCall: NetworkCall,moshi: Moshi):CashFreePaymentGateway{
        return CashFreePaymentGateway(apiRepository,networkCall,moshi)
    }
}