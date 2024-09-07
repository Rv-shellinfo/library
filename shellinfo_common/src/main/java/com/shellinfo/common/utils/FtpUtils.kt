package com.shellinfo.common.utils

import abbasi.android.filelogger.FileLogger
import android.os.Environment
import android.util.Log
import com.shellinfo.common.code.ConfigMaster
import com.shellinfo.common.code.enums.EquipmentType
import com.shellinfo.common.code.logs.LoggerImpl
import com.shellinfo.common.code.mqtt.MQTTManager
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FtpUtils @Inject constructor(
    private val sharedPreferenceUtil: SharedPreferenceUtil,
    private val loggerUtil: LoggerImpl,
    private val master: ConfigMaster
) {

    private val TAG = this::class.java.simpleName

    //host ip address
    private val host = master.ftp_ip_address

    //host port number
    private val port = master.ftp_port

    //ftp user
    private val ftpUser = master.ftp_user

    //ftp password
    private val ftpPassword = master.ftp_pass

    //application type
    lateinit var equipmentType: String

    //method to upload the log files to SC
    fun uploadFileToSc() {

        //get equipment type
        equipmentType= sharedPreferenceUtil.getPreference(SpConstants.DEVICE_TYPE,"")


        //ftp client object creation
        val ftpClient = FTPClient()

        //local path of the log file which needs to be uploaded on the SC
        val logPath = sharedPreferenceUtil.getPreference(SpConstants.LOCAL_LOG_FILE_DIRECTORY, "")


        //time stamp
        val timestampFormat = SimpleDateFormat("dd-MM-yyyy-HH:mm:ss", Locale.US)

        //time stamp 2 for ftp server folder date wise
        val timestampFormat2 = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val ftpFolderDate = timestampFormat2.format(Date())

        //device serial
        val deviceSerial = sharedPreferenceUtil.getPreference(SpConstants.DEVICE_SERIAL, "")

        //current date
        val currentDate = timestampFormat.format(Date())

        //server path
        val serverLogPath = ""

        //server file name to store
        val serverFileName= "/home/otauser/logs/$equipmentType/$deviceSerial/$ftpFolderDate/${currentDate}.txt"

        FileLogger.i("Server Log Path",serverLogPath)


        //get all files from local directory
        val directory = File(logPath)
        val filesInDirectory =  directory.listFiles() ?: arrayOf()


        try {

            //connect to the host
            ftpClient.connect(host,port.toInt())

            //if able to login then start uploading
            if(ftpClient.login(ftpUser,ftpPassword)){

                FileLogger.i("FTP LOGIN>>"," FTP LOGIN SUCCESS")

                ftpClient.enterLocalPassiveMode()
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE)

                //upload every file on server
                for (file in filesInDirectory) {

                    FileLogger.i("File Path to upload", file.absolutePath)

                    FileLogger.i("File name to upload", file.name)

                    val inputStream = FileInputStream(file)
                    val isUploaded= ftpClient.storeFile(serverFileName,inputStream)
                    inputStream.close()

                    if(isUploaded){
                        FileLogger.i("File uploaded success"," File Uploaded successfully")
                    }
                }


                //logout from ftpClient
                ftpClient.logout()

                //delete local log file
                FileLogger.deleteFiles()

                //create new log file
                //loggerUtil.initLogger(LoggerConfig())

            }else{

                FileLogger.e("FTP","FTP SERVER LOGIN FAILED FOR UPLOAD LOGS")
            }

        } catch (e: Exception) {

            //logging out from ftp server
            ftpClient.logout()

            e.printStackTrace()

            //Adding error in log file
            FileLogger.e("Log_File_Upload_Error", e)

        } finally {
            if (ftpClient.isConnected) {
                try {

                    //disconnect from ftp server
                    ftpClient.disconnect()
                } catch (e: Exception) {
                    e.printStackTrace()

                    //Adding error in log file
                    FileLogger.e("Log_File_Upload_Error_2", e)

                    //error handling must be done with all scenarios
                }
            }
        }
    }

    /**
     * Method to download updated apk file from FTP Server i.e. CC/SC
     */
    fun downloadUpdatedApk(filePath: String?, fileName: String?, newVersion: Int, downloadHandler: (String) -> Unit) {

        //current version
        val currentVersion = sharedPreferenceUtil.getPreference(SpConstants.APP_VERSION_CODE,0)

        //local file path
        val localFilePath = fileName

        //logging the file path
        FileLogger.d(TAG, "Local file >$localFilePath")


        //remote file path
        val remoteFilePath = filePath + fileName



        //check current version and new version / if new version number bigger then download new version
        if (currentVersion < newVersion) {

            FileLogger.d(TAG, "Older version found, Updated version starting to download")

            //creating ftp client for download
            val ftpClient = FTPClient()

            try {
                ftpClient.connect("$host:$port")

                if(ftpClient.login(ftpUser,ftpPassword)){

                    FileLogger.d(TAG,"FTP login success for download apk")

                    ftpClient.enterLocalPassiveMode()
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE)

                    val folder = File(Environment.getExternalStorageDirectory(),"/transit_app_build")

                    // Create the folder if it doesn't exist
                    if (!folder.exists()) {
                        folder.mkdirs()
                    }

                    //create local file if not exist and delete all other apk files
                    val localFile= File(folder,localFilePath!!)

                    //if already exist then delete the file and create new else create new
                    if(localFile.exists()){

                        val deleted= localFile.delete()

                        if(deleted){

                            FileLogger.i("FTP","Local existing apk file deleted")

                            // File does not exist, create it
                            val created = localFile.createNewFile()

                            if(created){
                                FileLogger.i("FTP","Local new apk file created")
                            }
                        }
                    }else{

                        localFile.createNewFile()

                        FileLogger.i("FTP","Local new apk file created")

                    }

                    val outputStream = FileOutputStream(localFile)
                    val success = ftpClient.retrieveFile(remoteFilePath, outputStream)
                    outputStream.close()

                    if (success) {
                        println("File downloaded successfully.")

                        //saving the file name in the preference
                        sharedPreferenceUtil.savePreference(SpConstants.NEW_APK_FILE_NAME,localFile.name)

                        //send downloaded apk for install
                        downloadHandler(localFile.absolutePath)

                        FileLogger.i(
                            "UPDATED_APK_DOWNLOAD_SUCCESS",
                            "Server File Path :$remoteFilePath"
                        )
                        FileLogger.i("UPDATED_APK_DOWNLOAD_SUCCESS", "Local File Path :$localFilePath")
                    } else {
                        println("Failed to download file.")

                        FileLogger.e("UPDATED_APK_DOWNLOAD_ERROR", "Server File Path :$remoteFilePath")
                        FileLogger.e("UPDATED_APK_DOWNLOAD_ERROR", "Local File Path :$localFilePath")
                    }
                }else{

                    FileLogger.i("FTP","FTP SERVER LOGIN FAILED FOR DOWNLOAD APK")
                }



            } catch (e: Exception) {
                e.printStackTrace()

                FileLogger.e("APK_DOWNLOAD_EXCEPTION", e)

            } finally {
                if (ftpClient.isConnected) {
                    try {
                        ftpClient.disconnect()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        FileLogger.e("APK_DOWNLOAD_EXCEPTION_2", e)
                    }
                }
            }

        } else {

            FileLogger.e(
                "DOWNLOAD_NEW_VERSION",
                "new version number is lesser then installed version"
            )

        }
    }

}