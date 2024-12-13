package com.shellinfo.common.code.worker

import android.annotation.SuppressLint
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.shell.transitapp.utils.workers.UploadLogsWorker
import com.shell.transitapp.utils.workers.UploadLogsWorker_AssistedFactory
import com.shellinfo.common.code.ota.ApkDownloadWorker
import com.shellinfo.common.code.ota.ApkDownloadWorker_AssistedFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomWorkerFactory @Inject constructor(
    private val apkDownloadWorkerFactory: ApkDownloadWorker_AssistedFactory,
    private val uploadlogsworkerAssistedfactory: UploadLogsWorker_AssistedFactory
) : WorkerFactory() {

    @SuppressLint("RestrictedApi")
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            ApkDownloadWorker::class.java.name -> apkDownloadWorkerFactory.create(appContext, workerParameters)
            UploadLogsWorker::class.java.name -> uploadlogsworkerAssistedfactory.create(appContext, workerParameters)
            else -> null  // Return null or handle other workers as needed
        }
    }
}