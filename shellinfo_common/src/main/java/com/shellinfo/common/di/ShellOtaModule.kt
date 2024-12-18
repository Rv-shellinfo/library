package com.shellinfo.common.di

import android.content.Context
import androidx.work.WorkManager
import com.shellinfo.common.code.ota.ApkDownloadWorkerStarter
import com.shellinfo.common.code.ota.OtaInstaller
import com.shellinfo.common.code.ota.OtaReceiver
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ShellOtaModule {

    @Singleton
    @Provides
    fun provideDownloadWorker(
        @ApplicationContext context: Context,
        workManager: WorkManager
    )= ApkDownloadWorkerStarter(context,workManager)

    @Singleton
    @Provides
    fun provideApkInstaller(
        @ApplicationContext context: Context,
        sharedPreferenceUtil: SharedPreferenceUtil
    )= OtaInstaller(context,sharedPreferenceUtil)

    @Singleton
    @Provides
    fun provideApkInstallReceiver(
        otaInstaller: OtaInstaller,
        sharedPreferenceUtil: SharedPreferenceUtil
    )= OtaReceiver(otaInstaller,sharedPreferenceUtil)
}