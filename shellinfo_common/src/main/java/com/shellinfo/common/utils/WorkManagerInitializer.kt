package com.shellinfo.common.utils

import android.content.Context
import androidx.work.WorkManager
import androidx.work.WorkerFactory

object WorkManagerInitializer {

    fun initialize(context: Context, workerFactory: WorkerFactory) {
        val config = androidx.work.Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

        WorkManager.initialize(context, config)
    }
}