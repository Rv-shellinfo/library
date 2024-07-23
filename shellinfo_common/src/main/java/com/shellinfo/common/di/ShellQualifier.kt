package com.shellinfo.common.di

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