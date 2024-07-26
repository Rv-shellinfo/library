package com.shellinfo.common.code.logs

import abbasi.android.filelogger.FileLogger
import abbasi.android.filelogger.config.Config
import android.os.Build
import android.os.Environment
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.shell.transitapp.utils.workers.LogWorkerStarter
import com.shellinfo.common.code.ConfigMaster
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.utils.SpConstants
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class LoggerImpl @Inject constructor(
    private val sharedPreferenceUtil: SharedPreferenceUtil,
    private val logWorkerStarter: LogWorkerStarter
):LoggerInterface, Configuration.Provider {

    private val TAG = LoggerImpl::class.java.simpleName

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var masterConfig: ConfigMaster

    override fun initLogger() {

        //time stamp
        val timestampFormat = SimpleDateFormat("dd-MM-yyyy-HH:mm:ss", Locale.US)


        //device serial
        val deviceSerial = sharedPreferenceUtil.getPreference(SpConstants.DEVICE_SERIAL,"")

        //current date
        val currentDate = timestampFormat.format(Date())

        //log file name to create
        val logFileName = "log_${masterConfig.log_local_file_name}_${deviceSerial}_${currentDate}.txt"

        //creating directory
        val directoryPath = Environment.getExternalStorageDirectory().absolutePath + masterConfig.log_local_file_path

        // Create the directory if it doesn't exist
        val directory = File(directoryPath)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        //saving the file name in the preferences
        sharedPreferenceUtil.savePreference(SpConstants.LOCAL_LOG_FILE_DIRECTORY,directory.path)


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
                    "Device Type" to sharedPreferenceUtil.getPreference(SpConstants.DEVICE_TYPE,""),
                    "Serial" to sharedPreferenceUtil.getPreference(SpConstants.DEVICE_SERIAL,""),
                )
            ).build()


        //init the file logger
        FileLogger.init(config)

        //enable logging
        FileLogger.setEnable(true)
    }

    override fun startLogging(localLogs:Boolean, serverLogs:Boolean) {

        //if both logs needs to enable
        if(localLogs && serverLogs){

            //init logger
            initLogger()

            //start logging worker to upload the log file to FTP server
            logWorkerStarter.invoke()

        }else if(localLogs){

            //init logger
            initLogger()
        }



    }

    override fun stopLogging(localLogs:Boolean, serverLogs:Boolean) {

        //if both logs needs to stop
        if(localLogs && serverLogs){

            FileLogger.setEnable(false)

            //stop log worker
            logWorkerStarter.stopLogWorker()

        }else if(!localLogs && serverLogs){

            //stop log worker
            logWorkerStarter.stopLogWorker()
        }
    }

    override fun logData(tag: String, message: String) {

        //log normal data
        FileLogger.d(tag, message)
    }

    override fun logError(tag: String, error: Throwable) {

        //log errors
        FileLogger.e(tag = tag, error)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}