package com.shellinfo.common.code.ota

import abbasi.android.filelogger.FileLogger
import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApkDownloadWorkerStarter @Inject constructor(
    private val context: Context
){

    companion object {
        const val WORK_MANAGER_DOWNLOAD_APK_TAG = "WORK_MANAGER_DOWNLOAD_APK_TAG"
    }

    //WORK Manager name to upload the logs on the server
    private val WORK_NAME="DOWNLOAD_APK_WORK"

    //work manager
    private val workManager = WorkManager.getInstance(context)


    operator fun invoke(serverPath:String,fileName:String,version:Int){

        FileLogger.d("Apk Download Worker", "Starting...")

        //cancel previous worker if any
        workManager.cancelUniqueWork(WORK_NAME)

        //input data creation
        val inputData= Data.Builder()
            .putString("apkFileName", fileName)
            .putInt("newVersion", version)
            .putString("serverPath", serverPath)
            .build()

        //create work request
        val workRequest = OneTimeWorkRequest.Builder(ApkDownloadWorker::class.java)
            .setInputData(inputData)
            .addTag(WORK_MANAGER_DOWNLOAD_APK_TAG)
            .build()

        //start worker
        WorkManager.getInstance(context).enqueue(workRequest)


    }
}