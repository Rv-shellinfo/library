package com.shellinfo.common.code.mqtt.topic_handler

import com.shellinfo.common.code.ota.ApkDownloadWorkerStarter
import com.shellinfo.common.code.ota.OtaInstaller
import com.shellinfo.common.data.local.data.mqtt.OtaUpdateMessage
import com.shellinfo.common.utils.FtpUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MqttOtaHandler @Inject constructor() {

    @Inject
    lateinit var ftpUtils: FtpUtils

    @Inject
    lateinit var installer: OtaInstaller

    @Inject
    lateinit var apkDownloadWorkerStarter: ApkDownloadWorkerStarter

    /**
     * Method to handle the OTA
     */
    fun handleOta(otaData:OtaUpdateMessage){

    }

    private fun downloadBuild(otaData:OtaUpdateMessage){

        ftpUtils.downloadUpdatedApk(otaData)
    }
}