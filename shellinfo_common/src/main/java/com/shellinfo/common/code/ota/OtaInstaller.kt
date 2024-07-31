package com.shellinfo.common.code.ota

import abbasi.android.filelogger.FileLogger
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.util.Log
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OtaInstaller @Inject constructor(
    private val context:Context
){

    private val TAG = this.javaClass.simpleName

    fun installNewApk(apkFile: File) {

        try{

            val packageInstaller = context.packageManager.packageInstaller
            val packageInstallerSessionParams = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
            packageInstallerSessionParams.setAppPackageName("com.shell.transitapp")
            val sessionId = packageInstaller.createSession(packageInstallerSessionParams)
            val session = packageInstaller.openSession(sessionId)

            val inputStream = FileInputStream(apkFile)
            val outputStream = session.openWrite("COSU", 0, -1)

            val buffer = ByteArray(65536)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            session.fsync(outputStream)
            inputStream.close()
            outputStream.close()


            val intent = Intent(context, this::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //val intent = Intent(context, context.javaClass)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            session.commit(pendingIntent.intentSender)

            FileLogger.i("APK INSTALLATION SUCCESS >>> ","")
//            val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
//            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE,true)
//            intent.putExtra(Intent.EXTRA_ALLOW_REPLACE,true)
//            intent.setDataAndType(FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", apkFile), "application/vnd.android.package-archive");
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            context.startActivity(intent)

//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.data = Uri.parse("package:${context.packageName}")
//            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//            context.startActivity(intent)

        }catch (ex:Exception){

            //Logcat Logging
            FileLogger.e("APK INSTALLATION FAILED >>> ","")
            ex.printStackTrace()

            //File Logging for Fail Case
            FileLogger.e(TAG, "APK INSTALLATION FAILED >>> ", ex.cause)
        }

    }

    /**
     * Method to start the newly installed apk
     */
    fun startInstalledApk(packageName: String) {
        // Check if the application is installed
        if (isPackageInstalled(packageName)) {
            // Create an Intent to launch the installed application
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            intent?.let {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(it)
            }
        } else {
            // The application is not installed
            // You may want to handle this case accordingly
        }
    }

    /**
     * Method to check if the package is installed already
     */
    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}