package com.shellinfo.common.di

import android.content.Context
import com.shellinfo.common.utils.SecurityUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ShellEncryptionModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext appContext: Context): Context {
        return appContext
    }

    @Provides
    @Singleton
    fun providePublicEncryption(@ApplicationContext context: Context,): SecurityUtils {
        return SecurityUtils(context)
    }
}