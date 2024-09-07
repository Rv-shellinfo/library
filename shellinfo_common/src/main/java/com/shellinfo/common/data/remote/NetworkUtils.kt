package com.shellinfo.common.data.remote

import android.content.Context
import com.shellinfo.common.R
import com.shellinfo.common.data.remote.response.ApiResponse
import com.shellinfo.common.data.remote.response.model.payment_gateway.cash_free.CashFreePaymentResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Response
import javax.inject.Inject

class NetworkUtils @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun <T> handleApiCall(apiCall: suspend () -> Response<T>): ApiResponse<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let {
                    ApiResponse.Success(it)
                } ?: ApiResponse.Error("No Data")
            } else {
                ApiResponse.Error(response.message())
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.localizedMessage ?: "Server Error Please Try After Sometime", e)
        }
    }
}