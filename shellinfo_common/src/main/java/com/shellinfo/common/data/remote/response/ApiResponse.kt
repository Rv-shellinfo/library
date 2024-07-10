package com.shellinfo.common.data.remote.response

sealed class ApiResponse<out T> {
    object Loading : ApiResponse<Nothing>()

    object Idle : ApiResponse<Nothing>()

    data class Success<out T>(val data: T) : ApiResponse<T>()

    data class Error(val message: String, val error: Throwable? = null) : ApiResponse<Nothing>()
}