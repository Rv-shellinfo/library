package com.shellinfo.common.di

import android.content.Context
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
        @ApplicationContext context: Context
    )= ApkDownloadWorkerStarter(context)

    @Singleton
    @Provides
    fun provideApkInstaller(
        @ApplicationContext context: Context
    )= OtaInstaller(context)

    @Singleton
    @Provides
    fun provideApkInstallReceiver(
        otaInstaller: OtaInstaller,
        sharedPreferenceUtil: SharedPreferenceUtil
    )= OtaReceiver(otaInstaller,sharedPreferenceUtil)
}