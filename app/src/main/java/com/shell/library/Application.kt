package com.shell.library

import android.app.Application
import com.shellinfo.common.code.ShellInfoLibrary
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class Application: Application() {

    @Inject
    lateinit var shellInfoLibrary: ShellInfoLibrary
}