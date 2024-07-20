package com.shell.transitapp.utils.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.shellinfo.common.utils.FtpUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class UploadLogsWorker @AssistedInject constructor(
    private val ftpUtils: FtpUtils,
    @Assisted appContext : Context,
    @Assisted params : WorkerParameters
): CoroutineWorker(appContext,params){

    override suspend fun doWork(): Result {

        Log.e("Worker Started","Worker Started to Upload the Logs")

        return withContext(Dispatchers.IO){
            ftpUtils.uploadFileToSc()
            Result.success()
        }
    }
}