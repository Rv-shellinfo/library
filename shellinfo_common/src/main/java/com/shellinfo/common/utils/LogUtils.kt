package com.shellinfo.common.utils

import abbasi.android.filelogger.FileLogger
import java.io.BufferedReader
import java.io.InputStreamReader

object LogUtils {

    fun getLogcatOutput() {
        val logcatOutput = StringBuilder()

        try {
            // Run the logcat command and capture the output
            val process = Runtime.getRuntime().exec("/system/bin/logcat -v time *:V")
            val reader = BufferedReader(InputStreamReader(process.inputStream))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                logcatOutput.append(line).append("\n")  // Append each log line to the StringBuilder
            }

            reader.close()
            process.waitFor()  // Ensure the process completes
        } catch (e: Exception) {
            e.printStackTrace()
        }

        FileLogger.d("LOG",logcatOutput.toString())
    }
}