package com.shellinfo.common.code.ota

import abbasi.android.filelogger.FileLogger
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Process
import android.util.Log
import com.shellinfo.common.R
import com.shellinfo.common.code.ShellInfoLibrary
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.utils.SpConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.solrudev.ackpine.DisposableSubscriptionContainer
import ru.solrudev.ackpine.installer.parameters.InstallParameters
import ru.solrudev.ackpine.installer.parameters.InstallerType
import ru.solrudev.ackpine.session.Session
import ru.solrudev.ackpine.session.SessionResult
import ru.solrudev.ackpine.session.await
import ru.solrudev.ackpine.session.parameters.Confirmation
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlin.system.exitProcess

@Singleton
class OtaInstaller @Inject constructor(
    private val context:Context,
    private val spUtil: SharedPreferenceUtil
){

    private val TAG = this.javaClass.simpleName




    suspend fun installPackage(uri:File): Boolean {

        val packageInstallerNew = ru.solrudev.ackpine.installer.PackageInstaller.getInstance(ShellInfoLibrary.globalActivityContext)

        // Step 1: Get the URI of the APK file
        val apkUri: Uri = Uri.fromFile(uri)

        // Step 2: Configure installation parameters
        val installParameters = InstallParameters.Builder(apkUri)
            .setRequireUserAction(false) // Silent install (if possible)
            .setInstallerType(InstallerType.DEFAULT)
            .setConfirmation(Confirmation.IMMEDIATE)
            .build()


        try {

            val restartIntent = Intent(context, AppRestartReceiver::class.java)
            context.sendBroadcast(restartIntent)

            spUtil.savePreference(SpConstants.IS_APP_UPDATED,true)

            packageInstallerNew.createSession(installParameters).await()

//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    val result = packageInstallerNew.createSession(installParameters).await()
//                    Log.d("UpdateInstaller", "Installation session result: $result")
//                    // Proceed with app restart logic here
//                } catch (e: Exception) {
//                    Log.e("UpdateInstaller", "Error during installation session", e)
//                }
//            }
//            val session = packageInstallerNew.createSession(installParameters)
//
//            session.addProgressListener(DisposableSubscriptionContainer()){
//                    sessionId, progress ->
//                    Log.e("Progress", ""+progress.progress)
//            }

//            when (packageInstallerNew.createSession(installParameters).await()) {
//
//                is SessionResult.Success->{
//                    Log.e("Success","Success")
//                }
//                is SessionResult.Error->{
//                    Log.e("Error","Error")
//                }
//
//                else ->{
//
//                    Log.e("Error","Else")
//                    // Restart the app after logging (optional)
//                    val intent = Intent(ShellInfoLibrary.globalActivityContext, ShellInfoLibrary.globalActivityContext::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                    ShellInfoLibrary.globalActivityContext.startActivity(intent)
//
//                    // Kill the current process to avoid the system's default crash dialog
//                    Process.killProcess(Process.myPid())
//                    exitProcess(0)
//                }
//            }

        } catch (cancellationException: CancellationException) {
            println("Cancelled")
            Log.e("Error","cancellationException")
            throw cancellationException
        } catch (exception: Exception) {
            Log.e("Error","exception")
            println(exception)
        }

//        val packageInstaller = io.github.solrudev.simpleinstaller.PackageInstaller
//        runBlocking {
//            val result =packageInstaller.installPackage(uri){
//                confirmationStrategy = ConfirmationStrategy.IMMEDIATE
//                notification {
//                    title = "Notification title"
//                    contentText = "Notification text"
//                }
//            }
//
//            when (result) {
//                InstallResult.Success -> println("Install succeeded.")
//                is InstallResult.Failure -> println(result.cause)
//            }
//        }


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