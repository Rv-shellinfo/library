package com.shell.transitapp.utils.workers

import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.utils.SpConstants
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogWorkerStarter @Inject constructor(
    private val context: Context,
    private val spUtils: SharedPreferenceUtil
) {

    //WORK Manager name to upload the logs on the server
    private val WORK_NAME="UPLOAD_LOG_WORK"

    //work manager
    private val workManager = WorkManager.getInstance(context)

    //upload time frequency in minutes default 30 MINS
    private val uploadTimeFrequency = spUtils.getPreference(SpConstants.UPLOAD_TIME_FREQUENCY,"10")

    companion object {
        const val WORK_MANAGER_UPLOAD_LOGS_TAG = "WORK_MANAGER_UPLOAD_LOGS_TAG"
    }

    operator fun invoke(){

        Log.e("Log Upload Worker", "Starting...")

        //cancel previous worker if any
        workManager.cancelUniqueWork(WORK_NAME)

//        //if already running then return
//        if(isWorkerAlreadyRunning()) {
//
//            Log.e("Worker Already Running", "Running returning...")
//
//            return
//        }

        //creating request for periodic worker
        val request = PeriodicWorkRequestBuilder<UploadLogsWorker>(uploadTimeFrequency.toLong(), TimeUnit.MINUTES)
            .addTag(WORK_MANAGER_UPLOAD_LOGS_TAG)
            .build()


        Log.e("Log Upload Worker", "Init Done...")


        workManager.enqueueUniquePeriodicWork(WORK_NAME,ExistingPeriodicWorkPolicy.UPDATE, request)
    }

     fun stopLogWorker(){
        //cancel previous worker if any
        workManager.cancelUniqueWork(WORK_NAME)
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