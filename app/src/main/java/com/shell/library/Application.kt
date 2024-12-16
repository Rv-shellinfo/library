package com.shell.library

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.shellinfo.common.code.ShellInfoLibrary
import com.shellinfo.common.code.worker.CustomWorkerFactory
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject


@HiltAndroidApp
class Application : Application(),Configuration.Provider{

    @Inject
    lateinit var shellInfoLibrary: ShellInfoLibrary

    @Inject
    lateinit var workerFactory: HiltWorkerFactory


    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface HiltWorkerFactoryEntryPoint {
        fun workerFactory(): HiltWorkerFactory
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(EntryPoints.get(this, HiltWorkerFactoryEntryPoint::class.java).workerFactory())
            .build()

}