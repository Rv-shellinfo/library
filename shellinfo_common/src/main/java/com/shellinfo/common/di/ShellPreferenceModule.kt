package com.shellinfo.common.di

import android.content.Context
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ShellPreferenceModule {

    @Provides
    @Singleton
    fun provideSharedPreferencesUtil(
        @ApplicationContext context: Context,
    ): SharedPreferenceUtil {
        return SharedPreferenceUtil(context)
    }
}