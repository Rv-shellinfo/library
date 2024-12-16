package com.shellinfo.common.utils

import abbasi.android.filelogger.FileLogger
import android.content.Intent
import com.shellinfo.common.code.ShellInfoLibrary
import java.io.File
import android.os.Process
import kotlin.system.exitProcess

class GlobalCrashHandler() : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        // Log the exception to the file
        writeExceptionToFile(thread, throwable)

        // Restart the app after logging (optional)
        val intent = Intent(ShellInfoLibrary.globalActivityContext, ShellInfoLibrary.globalActivityContext::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        ShellInfoLibrary.globalActivityContext.startActivity(intent)

        // Kill the current process to avoid the system's default crash dialog
        Process.killProcess(Process.myPid())
        exitProcess(0)

        // Optionally forward the exception to the default handler
        //defaultHandler?.uncaughtException(thread, throwable)
    }

    private fun writeExceptionToFile(thread: Thread, throwable: Throwable) {
        try {

            FileLogger.e("ERROR", "=== Unhandled Exception ===\n")
            FileLogger.e("Exception",throwable)
        } catch (e: Exception) {
            e.printStackTrace() // If logging fails
        }
    }
}