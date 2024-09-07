package com.shell.library

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.shellinfo.common.code.ShellInfoLibrary
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class Application : Application(), Configuration.Provider {

    @Inject
    lateinit var shellInfoLibrary: ShellInfoLibrary

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}