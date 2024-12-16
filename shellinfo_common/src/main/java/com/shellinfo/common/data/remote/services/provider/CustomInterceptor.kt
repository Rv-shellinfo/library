package com.shellinfo.common.data.remote.services.provider

import abbasi.android.filelogger.FileLogger
import android.util.Log
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

class CustomInterceptor:HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        // Log to console
        //Timber.d("HTTP_LOG", message)

        FileLogger.d("HTTP_LOG",message)
    }
}