package com.shellinfo.common.code.ota

import abbasi.android.filelogger.FileLogger
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.shellinfo.common.BuildConfig
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.utils.Constants
import com.shellinfo.common.utils.SpConstants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OtaReceiver @Inject constructor(
    private val otaInstaller: OtaInstaller,
    private val sharedPreferenceUtil: SharedPreferenceUtil
): BroadcastReceiver(){

    private val TAG = this.javaClass.simpleName

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_MY_PACKAGE_REPLACED) {

            //get the application id
            val appId= sharedPreferenceUtil.getPreference(SpConstants.APP_ID,"")

            // Your package has been replaced with an updated version
            FileLogger.d(TAG, "Package updated")

            otaInstaller.startInstalledApk(appId)
            // Start the necessary activity or service after the update
            // For example: startMainActivity(context)
        }
    }
}