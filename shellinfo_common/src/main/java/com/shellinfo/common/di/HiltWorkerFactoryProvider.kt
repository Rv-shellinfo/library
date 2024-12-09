package com.shellinfo.common.di

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.WorkerFactory
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

object HiltWorkerFactoryProvider {

    fun getWorkerFactory(context: Context): WorkerFactory {
        return EntryPointAccessors.fromApplication(context, WorkerFactoryEntryPoint::class.java).workerFactory()
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WorkerFactoryEntryPoint {
        fun workerFactory(): HiltWorkerFactory
    }
}