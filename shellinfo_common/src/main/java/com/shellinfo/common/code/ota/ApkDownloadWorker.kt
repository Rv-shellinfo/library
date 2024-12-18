package com.shellinfo.common.code.ota

import abbasi.android.filelogger.FileLogger
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.shellinfo.common.code.ShellInfoLibrary
import com.shellinfo.common.utils.FtpUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.IOException

@HiltWorker
class ApkDownloadWorker @AssistedInject constructor(
    private val ftpUtils: FtpUtils,
    private val otaInstaller: OtaInstaller,
    @Assisted context: Context,
    @Assisted params : WorkerParameters
): CoroutineWorker(context,params) {

    override suspend fun doWork(): Result {

        FileLogger.d("Download Worker Started","Worker Started to Upload the Logs")

        return withContext(Dispatchers.IO){

            //apk file name to download
            val fileName= inputData.getString("apkFileName")

            //version of the new apk to cross check the installed version and new version
            val version= inputData.getInt("newVersion", 1)

            //server path from where apk will be downloaded
            val serverPath= inputData.getString("serverPath")


            ftpUtils.downloadUpdatedApk(filePath = serverPath, fileName = fileName, newVersion = version) {

                //file path of the newly downloaded apk file
                val apkFilePath = it

                //check if file exist or not
                val fileNew = File(apkFilePath)
                if (fileNew.exists()) {

                    try {
                        //val inputStream = FileInputStream(fileNew)

                        runBlocking {
                            otaInstaller.installPackage(fileNew)
                        }

                        //installPackage(this, inputStream, "com.example.apk")
                    } catch (e: IOException) {
                        //Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }

                    //otaInstaller.installNewApk(fileNew)

                } else {

                    FileLogger.e("File Not Exist", "Downloaded file $apkFilePath not exist")

                    Result.failure()
                }
            }
            Result.success()
        }
    }
}