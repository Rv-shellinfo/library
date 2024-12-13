package com.shellinfo.common.code.ota

import abbasi.android.filelogger.FileLogger
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import com.shellinfo.common.R
import com.shellinfo.common.code.ShellInfoLibrary
import io.github.solrudev.simpleinstaller.apksource.ApkSource
import io.github.solrudev.simpleinstaller.data.ConfirmationStrategy
import io.github.solrudev.simpleinstaller.data.InstallResult
import io.github.solrudev.simpleinstaller.data.notification
import io.github.solrudev.simpleinstaller.installPackage
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class OtaInstaller @Inject constructor(
    private val context:Context
){

    private val TAG = this.javaClass.simpleName


    @Throws(IOException::class)
    fun installPackage( inputStream: InputStream, packageName: String, uri:File): Boolean {

        val packageInstaller = io.github.solrudev.simpleinstaller.PackageInstaller
        runBlocking {
            val result =packageInstaller.installPackage(uri){
                confirmationStrategy = ConfirmationStrategy.IMMEDIATE
                notification {
                    title = "Notification title"
                    contentText = "Notification text"
                }
            }

            when (result) {
                InstallResult.Success -> println("Install succeeded.")
                is InstallResult.Failure -> println(result.cause)
            }
        }


//        val installParams:InstallParameters =InstallParameters()
//
//
//        try {
//            when (val result = packageInstaller.createSession(apkUri).await()) {
//                Session.State.Succeeded -> println("Success")
//                is Session.State.Failed -> println(result.failure.message)
//            }
//        } catch (cancellationException: CancellationException) {
//            println("Cancelled")
//            throw cancellationException
//        } catch (exception: Exception) {
//            println(exception)
//        }
//
//
//        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
//        params.setAppPackageName(packageName)
//
//        // Create a session
//        val sessionId = packageInstaller.createSession(params)
//        val session = packageInstaller.openSession(sessionId)
//
//        // Write the APK data to the session
//        session.openWrite("COSU", 0, -1).use { outputStream ->
//            val buffer = ByteArray(65536)
//            var bytesRead: Int
//            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
//                outputStream.write(buffer, 0, bytesRead)
//            }
//            session.fsync(outputStream)
//        }
//
//        inputStream.close()
//
//        // Create an intent for the result of the installation
//        val intent = Intent(context, ShellInfoLibrary.globalActivityContext::class.java).apply {
//            putExtra("info", "somedata") // Add extra data if needed
//        }
//
//        val randomId = (0 until Int.MAX_VALUE).random()
//        val pendingIntent = PendingIntent.getActivity(
//            context,
//            randomId,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        // Commit the session
//        session.commit(pendingIntent.intentSender)

        return true
    }



    fun installNewApk(apkFile: File) {

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            if (!context.packageManager.canRequestPackageInstalls()) {
//                // Request permission to install unknown apps
//                val intent = Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
//                intent.data = Uri.parse("package:${context.packageName}")
//                context.startActivity(intent)
//                return
//            }
//        }

        try {
            // Use PackageInstaller for API 21 and above
            val packageInstaller = context.packageManager.packageInstaller
            val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)

            val packageInfo = context.packageManager.getPackageArchiveInfo(apkFile.absolutePath, 0)
            val packageName = packageInfo?.packageName ?: throw IllegalArgumentException("Cannot retrieve package name")
            params.setAppPackageName(packageName)

            val sessionId = packageInstaller.createSession(params)
            val session = packageInstaller.openSession(sessionId)

            // Write APK file to the session
            val inputStream = FileInputStream(apkFile)
            val outputStream = session.openWrite("APK_INSTALL", 0, -1)
            val buffer = ByteArray(65536)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            session.fsync(outputStream)
            inputStream.close()
            outputStream.close()

            // Commit session
            val intent = Intent(context, OtaReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            session.commit(pendingIntent.intentSender)
        } catch (e: Exception) {
            e.printStackTrace()
            //Toast.makeText(context, "Error installing APK: ${e.message}", Toast.LENGTH_SHORT).show()
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