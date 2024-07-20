package com.shellinfo.common.code.logs

import abbasi.android.filelogger.FileLogger
import abbasi.android.filelogger.config.Config
import android.os.Build
import android.os.Environment
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.shell.transitapp.utils.workers.LogWorkerStarter
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.utils.SpConstants
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class LoggerImpl @Inject constructor(
    private val sharedPreferenceUtil: SharedPreferenceUtil,
    private val logWorkerStarter: LogWorkerStarter
):LoggerInterface, Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun initLogger() {

        //time stamp
        val timestampFormat = SimpleDateFormat("dd-MM-yyyy-HH:mm:ss", Locale.US)

        //device serial
        val deviceSerial = Build.SERIAL

        //current date
        val currentDate = timestampFormat.format(Date())

        //log file name to create
        //val logFileName = "log_${deviceSerial}_${currentDate}.txt"

        //creating directory
        val directoryPath = Environment.getExternalStorageDirectory().absolutePath + "/transit_app_log"

        // Create the directory if it doesn't exist
        val directory = File(directoryPath)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        //saving the file name in the preferences
        sharedPreferenceUtil.savePreference(SpConstants.LOCAL_LOG_FILE_DIRECTORY,directory.path+"/fileLogs")


        //creating configuration
        val config = Config.Builder(directory.path)
            .setDefaultTag("TAG")
            .setLogcatEnable(true)
            .setDataFormatterPattern("dd-MM-yyyy-HH:mm:ss")
            .setStartupData(
                mapOf(
                    "Application Name" to sharedPreferenceUtil.getPreference(SpConstants.APP_NAME,""),
                    "Application Id" to sharedPreferenceUtil.getPreference(SpConstants.APP_ID,""),
                    "Application Version Code" to sharedPreferenceUtil.getPreference(SpConstants.APP_VERSION_CODE,""),
                    "Application Version Name" to sharedPreferenceUtil.getPreference(SpConstants.APP_VERSION_NAME,""),
                    "Device" to Build.DEVICE,
                    "Serial" to Build.SERIAL,
                )
            ).build()

        //init the file logger
        FileLogger.init(config)
    }

    override fun startLogging() {
        logWorkerStarter.invoke()
    }

    override fun stopLogging() {
        logWorkerStarter.stopLogWorker()
    }

    override fun logData(tag: String, message: String) {
        FileLogger.e(tag, message)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}