package com.shellinfo.common.di

import com.squareup.moshi.JsonQualifier
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ShellLibrary


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultMoshi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MqttMoshi

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class NullToDefault