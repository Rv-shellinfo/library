package com.shellinfo.common.code.mqtt.topic_handler

import abbasi.android.filelogger.FileLogger
import com.shellinfo.common.code.ota.ApkDownloadWorkerStarter
import com.shellinfo.common.code.ota.OtaInstaller
import com.shellinfo.common.data.local.data.mqtt.OtaUpdateMessage
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.utils.FtpUtils
import com.shellinfo.common.utils.SpConstants
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MqttOtaHandler @Inject constructor(
    private val ftpUtils: FtpUtils,
    private val installer: OtaInstaller,
    private val apkDownloadWorkerStarter: ApkDownloadWorkerStarter,
    private val sharedPreferenceUtil: SharedPreferenceUtil
) {


    /**
     * Method to handle the OTA
     */
    fun handleOta(otaData:OtaUpdateMessage){

        //compare version before downloading and installing the new version
        if(sharedPreferenceUtil.getPreference(SpConstants.APP_SERVER_VERSION,1) < otaData.version.toInt()){
            //download the updated build
            downloadBuild(otaData)
        }else{
            FileLogger.d("OTA","LATEST APP ALREADY INSTALLED")
        }
    }

    //download new build
    private fun downloadBuild(otaData:OtaUpdateMessage){

        apkDownloadWorkerStarter.invoke(otaData.ftpPath,otaData.fileName,otaData.version.toInt())
    }
}