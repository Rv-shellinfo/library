package com.shell.transitapp.utils.workers

import abbasi.android.filelogger.FileLogger
import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.shellinfo.common.code.ConfigMaster
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.utils.SpConstants
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogWorkerStarter @Inject constructor(
    private val context: Context,
    private val spUtils: SharedPreferenceUtil,
    private val master: ConfigMaster,
    private val workManager: WorkManager

) {


    //WORK Manager name to upload the logs on the server
    private val WORK_NAME="UPLOAD_LOG_WORK"


    companion object {
        const val WORK_MANAGER_UPLOAD_LOGS_TAG = "WORK_MANAGER_UPLOAD_LOGS_TAG"
    }

    operator fun invoke(){


        FileLogger.d("Log Upload Worker", "Starting...")

        //cancel previous worker if any
        //workManager.cancelUniqueWork(WORK_NAME)

        //creating request for periodic worker
        val request = PeriodicWorkRequestBuilder<UploadLogsWorker>(master.log_frequency.toLong(), TimeUnit.MINUTES)
            .addTag(WORK_MANAGER_UPLOAD_LOGS_TAG)
            .build()


        FileLogger.d("Log Upload Worker", "Init Done...")


        workManager.enqueueUniquePeriodicWork(WORK_NAME,ExistingPeriodicWorkPolicy.KEEP, request)
    }

     fun stopLogWorker(){

         if(isWorkerAlreadyRunning()){

             //cancel worker
             workManager.cancelUniqueWork(WORK_NAME)
         }

    }

    private fun isWorkerAlreadyRunning(): Boolean {
        val workInfo = workManager.getWorkInfosByTag(WORK_MANAGER_UPLOAD_LOGS_TAG).get()
        workInfo.forEach { work ->
            if(work.state == WorkInfo.State.ENQUEUED || work.state == WorkInfo.State.RUNNING)
                return true
        }
        return false
    }
}