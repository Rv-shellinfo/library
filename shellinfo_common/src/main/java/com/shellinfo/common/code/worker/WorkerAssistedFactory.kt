package com.shellinfo.common.code.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

interface WorkerAssistedFactory {
    fun create(context: Context, workerParams: WorkerParameters): ListenableWorker
}
